package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;

import java.util.List;

public interface RecensioneDao {

    List<RecensioneResponse> findByRecensito(Long idUtente);
    RecensioneResponse salva(InviaRecensioneRequest req, Long idRecensore);
}
