package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Elimina;
import org.springframework.data.jpa.repository.JpaRepository;

// repository per l'accesso ai dati delle eliminazioni nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface EliminaRepository extends JpaRepository<Elimina, Elimina.EliminaId> {
    // vuota perché JpaRepository fornisce già tutti i metodi necessari per l'accesso ai dati delle eliminazioni
}
