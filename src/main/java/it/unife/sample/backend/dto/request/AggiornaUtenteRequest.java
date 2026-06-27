package it.unife.sample.backend.dto.request;

import lombok.Data;

@Data
public class AggiornaUtenteRequest {

    private String nomeCompleto;
    private String indirizzo;
    private String password;
    private String fotoProfilo;
    private QuartiereRef quartiere;

    @Data
    public static class QuartiereRef {
        private Long idQuartiere;
    }
}
