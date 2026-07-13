package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;

import java.util.List;

// interfaccia per l'accesso ai dati delle recensioni: definisce i metodi per la gestione delle recensioni nel database
public interface RecensioneDao {

    List<RecensioneResponse> findByRecensito(Long idUtente); // restituisce la lista delle recensioni ricevute da un utente
    RecensioneResponse salva(InviaRecensioneRequest req, Long idRecensore); // salva una nuova recensione nel database e restituisce la recensione salvata
}
