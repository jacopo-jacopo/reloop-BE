package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.NotificaDao;
import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificaService {

    private final NotificaDao notificaDao;

    public void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo) {
        notificaDao.crea(idDestinatario, tipo, testo);
    }

    public List<NotificaResponse> getMie(Long idUtente) {
        return notificaDao.findByUtente(idUtente);
    }

    public void segnaLetta(Long idNotifica) {
        notificaDao.segnaLetta(idNotifica);
    }

    public void segnaLutteLette(Long idUtente) {
        notificaDao.segnaLutteLette(idUtente);
    }

    public long countNonLette(Long idUtente) {
        return notificaDao.countNonLette(idUtente);
    }
}
