package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.QuartiereDao;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuartiereService {

    private final QuartiereDao quartiereDao;

    public List<QuartiereResponse> findAll() {
        return quartiereDao.findAll();
    }
}
