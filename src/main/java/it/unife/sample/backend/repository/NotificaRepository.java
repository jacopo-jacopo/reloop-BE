package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Notifica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// repository per l'accesso ai dati delle notifiche nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface NotificaRepository extends JpaRepository<Notifica, Long> {

    // trova tutte le notifiche destinate a un utente specifico, ordinate per data di creazione decrescente
    List<Notifica> findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(Long idUtente);

    // conta il numero di notifiche non lette destinate a un utente specifico
    long countByDestinatario_IdUtenteRegAndLettaFalse(Long idUtente);
}
