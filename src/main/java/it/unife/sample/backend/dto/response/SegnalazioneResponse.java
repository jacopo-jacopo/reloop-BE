package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SegnalazioneResponse {

    private Long idSegnalazione;
    private String motivazione;
    private String statoSegnalazione;
    private LocalDateTime timestampSegnalazione;
    private AnnuncioSegnalatoSummary annuncioSegnalato;
    private AmministratoreSummary amministratore;

    @Data
    @AllArgsConstructor
    public static class AnnuncioSegnalatoSummary {
        private Long idAnnuncio;
        private String titolo;
        private String categoria;
        private String condizioni;
        private BigDecimal prezzoStimato;
        private String descrizioneAnnuncio;
        private AutoreSummary pubblicante;
    }

    @Data
    @AllArgsConstructor
    public static class AutoreSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
    }

    @Data
    @AllArgsConstructor
    public static class AmministratoreSummary {
        private Long idUtenteAdm;
        private String nomeCompleto;
    }
}
