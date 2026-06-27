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

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatDao chatDao;
    private final MessaggioDao messaggioDao;
    private final UtenteDao utenteDao;
    private final AnnuncioDao annuncioDao;
    private final BadgeService badgeService;
    private final ClimatiqService climatiqService;

    private static final String CONFERMA_SUFFIX = "ha confermato che lo scambio è stato completato";

    public List<ChatResponse> getMie(Long idUtente) {
        return chatDao.findByUtente(idUtente);
    }

    public ChatResponse getById(Long id) {
        return chatDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<MessaggioResponse> getMessaggi(Long idChat) {
        return messaggioDao.findByChat(idChat);
    }

    public MessaggioResponse inviaMessaggio(Long idChat, Long idMittente, InviaMessaggioRequest req) {
        ChatResponse chat = chatDao.findById(idChat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"aperta".equals(chat.getStatoChat())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chat non aperta");
        }
        return messaggioDao.invia(idChat, idMittente, req.getContenuto());
    }

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

        // Entrambi confermati: completamento
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

    public NonLettiResponse getNonLetti(Long idUtente) {
        LocalDateTime ultimaVisita = utenteDao.getUltimaVisitaChat(idUtente);
        List<Long> messaggiNonLetti = messaggioDao.findUnreadChatIds(idUtente);
        List<Long> chatVuote = chatDao.findVuoteByUtente(idUtente, ultimaVisita);
        return new NonLettiResponse(messaggiNonLetti, chatVuote);
    }

    public void leggi(Long idChat, Long idUtente) {
        messaggioDao.markAsRead(idChat, idUtente);
    }

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

    private BigDecimal calcolaCo2(Long idAnnuncio) {
        return annuncioDao.findById(idAnnuncio).map(ann -> {
            BigDecimal prezzo = ann.getPrezzoStimato();
            return climatiqService.stimaCo2Risparmiata(ann.getCategoria(), prezzo)
                    .orElseGet(() -> prezzo.multiply(new BigDecimal("0.032")));
        }).orElse(BigDecimal.ZERO);
    }
}
