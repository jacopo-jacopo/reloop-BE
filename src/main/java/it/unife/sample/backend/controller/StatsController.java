package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.response.AdminStatsResponse;
import it.unife.sample.backend.dto.response.StatsResponse;
import it.unife.sample.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

// controller per la gestione delle statistiche: 
// espone le API REST per ottenere le statistiche pubbliche, le statistiche per quartiere e le statistiche per l'admin
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/stats") // mappa tutte le richieste HTTP che iniziano con /api/stats a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, statsService)
public class StatsController {

    private final StatsService statsService;

    // endpoint per ottenere le statistiche pubbliche
    @GetMapping("/pubbliche")
    public StatsResponse getPubbliche() {
        return statsService.getPubbliche();
    }

    // endpoint per ottenere le statistiche per quartiere
    @GetMapping("/co2-quartiere")
    public BigDecimal getCo2Quartiere(@RequestParam Long quartiere) {
        return statsService.getCo2Quartiere(quartiere);
    }

    // endpoint per ottenere le statistiche per l'admin
    @GetMapping("/admin")
    public AdminStatsResponse getAdmin() {
        return statsService.getAdmin();
    }
}
