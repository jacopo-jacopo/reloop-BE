package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.AggiornaAnnuncioRequest;
import it.unife.sample.backend.dto.request.CreaAnnuncioRequest;
import it.unife.sample.backend.dto.response.AnnuncioResponse;
import it.unife.sample.backend.model.Annuncio;

import java.util.List;
import java.util.Optional;

public interface AnnuncioDao {

    List<AnnuncioResponse> findByQuartiere(Long idQuartiere, Long idUtenteEscluso);
    List<AnnuncioResponse> cercaPerTitolo(String cerca);
    List<AnnuncioResponse> cercaPerCategoria(String categoria);
    List<AnnuncioResponse> findAll();
    Optional<AnnuncioResponse> findById(Long id);
    List<AnnuncioResponse> findByPubblicante(Long idUtente);
    List<String> findFotoById(Long idAnnuncio);
    AnnuncioResponse crea(Long idUtente, CreaAnnuncioRequest req);
    AnnuncioResponse aggiorna(Long id, AggiornaAnnuncioRequest req);
    void elimina(Long id);
    void updateStato(Long id, Annuncio.StatoAnnuncio stato);
    boolean existsById(Long id);
}
