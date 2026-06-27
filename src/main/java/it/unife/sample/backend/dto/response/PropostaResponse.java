package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PropostaResponse {

    private Long idProposta;
    private String statoProposta;
    private LocalDateTime timestampProposta;
    private UtentePropostaSummary proponente;
    private AnnuncioInteresseSummary annuncioInteresse;
    private List<AnnuncioInclusoSummary> annunciOfferti;

    @Data
    @AllArgsConstructor
    public static class UtentePropostaSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
    }

    @Data
    @AllArgsConstructor
    public static class AnnuncioInteresseSummary {
        private Long idAnnuncio;
        private String titolo;
        private BigDecimal prezzoStimato;
        private UtentePropostaSummary pubblicante;
    }

    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoSummary {
        private AnnuncioInclusoIdDto id;
        private Boolean flagSelezionato;
        private AnnuncioOffertoSummary annuncioOfferto;
    }

    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoIdDto {
        private Long idProposta;
        private Long idAnnuncioOfferto;
    }

    @Data
    @AllArgsConstructor
    public static class AnnuncioOffertoSummary {
        private Long idAnnuncio;
        private String titolo;
        private BigDecimal prezzoStimato;
    }
}
