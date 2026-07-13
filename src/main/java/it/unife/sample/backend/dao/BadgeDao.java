package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.BadgeOttenutoResponse;
import it.unife.sample.backend.dto.response.BadgeResponse;
import it.unife.sample.backend.model.Badge;

import java.util.List;

// interfaccia per l'accesso ai dati dei badge: definisce i metodi per la gestione dei badge nel database
public interface BadgeDao {

    List<BadgeOttenutoResponse> findByUtente(Long idUtente); // restituisce la lista dei badge ottenuti da un utente registrato dato il suo id
    List<BadgeResponse> findAll(); // restituisce la lista di tutti i badge disponibili
    List<Badge> findAllEntity(); // restituisce la lista di tutte le entità dei badge disponibili
    boolean giaOttenuto(Long idUtente, String nomeBadge); // verifica se un utente registrato ha già ottenuto un determinato badge
    void assegna(Long idUtente, String nomeBadge); // assegna un badge a un utente registrato
}
