package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuartiereResponse {
    private Long idQuartiere;
    private String nomeQuartiere;
    private String citta;
}
