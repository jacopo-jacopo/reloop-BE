package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

// interfaccia per l'accesso ai dati dei badge: estende JpaRepository per fornire metodi CRUD e di query personalizzati per l'entità Badge
public interface BadgeRepository extends JpaRepository<Badge, String> {
    // vuota perché JpaRepository fornisce già tutti i metodi necessari per l'accesso ai dati dei badge nel database
}