package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.NotificaDao;
import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// service per la gestione delle notifiche: fornisce metodi per creare notifiche, ottenere le notifiche dell'utente loggato, 
// segnare le notifiche come lette e contare le notifiche non lette
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, notificaDao)
public class NotificaService {

    private final NotificaDao notificaDao;

    // metodo per creare una nuova notifica: riceve l'id del destinatario, il tipo di notifica e il testo della notifica,
    // e chiama il metodo crea del NotificaDao per salvare la notifica nel db  
    public void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo) {
        notificaDao.crea(idDestinatario, tipo, testo);
    }

    // metodo per ottenere le notifiche dell'utente loggato: riceve l'id dell'utente e chiama il metodo findByUtente del NotificaDao
    public List<NotificaResponse> getMie(Long idUtente) {
        return notificaDao.findByUtente(idUtente);
    }

    // metodo per segnare una notifica come letta: riceve l'id della notifica e chiama il metodo segnaLetta del NotificaDao
    public void segnaLetta(Long idNotifica) {
        notificaDao.segnaLetta(idNotifica);
    }

    // metodo per segnare tutte le notifiche di un utente come lette: riceve l'id dell'utente e chiama il metodo segnaLutteLette del NotificaDao
    public void segnaLutteLette(Long idUtente) {
        notificaDao.segnaLutteLette(idUtente);
    }

    // metodo per contare le notifiche non lette di un utente: riceve l'id dell'utente e chiama il metodo countNonLette del NotificaDao
    public long countNonLette(Long idUtente) {
        return notificaDao.countNonLette(idUtente);
    }
}
