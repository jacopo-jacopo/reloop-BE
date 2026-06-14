package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import it.unife.sample.backend.service.BadgeService;
import it.unife.sample.backend.service.ClimatiqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per le chat tra utenti.
 * Gestisce messaggi, completamento e annullamento degli scambi.
 * Al completamento calcola automaticamente la CO₂ risparmiata
 * e delega a {@link BadgeService} l'assegnazione di eventuali nuovi badge.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    // Repository necessari per la gestione delle chat
    private final ChatRepository chatRepo;
    private final MessaggioRepository messaggioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AnnuncioRepository annuncioRepo;
    private final BadgeService badgeService;
    private final ClimatiqService climatiqService;

    /**
     * GET /api/chat
     * Restituisce tutte le chat dell'utente loggato.
     * Include sia le chat dove è proponente sia quelle dove è pubblicante.
     * La query JPQL nel repository gestisce entrambi i casi.
     *
     * @param idUtente  ID dell'utente loggato dall'header
     * @return Lista di chat dell'utente (tutti gli stati)
     */
    @GetMapping
    public List<Chat> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return chatRepo.findByUtente(idUtente);
    }

    /**
     * GET /api/chat/{id}
     * Restituisce una singola chat per ID.
     * Usato per ricaricare i dati aggiornati di una chat specifica.
     *
     * @param id  ID della chat
     * @return 200 con la chat, 404 se non trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getById(@PathVariable Long id) {
        return chatRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/chat/{id}/messaggi
     * Restituisce tutti i messaggi di una chat ordinati per data di invio (ASC).
     * Usato dal polling ogni 3 secondi nel componente chat Angular.
     *
     * @param id  ID della chat
     * @return Lista di messaggi ordinata cronologicamente
     */
    @GetMapping("/{id}/messaggi")
    public List<Messaggio> getMessaggi(@PathVariable Long id) {
        return messaggioRepo.findByIdChatOrderByDataInvio(id);
    }

    /**
     * POST /api/chat/{id}/messaggi
     * Invia un messaggio in una chat aperta.
     * Verifica che la chat sia in stato "aperta" prima di procedere.
     * L'ID del messaggio è calcolato come MAX(id_messaggio) + 1 per quella chat
     * (chiave primaria composita: id_messaggio + id_chat).
     *
     * @param id        ID della chat
     * @param body      Map con "contenuto" del messaggio
     * @param idUtente  ID del mittente dall'header
     * @return 200 con il messaggio salvato, 400 se chat non aperta o utente non trovato
     */
    @PostMapping("/{id}/messaggi")
    public ResponseEntity<?> inviaMessaggio(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") Long idUtente) {

        // Verifica esistenza della chat
        Chat chat = chatRepo.findById(id).orElse(null);
        if (chat == null) return ResponseEntity.notFound().build();

        // Vincolo di business: i messaggi sono inviabili solo in chat aperte
        if (chat.getStatoChat() != Chat.StatoChat.aperta)
            return ResponseEntity.badRequest().body("Chat non aperta");

        // Verifica esistenza del mittente
        UtenteRegistrato mittente = utenteRepo.findById(idUtente).orElse(null);
        if (mittente == null) return ResponseEntity.badRequest().body("Utente non trovato");

        // Calcola il prossimo ID del messaggio (chiave composita incrementale per chat)
        Long maxId = messaggioRepo.findMaxIdByIdChat(id);
        Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
        msgId.setIdMessaggio(maxId + 1);
        msgId.setIdChat(id);

        // Costruisce e salva il messaggio
        Messaggio msg = new Messaggio();
        msg.setId(msgId);
        msg.setChat(chat);
        msg.setContenuto(body.get("contenuto"));
        msg.setMittente(mittente);
        // flag_lettura inizialmente false — da implementare in futuro con WebSocket

        return ResponseEntity.ok(messaggioRepo.save(msg));
    }

    // Suffisso del messaggio di sistema inserito quando un utente conferma il completamento.
    // Usato sia per generare il messaggio sia per riconoscere le conferme già registrate.
    private static final String CONFERMA_SUFFIX = "ha confermato che lo scambio è stato completato";

    /**
     * PUT /api/chat/{id}/completa
     * Registra la conferma di completamento dell'utente. Lo scambio si considera
     * concluso solo quando ENTRAMBI gli utenti coinvolti hanno confermato:
     * 1. Aggiunge un messaggio di sistema che segnala chi ha confermato
     * 2. Se è la prima conferma: la chat resta "aperta", in attesa dell'altro utente
     * 3. Se è la seconda conferma (entrambi hanno confermato):
     *    - Imposta stato chat → completata
     *    - Chiude l'annuncio di interesse
     *    - Calcola CO₂ risparmiata (prezzo stimato × 0.032 kg/€)
     *    - Aggiorna co2_totale e punteggio di entrambi gli utenti (+50 punti ciascuno)
     *    - Assegna badge automaticamente se le soglie sono state raggiunte
     *
     * @param id        ID della chat da completare
     * @param idUtente  ID dell'utente che ha premuto "Completa" dall'header
     * @return Map con chat aggiornata, completato (bool) e, se completato,
     *         co2_risparmiata e id_altro_utente
     */
    @PutMapping("/{id}/completa")
    public ResponseEntity<?> completa(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {

        // Verifica esistenza della chat
        Chat chat = chatRepo.findById(id).orElse(null);
        if (chat == null) return ResponseEntity.notFound().build();

        if (chat.getStatoChat() != Chat.StatoChat.aperta)
            return ResponseEntity.badRequest().body("Chat non aperta");

        UtenteRegistrato utente = utenteRepo.findById(idUtente).orElse(null);
        if (utente == null) return ResponseEntity.badRequest().body("Utente non trovato");

        Annuncio annuncioInteresse = chat.getPropostaGenerante().getAnnuncioInteresse();
        UtenteRegistrato pubblicante = annuncioInteresse.getPubblicante();
        UtenteRegistrato proponente  = chat.getPropostaGenerante().getProponente();

        // Recupera le conferme già registrate tramite i messaggi di sistema
        List<Messaggio> messaggi = messaggioRepo.findByIdChatOrderByDataInvio(id);
        boolean confermaPubblicante = messaggi.stream().anyMatch(m ->
            m.getContenuto().endsWith(CONFERMA_SUFFIX)
            && m.getMittente().getIdUtenteReg().equals(pubblicante.getIdUtenteReg()));
        boolean confermaProponente = messaggi.stream().anyMatch(m ->
            m.getContenuto().endsWith(CONFERMA_SUFFIX)
            && m.getMittente().getIdUtenteReg().equals(proponente.getIdUtenteReg()));

        boolean giaConfermatoDaMe = idUtente.equals(pubblicante.getIdUtenteReg()) ? confermaPubblicante : confermaProponente;

        // Aggiunge il messaggio di sistema con la conferma dell'utente corrente (se non già fatto)
        if (!giaConfermatoDaMe) {
            Long maxId = messaggioRepo.findMaxIdByIdChat(id);
            Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
            msgId.setIdMessaggio(maxId + 1);
            msgId.setIdChat(id);

            Messaggio msg = new Messaggio();
            msg.setId(msgId);
            msg.setChat(chat);
            msg.setContenuto(utente.getNomeCompleto() + " " + CONFERMA_SUFFIX);
            msg.setMittente(utente);
            messaggioRepo.save(msg);

            if (idUtente.equals(pubblicante.getIdUtenteReg())) confermaPubblicante = true;
            else confermaProponente = true;
        }

        // Se solo uno dei due ha confermato, la chat resta aperta in attesa dell'altro
        if (!(confermaPubblicante && confermaProponente)) {
            return ResponseEntity.ok(Map.of(
                "chat",       chat,
                "completato", false
            ));
        }

        // Entrambi hanno confermato: completa lo scambio
        chat.setStatoChat(Chat.StatoChat.completata);
        chat.setDataCompletamento(LocalDateTime.now());
        chatRepo.save(chat);

        Annuncio annuncioOfferto = chat.getPropostaGenerante().getAnnunciOfferti().stream()
                .filter(AnnuncioIncluso::getFlagSelezionato)
                .map(AnnuncioIncluso::getAnnuncioOfferto)
                .findFirst()
                .orElseThrow();

        // Chiude entrambi gli annunci coinvolti nello scambio
        annuncioInteresse.setStatoAnnuncio(Annuncio.StatoAnnuncio.chiuso);
        annuncioRepo.save(annuncioInteresse);
        annuncioOfferto.setStatoAnnuncio(Annuncio.StatoAnnuncio.chiuso);
        annuncioRepo.save(annuncioOfferto);

        // Calcola la CO₂ risparmiata tramite l'API Climatiq per ciascuno dei due
        // oggetti scambiati (quello di interesse e quello offerto e selezionato),
        // in base a categoria e valore stimato. Se Climatiq non è disponibile,
        // si ricade sulla formula empirica: prezzo stimato × 0.032 kg/€.
        BigDecimal co2Interesse = calcolaCo2Annuncio(annuncioInteresse);
        BigDecimal co2Offerto   = calcolaCo2Annuncio(annuncioOfferto);
        BigDecimal co2Risparmiata = co2Interesse.add(co2Offerto);

        // Aggiorna CO₂ e punteggio del pubblicante
        pubblicante.setCo2Totale(pubblicante.getCo2Totale().add(co2Risparmiata));
        pubblicante.setPunteggio(pubblicante.getPunteggio() + 50); // +50 punti per scambio completato
        utenteRepo.save(pubblicante);
        badgeService.assegnaBadge(pubblicante); // Verifica e assegna eventuali nuovi badge

        // Aggiorna CO₂ e punteggio del proponente
        proponente.setCo2Totale(proponente.getCo2Totale().add(co2Risparmiata));
        proponente.setPunteggio(proponente.getPunteggio() + 50);
        utenteRepo.save(proponente);
        badgeService.assegnaBadge(proponente); // Verifica e assegna eventuali nuovi badge

        // Determina l'ID dell'altro utente per il modal recensione nel frontend
        // (chi ha completato la chat deve recensire l'altro)
        Long idAltroUtente = idUtente.equals(pubblicante.getIdUtenteReg())
                ? proponente.getIdUtenteReg()
                : pubblicante.getIdUtenteReg();

        // Restituisce un oggetto composito con tutti i dati necessari al frontend
        return ResponseEntity.ok(Map.of(
            "chat",            chat,
            "completato",      true,
            "co2_risparmiata", co2Risparmiata,
            "id_altro_utente", idAltroUtente
        ));
    }

    /**
     * Calcola la CO₂ risparmiata per un singolo annuncio scambiato,
     * tramite l'API Climatiq (con fallback alla formula empirica
     * prezzo stimato × 0.032 kg/€ se l'API non è disponibile).
     */
    private BigDecimal calcolaCo2Annuncio(Annuncio annuncio) {
        BigDecimal prezzo = annuncio.getPrezzoStimato();
        return climatiqService
                .stimaCo2Risparmiata(annuncio.getCategoria(), prezzo)
                .orElseGet(() -> prezzo.multiply(new BigDecimal("0.032")));
    }

    /**
     * GET /api/chat/non-letti
     * Restituisce due liste separate:
     * - messaggi_non_letti: chat con messaggi non letti inviati dall'altro utente
     * - chat_vuote: chat senza messaggi (appena create, non ancora aperte)
     * Separate perché il frontend le tratta diversamente (le chat vuote vengono
     * filtrate via localStorage dopo la prima apertura, le altre no).
     */
    @GetMapping("/non-letti")
    public Map<String, List<Long>> getNonLetti(@RequestHeader("X-User-Id") Long idUtente) {
        java.time.LocalDateTime ultimaVisitaChat = utenteRepo.findById(idUtente)
            .map(u -> u.getUltimaVisitaChat())
            .orElse(null);
        return Map.of(
            "messaggi_non_letti", messaggioRepo.findUnreadChatIdsByUtente(idUtente),
            "chat_vuote",         chatRepo.findVuoteByUtente(idUtente, ultimaVisitaChat)
        );
    }

    /**
     * PUT /api/chat/{id}/leggi
     * Segna come letti tutti i messaggi della chat non inviati dall'utente corrente.
     *
     * @param id        ID della chat
     * @param idUtente  ID dell'utente loggato dall'header
     */
    @PutMapping("/{id}/leggi")
    public ResponseEntity<?> leggi(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        messaggioRepo.markAsRead(id, idUtente);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/chat/{id}/annulla
     * Annulla uno scambio in corso.
     * Riattiva l'annuncio di interesse e l'annuncio offerto scelto (tornano "attivi"
     * e disponibili per nuove proposte). La chat passa in stato "annullata" e non
     * accetta più messaggi. Aggiunge un messaggio di sistema che segnala chi ha
     * annullato lo scambio.
     *
     * @param id        ID della chat da annullare
     * @param idUtente  ID dell'utente che ha annullato lo scambio, dall'header
     * @return 200 con la chat aggiornata, 404 se non trovata
     */
    @PutMapping("/{id}/annulla")
    public ResponseEntity<?> annulla(@PathVariable Long id, @RequestHeader("X-User-Id") Long idUtente) {
        return chatRepo.findById(id).map(chat -> {

            // Imposta stato annullata
            chat.setStatoChat(Chat.StatoChat.annullata);
            chatRepo.save(chat);

            // Riattiva l'annuncio di interesse — torna disponibile per nuove proposte
            Annuncio annuncioInteresse = chat.getPropostaGenerante().getAnnuncioInteresse();
            annuncioInteresse.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
            annuncioRepo.save(annuncioInteresse);

            // Riattiva l'annuncio offerto scelto — torna disponibile per nuove proposte
            chat.getPropostaGenerante().getAnnunciOfferti().stream()
                .filter(AnnuncioIncluso::getFlagSelezionato)
                .map(AnnuncioIncluso::getAnnuncioOfferto)
                .forEach(ann -> {
                    ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
                    annuncioRepo.save(ann);
                });

            // Messaggio di sistema: segnala chi ha annullato lo scambio
            UtenteRegistrato utente = utenteRepo.findById(idUtente).orElse(null);
            if (utente != null) {
                Long maxId = messaggioRepo.findMaxIdByIdChat(id);
                Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
                msgId.setIdMessaggio(maxId + 1);
                msgId.setIdChat(id);

                Messaggio msg = new Messaggio();
                msg.setId(msgId);
                msg.setChat(chat);
                msg.setContenuto(utente.getNomeCompleto() + " ha annullato lo scambio");
                msg.setMittente(utente);
                messaggioRepo.save(msg);
            }

            return ResponseEntity.ok(chat);
        }).orElse(ResponseEntity.notFound().build());
    }

}