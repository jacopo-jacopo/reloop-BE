package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.SegnalazioneDao;
import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SegnalazioneService {

    private final SegnalazioneDao segnalazioneDao;

    public List<SegnalazioneResponse> getTutte() {
        return segnalazioneDao.findAll();
    }

    public List<SegnalazioneResponse> getMie(Long idUtente) {
        return segnalazioneDao.findByUtente(idUtente);
    }

    public SegnalazioneResponse invia(InviaSegnalazioneRequest req, Long idUtente) {
        if (segnalazioneDao.existsSegnalazioneAperta(idUtente, req.getIdAnnuncioSegnalato())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hai già segnalato questo annuncio");
        }
        return segnalazioneDao.crea(req, idUtente);
    }

    public SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin) {
        return segnalazioneDao.prendiInCarico(idSegnalazione, idAdmin);
    }

    public SegnalazioneResponse chiudi(Long idSegnalazione, ChiudiSegnalazioneRequest req, Long idAdmin) {
        return segnalazioneDao.chiudi(idSegnalazione, req, idAdmin);
    }
}
