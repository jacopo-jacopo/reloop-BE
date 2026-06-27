package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AnnuncioResponse {

    private Long idAnnuncio;
    private String titolo;
    private String descrizioneAnnuncio;
    private String categoria;
    private BigDecimal prezzoStimato;
    private String condizioni;
    private String statoAnnuncio;
    private boolean notificaOscuramentoLetta;
    private PubblicanteSummary pubblicante;

    @Data
    @AllArgsConstructor
    public static class PubblicanteSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
        private QuartiereResponse quartiere;
    }
}
