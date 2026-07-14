package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;

import java.util.List;

// interfaccia per l'accesso ai dati delle notifiche: definisce i metodi per la gestione delle notifiche nel database
public interface NotificaDao {

    void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo); // crea una nuova notifica per un utente destinatario e la salva nel db
    List<NotificaResponse> findByUtente(Long idUtente); // restituisce la lista delle notifiche di un utente, ordinate per data di creazione decrescente
    void segnaLetta(Long idNotifica); // segna una notifica come letta
    void segnaTutteLette(Long idUtente); // segna tutte le notifiche di un utente come lette
    long countNonLette(Long idUtente); // restituisce il numero di notifiche non lette di un utente
}
