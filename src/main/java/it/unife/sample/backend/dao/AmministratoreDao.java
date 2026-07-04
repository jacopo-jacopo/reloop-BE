package it.unife.sample.backend.dao;

import it.unife.sample.backend.model.Amministratore;

import java.util.Optional;

// interfaccia per l'accesso ai dati degli amministratori: definisce i metodi per cercare un amministratore nel database
public interface AmministratoreDao {

    // entrambi i metodi restituiscono un Optional:
    // se l'amministratore esiste nel database, l'Optional conterrà l'oggetto Amministratore; altrimenti, sarà vuoto

    Optional<Amministratore> findByEmail(String email); // cerca un amministratore nel database per email
    Optional<Amministratore> findById(Long id); // cerca un amministratore nel database per id
}
