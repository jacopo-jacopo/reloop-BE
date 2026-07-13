package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// repository per l'accesso ai dati delle recensioni nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface RecensioneRepository extends JpaRepository<Recensione, Recensione.RecensioneId> {
    List<Recensione> findById_IdUtenteRegRecensito(Long idUtente); // restituisce la lista delle recensioni ricevute da un utente
    List<Recensione> findById_IdUtenteRegRecensore(Long idUtente); // restituisce la lista delle recensioni inviate da un utente

    // conta il numero di recensioni ricevute da un utente con un determinato voto
    long countById_IdUtenteRegRecensitoAndVoto(Long idUtente, Integer voto);

    // conta il numero totale di recensioni ricevute da un utente
    long countById_IdUtenteRegRecensito(Long idUtente);

    // calcola la media dei voti ricevuti da un utente
    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.id.idUtenteRegRecensito = :idUtente")
    Double mediaVotoById_IdUtenteRegRecensito(@Param("idUtente") Long idUtente);
}