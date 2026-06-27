package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;
import it.unife.sample.backend.service.RecensioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recensioni")
@RequiredArgsConstructor
public class RecensioneController {

    private final RecensioneService recensioneService;

    @GetMapping("/{idUtente}")
    public List<RecensioneResponse> getByUtente(@PathVariable Long idUtente) {
        return recensioneService.getByUtente(idUtente);
    }

    @PostMapping
    public ResponseEntity<RecensioneResponse> invia(
            @Valid @RequestBody InviaRecensioneRequest req,
            @RequestHeader("X-User-Id") Long idRecensore) {
        return ResponseEntity.ok(recensioneService.invia(req, idRecensore));
    }
}
