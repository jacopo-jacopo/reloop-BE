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

@RestController
@RequestMapping("/api/proposte")
@RequiredArgsConstructor
public class PropostaController {

    private final PropostaService propostaService;

    @GetMapping("/badge")
    public long getBadge(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getBadge(idUtente);
    }

    @GetMapping("/ricevute")
    public List<PropostaResponse> getRicevute(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getRicevute(idUtente);
    }

    @GetMapping("/inviate")
    public List<PropostaResponse> getInviate(@RequestHeader("X-User-Id") Long idUtente) {
        return propostaService.getInviate(idUtente);
    }

    @PostMapping
    public ResponseEntity<PropostaResponse> invia(
            @Valid @RequestBody InviaPropostaRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(propostaService.invia(req, idUtente));
    }

    @PutMapping("/{id}/accetta")
    public ResponseEntity<ChatResponse> accetta(
            @PathVariable Long id,
            @Valid @RequestBody AccettaPropostaRequest req) {
        return ResponseEntity.ok(propostaService.accetta(id, req));
    }

    @PutMapping("/{id}/rifiuta")
    public ResponseEntity<PropostaResponse> rifiuta(@PathVariable Long id) {
        return ResponseEntity.ok(propostaService.rifiuta(id));
    }
}
