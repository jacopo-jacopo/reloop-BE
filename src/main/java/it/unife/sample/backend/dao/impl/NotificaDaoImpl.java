package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.NotificaDao;
import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;
import it.unife.sample.backend.repository.NotificaRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// implementazione dell'interfaccia NotificaDao: fornisce i metodi per l'accesso ai dati delle notifiche nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato 
public class NotificaDaoImpl implements NotificaDao {

    private final NotificaRepository notificaRepo;
    private final UtenteRegistratoRepository utenteRepo;

    // crea una nuova notifica per un utente destinatario e la salva nel db
    @Override
    public void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo) {
        utenteRepo.findById(idDestinatario).ifPresent(u -> {
            Notifica n = new Notifica();
            n.setDestinatario(u);
            n.setTipo(tipo);
            n.setTesto(testo);
            notificaRepo.save(n);
        });
    }

    // restituisce la lista delle notifiche di un utente, ordinate per data di creazione decrescente
    @Override
    public List<NotificaResponse> findByUtente(Long idUtente) {
        return notificaRepo
                .findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(idUtente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // segna una notifica come letta
    @Override
    public void segnaLetta(Long idNotifica) {
        notificaRepo.findById(idNotifica).ifPresent(n -> {
            n.setLetta(true);
            notificaRepo.save(n);
        });
    }

    // segna tutte le notifiche di un utente come lette
    @Override
    public void segnaTutteLette(Long idUtente) {
        notificaRepo.findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(idUtente)
                .forEach(n -> { n.setLetta(true); notificaRepo.save(n); });
    }

    // restituisce il numero di notifiche non lette di un utente
    @Override
    public long countNonLette(Long idUtente) {
        return notificaRepo.countByDestinatario_IdUtenteRegAndLettaFalse(idUtente);
    }



    // mappa un oggetto Notifica a un oggetto NotificaResponse, 
    // che contiene solo le informazioni necessarie per la risposta al client
    private NotificaResponse toResponse(Notifica n) {
        return new NotificaResponse(
                n.getIdNotifica(),
                n.getTipo().name(),
                n.getTesto(),
                n.isLetta(),
                n.getTimestampNotifica()
        );
    }
}
