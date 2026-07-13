package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.BadgeOttenuto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// interfaccia per l'accesso ai dati dei badge ottenuti: estende JpaRepository per fornire metodi CRUD e query personalizzate
public interface BadgeOttenutoRepository extends JpaRepository<BadgeOttenuto, BadgeOttenuto.BadgeOttenutoId> {
    List<BadgeOttenuto> findById_IdUtenteReg(Long idUtente); // restituisce la lista dei badge ottenuti da un utente specifico
}