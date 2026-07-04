package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.AnnuncioDao;
import it.unife.sample.backend.dto.request.AggiornaAnnuncioRequest;
import it.unife.sample.backend.dto.request.CreaAnnuncioRequest;
import it.unife.sample.backend.dto.response.AnnuncioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnuncioService {

    private final AnnuncioDao annuncioDao;

    public List<AnnuncioResponse> getAll(String cerca, String categoria, Long idQuartiere, Integer limit, Long idUtente) {
        List<AnnuncioResponse> result;
        if (cerca != null)           result = annuncioDao.cercaPerTitolo(cerca);
        else if (categoria != null)  result = annuncioDao.cercaPerCategoria(categoria);
        else if (idQuartiere != null) result = annuncioDao.findByQuartiere(idQuartiere, idUtente);
        else                         result = annuncioDao.findAll();
        if (limit != null && limit > 0) result = result.stream().limit(limit).toList();
        return result;
    }

    public AnnuncioResponse getById(Long id) {
        return annuncioDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<String> getFoto(Long idAnnuncio) {
        return annuncioDao.findFotoById(idAnnuncio);
    }

    public AnnuncioResponse crea(Long idUtente, CreaAnnuncioRequest req) {
        return annuncioDao.crea(idUtente, req);
    }

    public AnnuncioResponse aggiorna(Long id, AggiornaAnnuncioRequest req) {
        if (!annuncioDao.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return annuncioDao.aggiorna(id, req);
    }

    public void elimina(Long id) {
        if (!annuncioDao.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        annuncioDao.elimina(id);
    }
}
