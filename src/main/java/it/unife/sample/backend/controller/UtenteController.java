package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.response.*;
import it.unife.sample.backend.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @GetMapping("/me")
    public ResponseEntity<UtenteProfiloResponse> getMe(
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(utenteService.getMe(idUtente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtentePublicoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(utenteService.getById(id));
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardItemResponse> getLeaderboard() {
        return utenteService.getLeaderboard();
    }

    @GetMapping("/me/badge")
    public List<BadgeOttenutoResponse> getMieiBadge(
            @RequestHeader("X-User-Id") Long idUtente) {
        return utenteService.getMieiBadge(idUtente);
    }

    @GetMapping("/badge/tutti")
    public List<BadgeResponse> getTuttiBadge() {
        return utenteService.getTuttiBadge();
    }

    @GetMapping("/me/annunci")
    public List<AnnuncioResponse> getMieiAnnunci(
            @RequestHeader("X-User-Id") Long idUtente) {
        return utenteService.getMieiAnnunci(idUtente);
    }

    @PutMapping("/me")
    public ResponseEntity<UtenteSessioneResponse> aggiorna(
            @RequestHeader("X-User-Id") Long idUtente,
            @RequestBody AggiornaUtenteRequest req) {
        return ResponseEntity.ok(utenteService.aggiorna(idUtente, req));
    }

    @PutMapping("/visita-proposte")
    public ResponseEntity<Void> visitaProposte(@RequestHeader("X-User-Id") Long idUtente) {
        utenteService.visitaProposte(idUtente);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/visita-chat")
    public ResponseEntity<Void> visitaChat(@RequestHeader("X-User-Id") Long idUtente) {
        utenteService.visitaChat(idUtente);
        return ResponseEntity.ok().build();
    }
}
