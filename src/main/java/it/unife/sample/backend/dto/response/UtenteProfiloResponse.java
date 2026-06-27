package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UtenteProfiloResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private Integer punteggio;
    private BigDecimal co2Totale;
    private QuartiereResponse quartiere;
}
