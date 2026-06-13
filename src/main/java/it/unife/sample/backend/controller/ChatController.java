package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
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
 * e assegna i badge in base al punteggio aggiornato.
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
    private final BadgeRepository badgeRepo;
    private final BadgeOttenutoRepository badgeOttenutoRepo;

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

    /**
     * PUT /api/chat/{id}/completa
     * Completa uno scambio. È l'endpoint più ricco di logica di business:
     * 1. Imposta stato chat → completata
     * 2. Chiude l'annuncio di interesse
     * 3. Calcola CO₂ risparmiata (prezzo stimato × 0.032 kg/€)
     * 4. Aggiorna co2_totale e punteggio di entrambi gli utenti (+48 punti ciascuno)
     * 5. Assegna badge automaticamente se le soglie sono state raggiunte
     * 6. Restituisce l'ID dell'altro utente per aprire il modal recensione nel frontend
     *
     * @param id        ID della chat da completare
     * @param idUtente  ID dell'utente che ha premuto "Completa" dall'header
     * @return Map con chat aggiornata, co2_risparmiata, id_altro_utente
     */
    @PutMapping("/{id}/completa")
    public ResponseEntity<?> completa(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {

        // Verifica esistenza della chat
        Chat chat = chatRepo.findById(id).orElse(null);
        if (chat == null) return ResponseEntity.notFound().build();

        // Imposta stato completata e timestamp di completamento
        chat.setStatoChat(Chat.StatoChat.completata);
        chat.setDataCompletamento(LocalDateTime.now());
        chatRepo.save(chat);

        // Recupera l'annuncio di interesse dalla proposta collegata e lo chiude
        Annuncio annuncioInteresse = chat.getPropostaGenerante().getAnnuncioInteresse();
        annuncioInteresse.setStatoAnnuncio(Annuncio.StatoAnnuncio.chiuso);
        annuncioRepo.save(annuncioInteresse);

        // Calcola CO₂ risparmiata: formula empirica = prezzo stimato × 0.032
        // Ogni euro di valore scambiato equivale a ~0.032 kg di CO₂ risparmiata
        // (evita la produzione di un bene nuovo equivalente)
        BigDecimal prezzoTotale   = annuncioInteresse.getPrezzoStimato();
        BigDecimal co2Risparmiata = prezzoTotale.multiply(new BigDecimal("0.032"));

        // Recupera il pubblicante dell'annuncio e aggiorna CO₂ e punteggio
        UtenteRegistrato pubblicante = annuncioInteresse.getPubblicante();
        pubblicante.setCo2Totale(pubblicante.getCo2Totale().add(co2Risparmiata));
        pubblicante.setPunteggio(pubblicante.getPunteggio() + 48); // +48 punti per scambio completato
        utenteRepo.save(pubblicante);
        assegnaBadge(pubblicante); // Verifica e assegna eventuali nuovi badge

        // Recupera il proponente e aggiorna CO₂ e punteggio
        UtenteRegistrato proponente = chat.getPropostaGenerante().getProponente();
        proponente.setCo2Totale(proponente.getCo2Totale().add(co2Risparmiata));
        proponente.setPunteggio(proponente.getPunteggio() + 48);
        utenteRepo.save(proponente);
        assegnaBadge(proponente); // Verifica e assegna eventuali nuovi badge

        // Determina l'ID dell'altro utente per il modal recensione nel frontend
        // (chi ha completato la chat deve recensire l'altro)
        Long idAltroUtente = idUtente.equals(pubblicante.getIdUtenteReg())
                ? proponente.getIdUtenteReg()
                : pubblicante.getIdUtenteReg();

        // Restituisce un oggetto composito con tutti i dati necessari al frontend
        return ResponseEntity.ok(Map.of(
            "chat",           chat,
            "co2_risparmiata", co2Risparmiata,
            "id_altro_utente", idAltroUtente
        ));
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
     * Riattiva l'annuncio di interesse (torna disponibile per altre proposte).
     * La chat passa in stato "annullata" e non accetta più messaggi.
     *
     * @param id  ID della chat da annullare
     * @return 200 con la chat aggiornata, 404 se non trovata
     */
    @PutMapping("/{id}/annulla")
    public ResponseEntity<?> annulla(@PathVariable Long id) {
        return chatRepo.findById(id).map(chat -> {

            // Imposta stato annullata
            chat.setStatoChat(Chat.StatoChat.annullata);
            chatRepo.save(chat);

            // Riattiva l'annuncio di interesse — torna disponibile per nuove proposte
            Annuncio ann = chat.getPropostaGenerante().getAnnuncioInteresse();
            ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
            annuncioRepo.save(ann);

            return ResponseEntity.ok(chat);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Metodo privato: verifica e assegna badge all'utente in base al punteggio attuale.
     * Viene chiamato ogni volta che un utente completa uno scambio.
     * Controlla tutti i badge disponibili e assegna solo quelli non ancora ottenuti
     * la cui soglia_punti è stata raggiunta o superata.
     *
     * @param utente  L'utente a cui eventualmente assegnare i badge
     */
    private void assegnaBadge(UtenteRegistrato utente) {

        // Recupera tutti i badge definiti nella piattaforma
        List<Badge> tuttiBadge = badgeRepo.findAll();

        // Recupera i badge già ottenuti da questo utente
        List<BadgeOttenuto> badgeGiaOttenuti = badgeOttenutoRepo
                .findById_IdUtenteReg(utente.getIdUtenteReg());

        // Estrae solo i nomi dei badge già ottenuti per un confronto efficiente
        List<String> nomiBadgeGiaOttenuti = badgeGiaOttenuti.stream()
                .map(b -> b.getBadge().getNomeBadge())
                .toList();

        // Per ogni badge disponibile, verifica se va assegnato
        for (Badge badge : tuttiBadge) {

            // Assegna solo se: non ancora ottenuto AND punteggio sufficiente
            if (!nomiBadgeGiaOttenuti.contains(badge.getNomeBadge())
                    && utente.getPunteggio() >= badge.getSogliaPunti()) {

                // Costruisce la chiave composita (id_utente_reg, nome_badge)
                BadgeOttenuto.BadgeOttenutoId badgeId = new BadgeOttenuto.BadgeOttenutoId();
                badgeId.setIdUtenteReg(utente.getIdUtenteReg());
                badgeId.setNomeBadge(badge.getNomeBadge());

                // Crea e salva la relazione utente ↔ badge
                BadgeOttenuto badgeOttenuto = new BadgeOttenuto();
                badgeOttenuto.setId(badgeId);
                badgeOttenuto.setUtente(utente);
                badgeOttenuto.setBadge(badge);
                badgeOttenutoRepo.save(badgeOttenuto);
            }
        }
    }
}