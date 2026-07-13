package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;
import it.unife.sample.backend.service.RecensioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione delle recensioni: espone le API REST per ottenere le recensioni di un utente e inviare una nuova recensione
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/recensioni") // mappa tutte le richieste HTTP che iniziano con /api/recensioni a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class RecensioneController {

    private final RecensioneService recensioneService;

    // endpoint per ottenere le recensioni di un utente: 
    // riceve l'id dell'utente come parametro dell'URL e chiama il servizio RecensioneService
    @GetMapping("/{idUtente}")
    public List<RecensioneResponse> getByUtente(@PathVariable Long idUtente) {
        return recensioneService.getByUtente(idUtente);
    }

    // endpoint per inviare una nuova recensione:
    // riceve l'id dell'utente loggato dall'header della richiesta e i dati della recensione (validati) nel body, poi chiama RecensioneService
    @PostMapping
    public ResponseEntity<RecensioneResponse> invia(
            @Valid @RequestBody InviaRecensioneRequest req,
            @RequestHeader("X-User-Id") Long idRecensore) {
        return ResponseEntity.ok(recensioneService.invia(req, idRecensore));
    }
}
