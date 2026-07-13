package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.request.BloccaUtenteRequest;
import it.unife.sample.backend.dto.response.*;
import it.unife.sample.backend.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione degli utenti: espone le API REST per ottenere informazioni sugli utenti, aggiornare il profilo dell'utente loggato, 
// ottenere la classifica degli utenti, ottenere i badge ottenuti dall'utente loggato e bloccare/sbloccare un utente (solo per admin)
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/utenti") // mappa tutte le richieste HTTP che iniziano con /api/utenti a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, utenteService)
public class UtenteController {

    private final UtenteService utenteService;

    // endpoint per ottenere le informazioni del profilo dell'utente loggato: 
    // riceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio UtenteService
    @GetMapping("/me") 
    public ResponseEntity<UtenteProfiloResponse> getMe(
            @RequestHeader("X-User-Id") Long idUtente) { 
        return ResponseEntity.ok(utenteService.getMe(idUtente));
    }

    // endpoint per ottenere le informazioni pubbliche di un utente: 
    // riceve l'id dell'utente come parametro della richiesta e chiama il servizio UtenteService
    @GetMapping("/{id}")
    public ResponseEntity<UtentePublicoResponse> getById(@PathVariable Long id) {  // pathVariable indica che il parametro id 
                                                                                   // viene estratto dall'URL della richiesta
        return ResponseEntity.ok(utenteService.getById(id));
    }

    // endpoint per ottenere la classifica degli utenti: chiama il servizio UtenteService
    @GetMapping("/leaderboard")
    public List<LeaderboardItemResponse> getLeaderboard() {
        return utenteService.getLeaderboard();
    }

    // endpoint per ottenere i badge ottenuti dall'utente loggato:
    // riceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio UtenteService
    @GetMapping("/me/badge")
    public List<BadgeOttenutoResponse> getMieiBadge(
            @RequestHeader("X-User-Id") Long idUtente) {
        return utenteService.getMieiBadge(idUtente);
    }

    // endpoint per ottenere tutti i badge disponibili: chiama il servizio UtenteService
    @GetMapping("/badge/tutti")
    public List<BadgeResponse> getTuttiBadge() {
        return utenteService.getTuttiBadge();
    }

    // endpoint per ottenere gli annunci pubblicati dall'utente loggato:
    // riceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio UtenteService
    @GetMapping("/me/annunci")
    public List<AnnuncioResponse> getMieiAnnunci(
            @RequestHeader("X-User-Id") Long idUtente) {
        return utenteService.getMieiAnnunci(idUtente);
    }

    // endpoint per aggiornare le informazioni del profilo dell'utente loggato:
    // riceve l'id dell'utente loggato dall'header della richiesta e i dati da aggiornare nel corpo della richiesta, e chiama il servizio UtenteService
    @PutMapping("/me")
    public ResponseEntity<UtenteSessioneResponse> aggiorna(
            @RequestHeader("X-User-Id") Long idUtente,
            @RequestBody AggiornaUtenteRequest req) {
        return ResponseEntity.ok(utenteService.aggiorna(idUtente, req));
    }

    // endpoint per aggiornare la data dell'ultima visita dell'utente loggato alla sezione delle proposte:
    // riceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio UtenteService
    @PutMapping("/visita-proposte")
    public ResponseEntity<Void> visitaProposte(@RequestHeader("X-User-Id") Long idUtente) {
        utenteService.visitaProposte(idUtente);
        return ResponseEntity.ok().build(); // build crea una risposta HTTP senza body (void)
    }

    // endpoint per aggiornare la data dell'ultima visita dell'utente loggato alla sezione delle chat:
    // riceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio UtenteService
    @PutMapping("/visita-chat")
    public ResponseEntity<Void> visitaChat(@RequestHeader("X-User-Id") Long idUtente) {
        utenteService.visitaChat(idUtente);
        return ResponseEntity.ok().build(); // build crea una risposta HTTP senza body (void)
    }

    // endpoint per ottenere tutti gli utenti amministratori: chiama il servizio UtenteService
    @GetMapping // mappa le richieste GET a /api/utenti a questo metodo
    public List<UtenteAdminResponse> getAllAdmin() {
        return utenteService.getAllAdmin();
    }

    // endpoint per bloccare o sbloccare un utente: 
    // riceve l'id dell'utente da bloccare/sbloccare come parametro dell'URL della richiesta
    // e lo stato di blocco nel body della richiesta, poi chiama il servizio UtenteService
    @PutMapping("/{id}/blocca")
    public ResponseEntity<UtenteAdminResponse> blocca(
            @PathVariable Long id,
            @RequestBody BloccaUtenteRequest req) {
        return ResponseEntity.ok(utenteService.blocca(id, req.isBloccato()));
    }
}
