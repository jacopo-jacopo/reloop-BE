package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.AccettaPropostaRequest;
import it.unife.sample.backend.dto.request.InviaPropostaRequest;
import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.dto.response.PropostaResponse;
import it.unife.sample.backend.service.PropostaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione delle proposte: espone le API REST per ottenere le proposte ricevute e inviate dall'utente loggato,
// inviare una nuova proposta, accettare o rifiutare una proposta ricevuta
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/proposte") // mappa tutte le richieste HTTP che iniziano con /api/proposte a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, propostaService)
public class PropostaController {

    private final PropostaService propostaService;

    // endpoint per ottenere il badge che indica se l'utente ha ricevuto nuove proposte
    @GetMapping("/badge")
    public long getBadge(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getBadge(idUtente);
    }

    // endpoint per ottenere le proposte ricevute dall'utente loggato
    @GetMapping("/ricevute")
    public List<PropostaResponse> getRicevute(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getRicevute(idUtente);
    }

    // endpoint per ottenere le proposte inviate dall'utente loggato
    @GetMapping("/inviate")
    public List<PropostaResponse> getInviate(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getInviate(idUtente);
    }

    // endpoint per inviare una nuova proposta
    @PostMapping
    public ResponseEntity<PropostaResponse> invia(
            @Valid @RequestBody InviaPropostaRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(propostaService.invia(req, idUtente));
    }

    // endpoint per accettare una proposta ricevuta
    @PutMapping("/{id}/accetta")
    public ResponseEntity<ChatResponse> accetta(
            @PathVariable Long id,
            @Valid @RequestBody AccettaPropostaRequest req) {
        return ResponseEntity.ok(propostaService.accetta(id, req));
    }

    // endpoint per rifiutare una proposta ricevuta
    @PutMapping("/{id}/rifiuta")
    public ResponseEntity<PropostaResponse> rifiuta(@PathVariable Long id) {
        return ResponseEntity.ok(propostaService.rifiuta(id));
    }
}
