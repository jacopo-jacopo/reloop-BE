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
        return propostaDao.crea(req, idUtente);
    }

    public ChatResponse accetta(Long idProposta, AccettaPropostaRequest req) {
        PropostaResponse proposta = propostaDao.findById(idProposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Accetta proposta e marca annuncio scelto
        propostaDao.updateStato(idProposta, Proposta.StatoProposta.accettata);
        propostaDao.accettaConAnnuncioScelto(idProposta, req.getIdAnnuncioScelto());

        // Sospendi annuncio di interesse
        Long idInteresse = proposta.getAnnuncioInteresse().getIdAnnuncio();
        annuncioDao.updateStato(idInteresse, Annuncio.StatoAnnuncio.sospeso);

        // Sospendi annuncio scelto
        annuncioDao.updateStato(req.getIdAnnuncioScelto(), Annuncio.StatoAnnuncio.sospeso);

        // Rifiuta altre proposte in attesa che coinvolgono i due annunci
        propostaDao.rifiutaProposteInAttesaPerAnnunci(idInteresse, req.getIdAnnuncioScelto(), idProposta);

        // Crea chat automaticamente
        return chatDao.crea(idProposta);
    }

    public PropostaResponse rifiuta(Long idProposta) {
        if (propostaDao.findById(idProposta).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return propostaDao.updateStato(idProposta, Proposta.StatoProposta.rifiutata);
    }
}
