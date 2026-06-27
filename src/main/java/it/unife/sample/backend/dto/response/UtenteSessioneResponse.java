package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UtenteSessioneResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String fotoProfilo;
    private String indirizzo;
    private QuartiereResponse quartiere;
}
