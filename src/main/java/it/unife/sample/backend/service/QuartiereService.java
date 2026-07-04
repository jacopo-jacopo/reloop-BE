package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.QuartiereDao;
import it.unife.sample.backend.dto.request.AggiornaQuartiereRequest;
import it.unife.sample.backend.dto.request.CreaQuartiereRequest;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuartiereService {

    // inietta il DAO dei quartieri per accedere ai dati dei quartieri nel database
    private final QuartiereDao quartiereDao;

    // restituisce la lista di tutti i quartieri
    public List<QuartiereResponse> findAll() {
        return quartiereDao.findAll();
    }

    // crea un nuovo quartiere utilizzando i dati forniti nella richiesta
    public QuartiereResponse crea(CreaQuartiereRequest req) {
        return quartiereDao.crea(req.getNomeQuartiere(), req.getCitta());
    }

    // aggiorna un quartiere esistente identificato dall'ID utilizzando i dati forniti nella richiesta
    public QuartiereResponse aggiorna(Long id, AggiornaQuartiereRequest req) {
        return quartiereDao.aggiorna(id, req.getNomeQuartiere(), req.getCitta());
    }
}
