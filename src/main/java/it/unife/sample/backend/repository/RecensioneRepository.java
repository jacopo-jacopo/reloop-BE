package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Recensione.RecensioneId> {
    List<Recensione> findById_IdUtenteRegRecensito(Long idUtente);
    List<Recensione> findById_IdUtenteRegRecensore(Long idUtente);

    // Numero di recensioni ricevute con un determinato voto — usato per i badge "X recensioni a Y stelle"
    long countById_IdUtenteRegRecensitoAndVoto(Long idUtente, Integer voto);

    // Numero totale di recensioni ricevute
    long countById_IdUtenteRegRecensito(Long idUtente);

    // Media dei voti ricevuti
    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.id.idUtenteRegRecensito = :idUtente")
    Double mediaVotoById_IdUtenteRegRecensito(@Param("idUtente") Long idUtente);
}