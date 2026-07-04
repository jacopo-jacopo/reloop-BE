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

@Service
@RequiredArgsConstructor
public class PropostaService {

    private final PropostaDao propostaDao;
    private final AnnuncioDao annuncioDao;
    private final ChatDao chatDao;
    private final UtenteDao utenteDao;
    private final NotificaService notificaService;

    public long getBadge(Long idUtente) {
        LocalDateTime ultimaVisita = utenteDao.getUltimaVisitaProposte(idUtente);
        return propostaDao.countNuoveRicevute(idUtente, ultimaVisita);
    }

    public List<PropostaResponse> getRicevute(Long idUtente) {
        return propostaDao.findRicevute(idUtente);
    }

    public List<PropostaResponse> getInviate(Long idUtente) {
        return propostaDao.findInviate(idUtente);
    }

    public PropostaResponse invia(InviaPropostaRequest req, Long idUtente) {
        PropostaResponse proposta = propostaDao.crea(req, idUtente);
        Long idPubblicante = proposta.getAnnuncioInteresse().getPubblicante().getIdUtenteReg();
        notificaService.crea(idPubblicante, Notifica.TipoNotifica.NUOVA_PROPOSTA,
                "Hai ricevuto una nuova proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\".");
        return proposta;
    }

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
                "La tua proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\" è stata accettata!");

        return chatDao.crea(idProposta);
    }

    public PropostaResponse rifiuta(Long idProposta) {
        PropostaResponse proposta = propostaDao.findById(idProposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        PropostaResponse aggiornata = propostaDao.updateStato(idProposta, Proposta.StatoProposta.rifiutata);
        notificaService.crea(proposta.getProponente().getIdUtenteReg(), Notifica.TipoNotifica.PROPOSTA_RIFIUTATA,
                "La tua proposta per \"" + proposta.getAnnuncioInteresse().getTitolo() + "\" è stata rifiutata.");
        return aggiornata;
    }
}
