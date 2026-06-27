package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.model.Quartiere;

import java.util.List;
import java.util.Optional;

public interface QuartiereDao {

    List<QuartiereResponse> findAll();
    Optional<Quartiere> findById(Long id);
}
