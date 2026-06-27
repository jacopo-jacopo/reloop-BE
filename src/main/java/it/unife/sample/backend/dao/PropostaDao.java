package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.InviaPropostaRequest;
import it.unife.sample.backend.dto.response.PropostaResponse;
import it.unife.sample.backend.model.Proposta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PropostaDao {

    List<PropostaResponse> findRicevute(Long idUtente);
    List<PropostaResponse> findInviate(Long idUtente);
    long countNuoveRicevute(Long idUtente, LocalDateTime ultimaVisita);
    Optional<PropostaResponse> findById(Long id);
    PropostaResponse crea(InviaPropostaRequest req, Long idProponente);
    PropostaResponse updateStato(Long idProposta, Proposta.StatoProposta stato);
    void accettaConAnnuncioScelto(Long idProposta, Long idAnnuncioScelto);
    void rifiutaProposteInAttesaPerAnnunci(Long idAnnuncio1, Long idAnnuncio2, Long idPropostaEsclusa);
    void rifiutaProposteInAttesaPerAnnuncio(Long idAnnuncio);
}
