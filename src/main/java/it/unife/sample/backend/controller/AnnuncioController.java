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

@RestController
@RequestMapping("/api/annunci")
@RequiredArgsConstructor
public class AnnuncioController {

    private final AnnuncioService annuncioService;

    @GetMapping
    public List<AnnuncioResponse> getAll(
            @RequestParam(required = false) String cerca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long quartiere,
            @RequestHeader("X-User-Id") Long idUtente) {
        return annuncioService.getAll(cerca, categoria, quartiere, idUtente);
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
