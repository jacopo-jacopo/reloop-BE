package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;
import it.unife.sample.backend.service.SegnalazioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/segnalazioni")
@RequiredArgsConstructor
public class SegnalazioneController {

    private final SegnalazioneService segnalazioneService;

    @GetMapping
    public List<SegnalazioneResponse> getTutte() {
        return segnalazioneService.getTutte();
    }

    @GetMapping("/mie")
    public List<SegnalazioneResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return segnalazioneService.getMie(idUtente);
    }

    @PostMapping
    public ResponseEntity<SegnalazioneResponse> invia(
            @Valid @RequestBody InviaSegnalazioneRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(segnalazioneService.invia(req, idUtente));
    }

    @PutMapping("/{id}/carico")
    public ResponseEntity<SegnalazioneResponse> prendiInCarico(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idAdmin) {
        return ResponseEntity.ok(segnalazioneService.prendiInCarico(id, idAdmin));
    }

    @PutMapping("/{id}/chiudi")
    public ResponseEntity<SegnalazioneResponse> chiudi(
            @PathVariable Long id,
            @RequestBody ChiudiSegnalazioneRequest req,
            @RequestHeader("X-User-Id") Long idAdmin) {
        return ResponseEntity.ok(segnalazioneService.chiudi(id, req, idAdmin));
    }
}
