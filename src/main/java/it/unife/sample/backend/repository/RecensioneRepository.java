package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Recensione.RecensioneId> {
    List<Recensione> findById_IdUtenteRegRecensito(Long idUtente);
    List<Recensione> findById_IdUtenteRegRecensore(Long idUtente);
    boolean existsById_IdUtenteRegRecensoreAndId_IdUtenteRegRecensito(Long idRecensore, Long idRecensito);
}