package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.AnnuncioDao;
import it.unife.sample.backend.dao.ChatDao;
import it.unife.sample.backend.dao.PropostaDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.AccettaPropostaRequest;
import it.unife.sample.backend.dto.request.InviaPropostaRequest;
import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.dto.response.PropostaResponse;
import it.unife.sample.backend.model.Annuncio;
import it.unife.sample.backend.model.Notifica;
import it.unife.sample.backend.model.Proposta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

// service per la gestione delle proposte: fornisce metodi per ottenere le proposte ricevute e inviate dall'utente loggato,
// inviare una nuova proposta, accettare o rifiutare una proposta ricevuta
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class PropostaService {

    private final PropostaDao propostaDao;
    private final AnnuncioDao annuncioDao;
    private final ChatDao chatDao;
    private final UtenteDao utenteDao;
    private final NotificaService notificaService;

    // metodo per ottenere il numero di nuove proposte ricevute dall'utente loggato
    public long getBadge(Long idUtente) {
        LocalDateTime ultimaVisita = utenteDao.getUltimaVisitaProposte(idUtente);
        return propostaDao.countNuoveRicevute(idUtente, ultimaVisita);
    }

    // metodo per ottenere le proposte ricevute dall'utente loggato
    public List<PropostaResponse> getRicevute(Long idUtente) {
        return propostaDao.findRicevute(idUtente);
    }

    // metodo per ottenere le proposte inviate dall'utente loggato
    public List<PropostaResponse> getInviate(Long idUtente) {
        return propostaDao.findInviate(idUtente);
    }

    // metodo per inviare una nuova proposta
    public PropostaResponse invia(InviaPropostaRequest req, Long idUtente) {
        PropostaResponse proposta = propostaDao.crea(req, idUtente);
        Long idPubblicante = proposta.getAnnuncioInteresse().getPubblicante().getIdUtenteReg();
        notificaService.crea(idPubblicante, Notifica.TipoNotifica.NUOVA_PROPOSTA,
                "Hai ricevuto una nuova proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\" da " + proposta.getProponente().getNomeCompleto() + ".");
        return proposta;
    }

    // metodo per accettare una proposta ricevuta: aggiorna lo stato della proposta, sospende gli annunci coinvolti,
    // rifiuta le altre proposte in attesa per gli annunci coinvolti, crea una notifica per il proponente della proposta accettata 
    // e crea una chat tra i due utenti
    public ChatResponse accetta(Long idProposta, AccettaPropostaRequest req) {
        PropostaResponse proposta = propostaDao.findById(idProposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        propostaDao.updateStato(idProposta, Proposta.StatoProposta.accettata);
        propostaDao.accettaConAnnuncioScelto(idProposta, req.getIdAnnuncioScelto());

        Long idInteresse = proposta.getAnnuncioInteresse().getIdAnnuncio();
        annuncioDao.updateStato(idInteresse, Annuncio.StatoAnnuncio.sospeso);
        annuncioDao.updateStato(req.getIdAnnuncioScelto(), Annuncio.StatoAnnuncio.sospeso);

        propostaDao.rifiutaProposteInAttesaPerAnnunci(idInteresse, req.getIdAnnuncioScelto(), idProposta);

        notificaService.crea(proposta.getProponente().getIdUtenteReg(), Notifica.TipoNotifica.PROPOSTA_ACCETTATA,
                "La tua proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\" è stata accettata da "
                + proposta.getAnnuncioInteresse().getPubblicante().getNomeCompleto() + "!");

        return chatDao.crea(idProposta);
    }

    // metodo per rifiutare una proposta ricevuta: aggiorna lo stato della proposta e crea una notifica per il proponente della proposta rifiutata
    public PropostaResponse rifiuta(Long idProposta) {
        PropostaResponse proposta = propostaDao.findById(idProposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        PropostaResponse aggiornata = propostaDao.updateStato(idProposta, Proposta.StatoProposta.rifiutata);
        notificaService.crea(proposta.getProponente().getIdUtenteReg(), Notifica.TipoNotifica.PROPOSTA_RIFIUTATA,
                "La tua proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\" è stata rifiutata.");
        return aggiornata;
    }
}
