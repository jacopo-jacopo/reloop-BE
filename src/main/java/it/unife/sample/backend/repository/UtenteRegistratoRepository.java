package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.UtenteRegistrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UtenteRegistratoRepository extends JpaRepository<UtenteRegistrato, Long> {

    Optional<UtenteRegistrato> findByEmail(String email);

    boolean existsByEmail(String email);

    // Leaderboard: tutti gli utenti ordinati per punteggio decrescente
    @Query("SELECT u FROM UtenteRegistrato u ORDER BY u.punteggio DESC")
    List<UtenteRegistrato> findLeaderboard();

    // Utenti di un quartiere — usato per calcolo CO₂ quartiere
    List<UtenteRegistrato> findByQuartiere_IdQuartiere(Long idQuartiere);
}