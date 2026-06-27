package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;

import java.util.List;

public interface SegnalazioneDao {

    List<SegnalazioneResponse> findAll();
    List<SegnalazioneResponse> findByUtente(Long idUtente);
    SegnalazioneResponse crea(InviaSegnalazioneRequest req, Long idUtente);
    SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin);
    SegnalazioneResponse chiudi(Long idSegnalazione, ChiudiSegnalazioneRequest req, Long idAdmin);
    boolean existsSegnalazioneAperta(Long idUtente, Long idAnnuncio);
}
