package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UtentePublicoResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String indirizzo;
    private Integer punteggio;
    private BigDecimal co2Totale;
    private String fotoProfilo;
    private QuartiereResponse quartiere;
    private Long scambiCompletati;
}
