package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatResponse {

    private Long idChat;
    private String statoChat;
    private LocalDateTime dataCompletamento;
    private LocalDateTime timestampChat;
    private PropostaGeneranteSummary propostaGenerante;

    @Data
    @AllArgsConstructor
    public static class PropostaGeneranteSummary {
        private Long idProposta;
        private UtenteChatSummary proponente;
        private AnnuncioInteresseSummary annuncioInteresse;
        private List<AnnuncioInclusoSummary> annunciOfferti;
    }

    @Data
    @AllArgsConstructor
    public static class UtenteChatSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
    }

    @Data
    @AllArgsConstructor
    public static class AnnuncioInteresseSummary {
        private Long idAnnuncio;
        private String titolo;
        private UtenteChatSummary pubblicante;
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
    }
}
