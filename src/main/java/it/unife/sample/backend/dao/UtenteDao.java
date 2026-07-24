package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.dto.response.LeaderboardItemResponse;
import it.unife.sample.backend.dto.response.UtenteProfiloResponse;
import it.unife.sample.backend.dto.response.UtentePublicoResponse;
import it.unife.sample.backend.dto.response.UtenteSessioneResponse;
import it.unife.sample.backend.model.UtenteRegistrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// interfaccia per l'accesso ai dati degli utenti registrati: definisce i metodi per la gestione degli utenti nel database
public interface UtenteDao {

    Optional<UtenteSessioneResponse> findSessioneById(Long id); // restituisce le informazioni della sessione di un utente registrato dato il suo id
    Optional<UtenteProfiloResponse> findProfiloById(Long id); // restituisce le informazioni del profilo di un utente registrato dato il suo id
    Optional<UtentePublicoResponse> findPublicoById(Long id, long scambiCompletati); // restituisce le informazioni pubbliche di un utente registrato
    List<LeaderboardItemResponse> findLeaderboard(Long idQuartiere); // restituisce la classifica degli utenti registrati del quartiere in base al punteggio
    Optional<UtenteRegistrato> findEntityByEmail(String email); // restituisce l'entità di un utente registrato dato il suo indirizzo email
    Optional<UtenteRegistrato> findEntityById(Long id); // restituisce l'entità di un utente registrato dato il suo id
    boolean existsByEmail(String email); // verifica se esiste un utente registrato dato il suo indirizzo email
    UtenteSessioneResponse crea(RegistrazioneRequest req); // crea un nuovo utente registrato nel database e restituisce le informazioni della sessione
    UtenteSessioneResponse aggiorna(Long id, AggiornaUtenteRequest req); // aggiorna le informazioni di un utente registrato nel database e restituisce le informazioni della sessione
    void updateCo2AndPunteggio(Long id, BigDecimal co2DaAggiungere, int puntiDaAggiungere); // aggiorna punteggio e CO2 risparmiata di un utente registrato
    void updateUltimaVisitaProposte(Long id); // aggiorna la data e l'ora dell'ultima visita alle proposte di un utente registrato
    void updateUltimaVisitaChat(Long id); // aggiorna la data e l'ora dell'ultima visita alla chat di un utente registrato
    LocalDateTime getUltimaVisitaChat(Long id); // restituisce la data e l'ora dell'ultima visita alla chat di un utente registrato
    LocalDateTime getUltimaVisitaProposte(Long id); // restituisce la data e l'ora dell'ultima visita alle proposte di un utente registrato
    List<UtenteRegistrato> findByQuartiere(Long idQuartiere); // restituisce la lista degli utenti registrati che appartengono a un determinato quartiere
    long count(); // restituisce il numero totale di utenti registrati
    List<UtenteRegistrato> findAll(); // restituisce la lista di tutti gli utenti registrati
    List<UtenteRegistrato> findAllAdmin(); // restituisce la lista di tutti gli utenti registrati, compresi gli amministratori
    UtenteRegistrato blocca(Long id, boolean bloccato); // blocca o sblocca un utente registrato dato il suo id
}
