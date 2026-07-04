package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;

import java.util.List;

public interface NotificaDao {

    void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo);
    List<NotificaResponse> findByUtente(Long idUtente);
    void segnaLetta(Long idNotifica);
    void segnaLutteLette(Long idUtente);
    long countNonLette(Long idUtente);
}
