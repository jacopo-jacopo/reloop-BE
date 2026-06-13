package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Segnalazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SegnalazioneRepository extends JpaRepository<Segnalazione, Long> {

    // Segnalazioni dell'utente — usa id_utente_reg (nome colonna DB)
    List<Segnalazione> findBySegnalante_IdUtenteReg(Long idUtente);

    List<Segnalazione> findByStatoSegnalazione(Segnalazione.StatoSegnalazione stato);
}