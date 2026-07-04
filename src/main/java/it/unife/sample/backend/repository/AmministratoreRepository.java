package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Amministratore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// repository per la'accesso agli amministratori nel database:
// estende JpaRepository per fornire metodi CRUD e query personalizzate sugli amministratori
public interface AmministratoreRepository extends JpaRepository<Amministratore, Long> {
    Optional<Amministratore> findByEmail(String email);
}