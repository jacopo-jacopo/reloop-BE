package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.AmministratoreDao;
import it.unife.sample.backend.model.Amministratore;
import it.unife.sample.backend.repository.AmministratoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// implementazione dell'interfaccia AmministratoreDao: fornisce i metodi per l'accesso ai dati degli amministratori nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, adminRepo)
public class AmministratoreDaoImpl implements AmministratoreDao {

    private final AmministratoreRepository adminRepo;

    // trova un amministratore nel database in base all'email fornita
    @Override
    public Optional<Amministratore> findByEmail(String email) {
        return adminRepo.findByEmail(email);
    }

    // trova un amministratore nel database in base all'ID fornito
    @Override
    public Optional<Amministratore> findById(Long id) {
        return adminRepo.findById(id);
    }
}
