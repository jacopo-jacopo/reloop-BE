package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;

import java.util.List;

// interfaccia per l'accesso ai dati delle segnalazioni: definisce i metodi per la gestione delle segnalazioni nel db
public interface SegnalazioneDao {

    List<SegnalazioneResponse> findAll(); // restituisce la lista di tutte le segnalazioni
    List<SegnalazioneResponse> findByUtente(Long idUtente); // restituisce la lista delle segnalazioni inviate da un utente 
    SegnalazioneResponse crea(InviaSegnalazioneRequest req, Long idUtente); // crea una nuova segnalazione nel db e la restituisce
    SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin); // prende in carico una segnalazione (solo per admin) e la restituisce
    SegnalazioneResponse chiudi(Long idSegnalazione, ChiudiSegnalazioneRequest req, Long idAdmin); // chiude una segnalazione (solo per admin) e la restituisce
    boolean existsSegnalazioneAperta(Long idUtente, Long idAnnuncio); // verifica se esiste già una segnalazione aperta per un annuncio da parte di un utente
}
