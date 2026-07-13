package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.AggiornaAnnuncioRequest;
import it.unife.sample.backend.dto.request.CreaAnnuncioRequest;
import it.unife.sample.backend.dto.response.AnnuncioResponse;
import it.unife.sample.backend.model.Annuncio;

import java.util.List;
import java.util.Optional;

// interfaccia per l'accesso ai dati degli annunci: definisce i metodi per la gestione degli annunci nel database
public interface AnnuncioDao {

    List<AnnuncioResponse> findByQuartiere(Long idQuartiere, Long idUtenteEscluso); // restituisce la lista degli annunci pubblicati dagli utenti
                                                                                    // registrati che appartengono a un determinato quartiere
    List<AnnuncioResponse> cercaPerTitolo(String cerca); // restituisce la lista degli annunci che contengono una determinata stringa nel titolo
    List<AnnuncioResponse> cercaPerCategoria(String categoria); // restituisce la lista degli annunci che appartengono a una determinata categoria
    List<AnnuncioResponse> findAll(); // restituisce la lista di tutti gli annunci pubblicati dagli utenti registrati
    Optional<AnnuncioResponse> findById(Long id); // restituisce le informazioni di un annuncio
    List<AnnuncioResponse> findByPubblicante(Long idUtente); // restituisce la lista degli annunci pubblicati da un determinato utente registrato
    List<String> findFotoById(Long idAnnuncio); // restituisce la lista delle foto associate a un annuncio
    AnnuncioResponse crea(Long idUtente, CreaAnnuncioRequest req); // crea un nuovo annuncio nel db e restituisce le info dell'annuncio creato
    AnnuncioResponse aggiorna(Long id, AggiornaAnnuncioRequest req); // aggiorna le info di un annuncio nel db e le restituisce 
    void elimina(Long id); // elimina un annuncio dal db
    void updateStato(Long id, Annuncio.StatoAnnuncio stato); // aggiorna lo stato di un annuncio nel db
    boolean existsById(Long id); // verifica se esiste un annuncio nel db
}
