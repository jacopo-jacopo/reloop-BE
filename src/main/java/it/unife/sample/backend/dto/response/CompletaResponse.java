package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompletaResponse {
    private boolean completato;
    private Long idAltroUtente;
}
