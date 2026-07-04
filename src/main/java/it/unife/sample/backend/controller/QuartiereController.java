package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.request.AggiornaQuartiereRequest;
import it.unife.sample.backend.dto.request.CreaQuartiereRequest;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.service.QuartiereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// controller per la gestione dei quartieri: fornisce endpoint per ottenere tutti i quartieri, 
// creare un nuovo quartiere e aggiornare un quartiere esistente
@RestController
@RequestMapping("/api/quartieri") // mappa le richieste HTTP a /api/quartieri a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, quartiereService)
public class QuartiereController {

    private final QuartiereService quartiereService;

    @GetMapping // mappa le richieste GET a /api/quartieri a questo metodo
    public List<QuartiereResponse> getAll() {
        return quartiereService.findAll();
    }

    @PostMapping // mappa le richieste POST a /api/quartieri a questo metodo
    public ResponseEntity<QuartiereResponse> crea(@RequestBody CreaQuartiereRequest req) { // estrae il corpo della richiesta e lo 
                                                                                           // deserializza in un oggetto CreaQuartiereRequest
        return ResponseEntity.ok(quartiereService.crea(req));
    }

    @PutMapping("/{id}") // mappa le richieste PUT a /api/quartieri/{id} a questo metodo
    public ResponseEntity<QuartiereResponse> aggiorna(
            @PathVariable Long id, // estrae il parametro {id} dall'URL e lo passa come argomento al metodo
            @RequestBody AggiornaQuartiereRequest req) {  // estrae il corpo della richiesta e lo deserializza in un oggetto AggiornaQuartiereRequest
        return ResponseEntity.ok(quartiereService.aggiorna(id, req));
    }
}
