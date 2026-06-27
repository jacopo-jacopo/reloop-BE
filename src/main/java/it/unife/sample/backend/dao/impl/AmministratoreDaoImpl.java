package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.AmministratoreDao;
import it.unife.sample.backend.model.Amministratore;
import it.unife.sample.backend.repository.AmministratoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AmministratoreDaoImpl implements AmministratoreDao {

    private final AmministratoreRepository adminRepo;

    @Override
    public Optional<Amministratore> findByEmail(String email) {
        return adminRepo.findByEmail(email);
    }

    @Override
    public Optional<Amministratore> findById(Long id) {
        return adminRepo.findById(id);
    }
}
