package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Quartiere;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// repository per l'entità Quartiere, estende JpaRepository per fornire metodi CRUD e query personalizzate
public interface QuartiereRepository extends JpaRepository<Quartiere, Long> {
    List<Quartiere> findByCitta(String citta);
}