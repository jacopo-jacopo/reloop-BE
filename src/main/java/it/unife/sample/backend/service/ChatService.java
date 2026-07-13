package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.*;
import it.unife.sample.backend.dto.request.InviaMessaggioRequest;
import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.dto.response.CompletaResponse;
import it.unife.sample.backend.dto.response.MessaggioResponse;
import it.unife.sample.backend.dto.response.NonLettiResponse;
import it.unife.sample.backend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// service per la gestione delle chat: fornisce metodi per ottenere le chat dell'utente loggato, ottenere i messaggi di una chat, 
// inviare un messaggio, completare una chat, ottenere il numero di messaggi non letti e segnare una chat come letta
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class ChatService {

    private final ChatDao chatDao;
    private final MessaggioDao messaggioDao;
    private final UtenteDao utenteDao;
    private final AnnuncioDao annuncioDao;
    private final BadgeService badgeService;
    private final ClimatiqService climatiqService;
    private final NotificaService notificaService;

    private static final String CONFERMA_SUFFIX = "ha confermato che lo scambio è stato completato";

    // metodo per ottenere le chat dell'utente loggato
    public List<ChatResponse> getMie(Long idUtente) {
        return chatDao.findByUtente(idUtente);
    }

    // metodo per ottenere una chat per ID
    public ChatResponse getById(Long id) {
        return chatDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // metodo per ottenere i messaggi di una chat per ID
    public List<MessaggioResponse> getMessaggi(Long idChat) {
        return messaggioDao.findByChat(idChat);
    }

    // metodo per inviare un messaggio in una chat: verifica che la chat sia aperta, invia il messaggio e crea una notifica per l'altro utente
    public MessaggioResponse inviaMessaggio(Long idChat, Long idMittente, InviaMessaggioRequest req) {
        ChatResponse chat = chatDao.findById(idChat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"aperta".equals(chat.getStatoChat())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chat non aperta");
        }
        MessaggioResponse risposta = messaggioDao.invia(idChat, idMittente, req.getContenuto());
        Long idPubblicante = chat.getPropostaGenerante().getAnnuncioInteresse().getPubblicante().getIdUtenteReg();
        Long idProponente  = chat.getPropostaGenerante().getProponente().getIdUtenteReg();
        Long idAltro = idMittente.equals(idPubblicante) ? idProponente : idPubblicante;
        notificaService.crea(idAltro, Notifica.TipoNotifica.NUOVO_MESSAGGIO, "Hai ricevuto un nuovo messaggio.");
        return risposta;
    }

    // metodo per completare una chat: verifica che la chat sia aperta, invia un messaggio di conferma, 
    // aggiorna lo stato della chat e degli annunci, calcola la CO2 risparmiata e assegna i badge agli utenti
    public CompletaResponse completa(Long idChat, Long idUtente) {
        ChatResponse chat = chatDao.findById(idChat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"aperta".equals(chat.getStatoChat())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chat non aperta");
        }

        Long idPubblicante = chat.getPropostaGenerante().getAnnuncioInteresse().getPubblicante().getIdUtenteReg();
        Long idProponente  = chat.getPropostaGenerante().getProponente().getIdUtenteReg();

        List<MessaggioResponse> messaggi = messaggioDao.findByChat(idChat);
        boolean confermaPubblicante = messaggi.stream().anyMatch(m ->
                m.getContenuto().endsWith(CONFERMA_SUFFIX)
                && m.getMittente().getIdUtenteReg().equals(idPubblicante));
        boolean confermaProponente = messaggi.stream().anyMatch(m ->
                m.getContenuto().endsWith(CONFERMA_SUFFIX)
                && m.getMittente().getIdUtenteReg().equals(idProponente));

        boolean giaConfermatoDaMe = idUtente.equals(idPubblicante) ? confermaPubblicante : confermaProponente;

        if (!giaConfermatoDaMe) {
            UtenteRegistrato utente = utenteDao.findEntityById(idUtente).orElseThrow();
            messaggioDao.invia(idChat, idUtente, utente.getNomeCompleto() + " " + CONFERMA_SUFFIX);
            if (idUtente.equals(idPubblicante)) confermaPubblicante = true;
            else confermaProponente = true;
        }

        if (!(confermaPubblicante && confermaProponente)) {
            return new CompletaResponse(false, null);
        }

        // entrambi confermano: completamento
        chatDao.updateStato(idChat, Chat.StatoChat.completata, LocalDateTime.now());

        Long idInteresse = chat.getPropostaGenerante().getAnnuncioInteresse().getIdAnnuncio();
        annuncioDao.updateStato(idInteresse, Annuncio.StatoAnnuncio.chiuso);

        Long idAnnuncioOfferto = chat.getPropostaGenerante().getAnnunciOfferti().stream()
                .filter(ChatResponse.AnnuncioInclusoSummary::getFlagSelezionato)
                .map(ai -> ai.getAnnuncioOfferto().getIdAnnuncio())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        annuncioDao.updateStato(idAnnuncioOfferto, Annuncio.StatoAnnuncio.chiuso);

        BigDecimal co2Interesse = calcolaCo2(idInteresse);
        BigDecimal co2Offerto   = calcolaCo2(idAnnuncioOfferto);
        BigDecimal co2Risparmiata = co2Interesse.add(co2Offerto);

        utenteDao.updateCo2AndPunteggio(idPubblicante, co2Risparmiata, 50);
        badgeService.assegnaBadge(idPubblicante);

        utenteDao.updateCo2AndPunteggio(idProponente, co2Risparmiata, 50);
        badgeService.assegnaBadge(idProponente);

        Long idAltroUtente = idUtente.equals(idPubblicante) ? idProponente : idPubblicante;
        return new CompletaResponse(true, idAltroUtente);
    }

    // metodo per ottenere gli id dei messaggi non letti e delle chat vuote dell'utente loggato
    public NonLettiResponse getNonLetti(Long idUtente) {
        LocalDateTime ultimaVisita = utenteDao.getUltimaVisitaChat(idUtente);
        List<Long> messaggiNonLetti = messaggioDao.findUnreadChatIds(idUtente);
        List<Long> chatVuote = chatDao.findVuoteByUtente(idUtente, ultimaVisita);
        return new NonLettiResponse(messaggiNonLetti, chatVuote);
    }

    // metodo per segnare i messaggi di una chat come letti
    public void leggi(Long idChat, Long idUtente) {
        messaggioDao.markAsRead(idChat, idUtente);
    }

    // metodo per annullare una chat: aggiorna lo stato della chat e degli annunci, 
    // invia un messaggio di annullamento e crea una notifica per l'altro utente
    public ChatResponse annulla(Long idChat, Long idUtente) {
        ChatResponse chat = chatDao.findById(idChat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        chatDao.updateStato(idChat, Chat.StatoChat.annullata, null);

        Long idInteresse = chat.getPropostaGenerante().getAnnuncioInteresse().getIdAnnuncio();
        annuncioDao.updateStato(idInteresse, Annuncio.StatoAnnuncio.attivo);

        chat.getPropostaGenerante().getAnnunciOfferti().stream()
                .filter(ChatResponse.AnnuncioInclusoSummary::getFlagSelezionato)
                .map(ai -> ai.getAnnuncioOfferto().getIdAnnuncio())
                .forEach(id -> annuncioDao.updateStato(id, Annuncio.StatoAnnuncio.attivo));

        UtenteRegistrato utente = utenteDao.findEntityById(idUtente).orElse(null);
        if (utente != null) {
            messaggioDao.invia(idChat, idUtente, utente.getNomeCompleto() + " ha annullato lo scambio");
        }

        return chatDao.findById(idChat).orElseThrow();
    }

    // metodo privato per calcolare la CO2 risparmiata da uno scambio, interrogando il servizio ClimatiqService
    private BigDecimal calcolaCo2(Long idAnnuncio) {
        return annuncioDao.findById(idAnnuncio).map(ann -> {
            BigDecimal prezzo = ann.getPrezzoStimato();
            return climatiqService.stimaCo2Risparmiata(ann.getCategoria(), prezzo)
                    .orElseGet(() -> prezzo.multiply(new BigDecimal("0.032")));
        }).orElse(BigDecimal.ZERO);
    }
}
