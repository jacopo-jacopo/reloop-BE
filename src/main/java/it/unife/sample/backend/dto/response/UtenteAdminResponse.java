package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UtenteAdminResponse {
    private Long idUtenteReg;
    private String nomeCompleto;
    private String email;
    private boolean bloccato;
    private Integer punteggio;
    private QuartiereResponse quartiere;
}
