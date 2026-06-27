package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSessioneResponse {

    private Long idUtenteAdm;
    private String nomeCompleto;
}
