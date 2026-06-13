package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Amministratore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AmministratoreRepository extends JpaRepository<Amministratore, Long> {
    Optional<Amministratore> findByEmail(String email);
}