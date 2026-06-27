package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.response.StatsResponse;
import it.unife.sample.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/pubbliche")
    public StatsResponse getPubbliche() {
        return statsService.getPubbliche();
    }

    @GetMapping("/co2-quartiere")
    public BigDecimal getCo2Quartiere(@RequestParam Long quartiere) {
        return statsService.getCo2Quartiere(quartiere);
    }
}
