package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LeaderboardItemResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String fotoProfilo;
    private Integer punteggio;
    private BigDecimal co2Totale;
}
