package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.service.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione delle notifiche: espone le API REST per ottenere le notifiche dell'utente loggato,
// ottenere il numero di notifiche non lette, segnare una notifica come letta e segnare tutte le notifiche come lette
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/notifiche") // mappa tutte le richieste HTTP che iniziano con /api/notifiche a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato 
public class NotificaController {

    private final NotificaService notificaService;

    // endpoint per ottenere le notifiche dell'utente loggato
    @GetMapping
    public List<NotificaResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return notificaService.getMie(idUtente);
    }

    // endpoint per ottenere il numero di notifiche non lette dell'utente loggato (per aggiungere il badge di notifica)
    @GetMapping("/badge")
    public long getBadge(@RequestHeader("X-User-Id") Long idUtente) {
        return notificaService.countNonLette(idUtente);
    }

    // endpoint per segnare una notifica come letta
    @PutMapping("/{id}/letta")
    public ResponseEntity<Void> segnaLetta(@PathVariable Long id) {
        notificaService.segnaLetta(id);
        return ResponseEntity.ok().build();
    }

    // endpoint per segnare tutte le notifiche dell'utente loggato come lette
    @PutMapping("/leggi-tutte")
    public ResponseEntity<Void> segnaTutteLette(@RequestHeader("X-User-Id") Long idUtente) {
        notificaService.segnaTutteLette(idUtente);
        return ResponseEntity.ok().build();
    }
}
