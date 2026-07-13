package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.InviaMessaggioRequest;
import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.dto.response.CompletaResponse;
import it.unife.sample.backend.dto.response.MessaggioResponse;
import it.unife.sample.backend.dto.response.NonLettiResponse;
import it.unife.sample.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione delle chat: espone le API REST per ottenere le chat dell'utente loggato, ottenere i messaggi di una chat, 
// inviare un messaggio, completare una chat, ottenere il numero di messaggi non letti e segnare una chat come letta
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/chat") // mappa tutte le richieste HTTP che iniziano con /api/chat a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato 
public class ChatController {

    private final ChatService chatService;

    // endpoint per ottenere le chat dell'utente loggato
    @GetMapping
    public List<ChatResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return chatService.getMie(idUtente);
    }

    // endpoint per ottenere le informazioni di una chat specifica
    @GetMapping("/{id}")
    public ResponseEntity<ChatResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getById(id));
    }

    // endpoint per ottenere i messaggi di una chat specifica
    @GetMapping("/{id}/messaggi")
    public List<MessaggioResponse> getMessaggi(@PathVariable Long id) {
        return chatService.getMessaggi(id);
    }

    // endpoint per inviare un messaggio in una chat specifica
    @PostMapping("/{id}/messaggi")
    public ResponseEntity<MessaggioResponse> inviaMessaggio(
            @PathVariable Long id,
            @Valid @RequestBody InviaMessaggioRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.inviaMessaggio(id, idUtente, req));
    }

    // endpoint per completare una chat specifica
    @PutMapping("/{id}/completa")
    public ResponseEntity<CompletaResponse> completa(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.completa(id, idUtente));
    }

    // endpoint per ottenere il numero di messaggi non letti e le chat vuote dell'utente loggato
    @GetMapping("/non-letti")
    public NonLettiResponse getNonLetti(@RequestHeader("X-User-Id") Long idUtente) {
        return chatService.getNonLetti(idUtente);
    }

    // endpoint per segnare i messaggi di una chat come letti
    @PutMapping("/{id}/leggi")
    public ResponseEntity<Void> leggi(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        chatService.leggi(id, idUtente);
        return ResponseEntity.ok().build();
    }

    // endpoint per annullare una chat specifica
    @PutMapping("/{id}/annulla")
    public ResponseEntity<ChatResponse> annulla(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.annulla(id, idUtente));
    }
}
