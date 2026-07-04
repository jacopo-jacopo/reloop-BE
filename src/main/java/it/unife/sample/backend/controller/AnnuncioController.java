package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.AggiornaAnnuncioRequest;
import it.unife.sample.backend.dto.request.CreaAnnuncioRequest;
import it.unife.sample.backend.dto.response.AnnuncioResponse;
import it.unife.sample.backend.service.AnnuncioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller per la gestione degli annunci: espone le API REST per le CRUD sugli annunci
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/annunci") // mappa tutte le richieste HTTP che iniziano con /api/annunci a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, annuncioService)
public class AnnuncioController {

    private final AnnuncioService annuncioService;

    @GetMapping
    public List<AnnuncioResponse> getAll(
            @RequestParam(required = false) String cerca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long quartiere,
            @RequestParam(required = false) Integer limit,
            @RequestHeader("X-User-Id") Long idUtente) {
        return annuncioService.getAll(cerca, categoria, quartiere, limit, idUtente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnuncioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(annuncioService.getById(id));
    }

    @GetMapping("/{id}/foto")
    public List<String> getFoto(@PathVariable Long id) {
        return annuncioService.getFoto(id);
    }

    @PostMapping
    public ResponseEntity<AnnuncioResponse> crea(
            @Valid @RequestBody CreaAnnuncioRequest req,
            @RequestHeader("X-User-Id") Long idUtente) {
        return ResponseEntity.ok(annuncioService.crea(idUtente, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnuncioResponse> aggiorna(
            @PathVariable Long id,
            @RequestBody AggiornaAnnuncioRequest req) {
        return ResponseEntity.ok(annuncioService.aggiorna(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        annuncioService.elimina(id);
        return ResponseEntity.ok().build();
    }
}
