package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsResponse {
    private long segnalazioniInAttesa;
    private long segnalazioniChiuse;
    private long utentiBloccati;
}
