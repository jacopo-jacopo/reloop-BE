package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Notifica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepository extends JpaRepository<Notifica, Long> {

    List<Notifica> findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(Long idUtente);

    long countByDestinatario_IdUtenteRegAndLettaFalse(Long idUtente);
}
