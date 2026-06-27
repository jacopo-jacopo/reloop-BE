package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecensioneResponse {

    private RecensoreResponse recensore;
    private Integer voto;
    private String descrizioneRecensione;

    @Data
    @AllArgsConstructor
    public static class RecensoreResponse {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
    }
}
