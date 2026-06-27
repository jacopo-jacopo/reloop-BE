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

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public List<ChatResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return chatService.getMie(idUtente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getById(id));
    }

    @GetMapping("/{id}/messaggi")
    public List<MessaggioResponse> getMessaggi(@PathVariable Long id) {
        return chatService.getMessaggi(id);
    }

    @PostMapping("/{id}/messaggi")
    public ResponseEntity<MessaggioResponse> inviaMessaggio(
            @PathVariable Long id,
            @Valid @RequestBody InviaMessaggioRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.inviaMessaggio(id, idUtente, req));
    }

    @PutMapping("/{id}/completa")
    public ResponseEntity<CompletaResponse> completa(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.completa(id, idUtente));
    }

    @GetMapping("/non-letti")
    public NonLettiResponse getNonLetti(@RequestHeader("X-User-Id") Long idUtente) {
        return chatService.getNonLetti(idUtente);
    }

    @PutMapping("/{id}/leggi")
    public ResponseEntity<Void> leggi(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        chatService.leggi(id, idUtente);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/annulla")
    public ResponseEntity<ChatResponse> annulla(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(chatService.annulla(id, idUtente));
    }
}
