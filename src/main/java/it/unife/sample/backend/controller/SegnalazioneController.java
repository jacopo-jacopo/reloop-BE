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

// controller per la gestione delle segnalazioni: espone le API REST per ottenere tutte le segnalazioni, ottenere le segnalazioni dell'utente loggato,
// inviare una nuova segnalazione, prendere in carico una segnalazione e chiudere una segnalazione (ultime due solo per admin)
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/segnalazioni") // mappa tutte le richieste HTTP che iniziano con /api/segnalazioni a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato 
public class SegnalazioneController {

    private final SegnalazioneService segnalazioneService;

    // endpoint per ottenere tutte le segnalazioni: chiama il servizio SegnalazioneService
    @GetMapping
    public List<SegnalazioneResponse> getTutte() {
        return segnalazioneService.getTutte();
    }

    // endpoint per ottenere le segnalazioni dell'utente loggato: r
    // iceve l'id dell'utente loggato dall'header della richiesta e chiama il servizio SegnalazioneService
    @GetMapping("/mie")
    public List<SegnalazioneResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return segnalazioneService.getMie(idUtente);
    }

    // endpoint per inviare una nuova segnalazione: 
    // riceve l'id dell'utente loggato dall'header della richiesta e i dati della segnalazione nel corpo della richiesta
    @PostMapping
    public ResponseEntity<SegnalazioneResponse> invia(
            @Valid @RequestBody InviaSegnalazioneRequest req, // annotazione @Valid per validare automaticamente i campi della richiesta 
                                                              // in base alle regole (@NotNull) definite nella classe InviaSegnalazioneRequest
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(segnalazioneService.invia(req, idUtente));
    }

    // endpoint per prendere in carico una segnalazione:
    // riceve l'id della segnalazione come parametro della richiesta e l'id dell'admin dall'header della richiesta, poi chiama il servizio
    @PutMapping("/{id}/carico")
    public ResponseEntity<SegnalazioneResponse> prendiInCarico(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idAdmin) {
        return ResponseEntity.ok(segnalazioneService.prendiInCarico(id, idAdmin));
    }

    // endpoint per chiudere una segnalazione:
    // riceve l'id della segnalazione come parametro della richiesta, i dati della richiesta nel body e l'id dell'admin dall'header, poi chiama il servizio
    @PutMapping("/{id}/chiudi")
    public ResponseEntity<SegnalazioneResponse> chiudi(
            @PathVariable Long id,
            @RequestBody ChiudiSegnalazioneRequest req,
            @RequestHeader("X-User-Id") Long idAdmin) {
        return ResponseEntity.ok(segnalazioneService.chiudi(id, req, idAdmin));
    }
}
