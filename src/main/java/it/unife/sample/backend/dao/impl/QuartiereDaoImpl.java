package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.QuartiereDao;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.repository.QuartiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuartiereDaoImpl implements QuartiereDao {

    private final QuartiereRepository quartiereRepo;

    @Override
    public List<QuartiereResponse> findAll() {
        return quartiereRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public Optional<Quartiere> findById(Long id) {
        return quartiereRepo.findById(id);
    }

    private QuartiereResponse toResponse(Quartiere q) {
        return new QuartiereResponse(q.getIdQuartiere(), q.getNomeQuartiere(), q.getCitta());
    }
}
