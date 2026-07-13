package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.model.Chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// interfaccia per l'accesso ai dati delle chat: definisce i metodi per la gestione delle chat nel database
public interface ChatDao {

    List<ChatResponse> findByUtente(Long idUtente); // restituisce la lista delle chat a cui partecipa un utente
    Optional<ChatResponse> findById(Long id); // restituisce le informazioni di una chat 
    List<Long> findVuoteByUtente(Long idUtente, LocalDateTime ultimaVisita); // restituisce la lista degli id delle chat a cui partecipa un utente
                                                                             // e che non hanno messaggi inviati dopo l'ultima visita dell'utente
    long countCompletateByUtente(Long idUtente); // restituisce il numero di chat completate a cui ha partecipato un utente
    long countByStato(Chat.StatoChat stato); // restituisce il numero di chat in un determinato stato (APERTO, IN_CORSO, COMPLETATO)
    List<ChatResponse> findAperteByAnnuncio(Long idAnnuncio); // restituisce la lista delle chat aperte relative a un determinato annuncio
    ChatResponse crea(Long idProposta); // crea una nuova chat relativa a una proposta e restituisce le informazioni della chat
    ChatResponse updateStato(Long idChat, Chat.StatoChat stato, LocalDateTime dataCompletamento); // aggiorna lo stato di una chat e la data di 
                                                                                                  // completamento (se lo stato è COMPLETATO) 
                                                                                                  // e restituisce le informazioni della chat
}
