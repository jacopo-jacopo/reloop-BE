package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Elimina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EliminaRepository extends JpaRepository<Elimina, Elimina.EliminaId> {
}
