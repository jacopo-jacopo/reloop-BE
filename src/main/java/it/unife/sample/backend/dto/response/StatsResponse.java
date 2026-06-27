package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponse {

    private long scambiCompletati;
    private double co2TotaleKg;
    private long utentiAttivi;
}
