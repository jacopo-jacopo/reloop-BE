package it.unife.sample.backend.dao;

import it.unife.sample.backend.model.Amministratore;

import java.util.Optional;

public interface AmministratoreDao {

    Optional<Amministratore> findByEmail(String email);
    Optional<Amministratore> findById(Long id);
}
