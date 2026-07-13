package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.MessaggioResponse;

import java.util.List;

// interfaccia per l'accesso ai dati dei messaggi: definisce i metodi per la gestione dei messaggi nel database
public interface MessaggioDao {

    List<MessaggioResponse> findByChat(Long idChat); // restituisce la lista dei messaggi di una chat, ordinati per data di invio crescente
    MessaggioResponse invia(Long idChat, Long idMittente, String contenuto); // invia un nuovo messaggio in una chat, salvandolo nel db e restituendolo
    long findMaxIdByChat(Long idChat); // restituisce l'id massimo dei messaggi di una chat, utile per determinare l'ultimo messaggio inviato
    void markAsRead(Long idChat, Long idUtente); // segna tutti i messaggi di una chat come letti da un utente, aggiornando lo stato nel db
    List<Long> findUnreadChatIds(Long idUtente); // restituisce la lista degli id delle chat che contengono messaggi non letti da un utente, 
                                                 // utile per notificare l'utente in caso di nuovi messaggi
}
