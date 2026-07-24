package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.UtenteRegistrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

// repository per l'accesso ai dati degli utenti registrati nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface UtenteRegistratoRepository extends JpaRepository<UtenteRegistrato, Long> {

    // trova un utente registrato per email
    Optional<UtenteRegistrato> findByEmail(String email);

    // verifica se esiste un utente registrato con una determinata email
    boolean existsByEmail(String email);

    // trova tutti gli utenti registrati del quartiere indicato, ordinati per punteggio decrescente (per la leaderboard)
    @Query("SELECT u FROM UtenteRegistrato u WHERE u.quartiere.idQuartiere = :idQuartiere ORDER BY u.punteggio DESC, u.co2Totale DESC")
    List<UtenteRegistrato> findLeaderboard(@Param("idQuartiere") Long idQuartiere);

    // trova tutti gli utenti registrati appartenenti a un determinato quartiere
    List<UtenteRegistrato> findByQuartiere_IdQuartiere(Long idQuartiere);

    // conta il numero di utenti registrati con un punteggio maggiore di un determinato valore
    @Query("SELECT COUNT(u) FROM UtenteRegistrato u WHERE u.punteggio > :punteggio")
    long countConPunteggioMaggiore(@Param("punteggio") Integer punteggio);
}