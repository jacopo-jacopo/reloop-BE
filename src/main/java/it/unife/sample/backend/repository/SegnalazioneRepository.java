package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Segnalazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// repository per l'accesso ai dati delle segnalazioni nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface SegnalazioneRepository extends JpaRepository<Segnalazione, Long> {

    // trova tutte le segnalazioni effettuate da un utente specifico
    List<Segnalazione> findBySegnalante_IdUtenteReg(Long idUtente);

    // trova tutte le segnalazioni relative a uno stato specifico (ad esempio, aperta, chiusa, ecc.)
    List<Segnalazione> findByStatoSegnalazione(Segnalazione.StatoSegnalazione stato);

    // verifica se l'utente ha già una segnalazione non chiusa per questo annuncio
    boolean existsBySegnalante_IdUtenteRegAndAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(
            Long idUtente, Long idAnnuncio, Segnalazione.StatoSegnalazione stato);

    // trova tutte le segnalazioni non chiuse per un annuncio specifico
    List<Segnalazione> findByAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(
            Long idAnnuncio, Segnalazione.StatoSegnalazione stato);
}