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

// service per la gestione degli annunci: fornisce metodi per ottenere, creare, aggiornare ed eliminare gli annunci
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, annuncioDao)
public class AnnuncioService {

    private final AnnuncioDao annuncioDao;

    // metodo per ottenere tutti gli annunci, con possibilità di filtrare per titolo, categoria o quartiere, e limitare il numero di risultati
    public List<AnnuncioResponse> getAll(String cerca, String categoria, Long idQuartiere, Integer limit, Long idUtente) {
        List<AnnuncioResponse> result;
        if (cerca != null)           result = annuncioDao.cercaPerTitolo(cerca);
        else if (categoria != null)  result = annuncioDao.cercaPerCategoria(categoria);
        else if (idQuartiere != null) result = annuncioDao.findByQuartiere(idQuartiere, idUtente);
        else                         result = annuncioDao.findAll();
        if (limit != null && limit > 0) result = result.stream().limit(limit).toList();
        return result;
    }

    // metodo per ottenere un annuncio per ID: se l'annuncio non esiste, lancia un'eccezione (404 not found)
    public AnnuncioResponse getById(Long id) {
        return annuncioDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // metodo per ottenere le foto di un annuncio per ID
    public List<String> getFoto(Long idAnnuncio) {
        return annuncioDao.findFotoById(idAnnuncio);
    }

    // metodo per creare un nuovo annuncio: riceve l'ID dell'utente e i dati dell'annuncio
    public AnnuncioResponse crea(Long idUtente, CreaAnnuncioRequest req) {
        return annuncioDao.crea(idUtente, req);
    }

    // metodo per aggiornare un annuncio esistente: se l'annuncio non esiste, lancia un'eccezione (404 not found)
    public AnnuncioResponse aggiorna(Long id, AggiornaAnnuncioRequest req) {
        if (!annuncioDao.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return annuncioDao.aggiorna(id, req);
    }

    // metodo per eliminare un annuncio: se l'annuncio non esiste, lancia un'eccezione (404 not found)
    public void elimina(Long id) {
        if (!annuncioDao.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        annuncioDao.elimina(id);
    }
}
