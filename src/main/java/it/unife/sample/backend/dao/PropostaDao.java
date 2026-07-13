package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.InviaPropostaRequest;
import it.unife.sample.backend.dto.response.PropostaResponse;
import it.unife.sample.backend.model.Proposta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// interfaccia per l'accesso ai dati delle proposte: definisce i metodi per la gestione delle proposte nel database
public interface PropostaDao {

    List<PropostaResponse> findRicevute(Long idUtente); // restituisce la lista delle proposte ricevute da un utente 
    List<PropostaResponse> findInviate(Long idUtente); // restituisce la lista delle proposte inviate da un utente
    long countNuoveRicevute(Long idUtente, LocalDateTime ultimaVisita); // restituisce il numero di nuove proposte ricevute da un utente 
    Optional<PropostaResponse> findById(Long id); // restituisce le informazioni di una proposta dato il suo id
    PropostaResponse crea(InviaPropostaRequest req, Long idProponente); // crea una nuova proposta
    PropostaResponse updateStato(Long idProposta, Proposta.StatoProposta stato); // aggiorna lo stato di una proposta e la restituisce
    void accettaConAnnuncioScelto(Long idProposta, Long idAnnuncioScelto); // accetta una proposta e specifica quale annuncio offerto prendere
    void rifiutaProposteInAttesaPerAnnunci(Long idAnnuncio1, Long idAnnuncio2, Long idPropostaEsclusa); // rifiuta tutte le proposte in attesa per due
                                                                                                        // annunci specificati, escludendo una proposta specifica
    void rifiutaProposteInAttesaPerAnnuncio(Long idAnnuncio); // rifiuta tutte le proposte in attesa per un annuncio specificato
}
