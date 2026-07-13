package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO per la risposta delle proposte: 
// contiene le informazioni principali di una proposta, inclusi i dettagli del proponente, dell'annuncio di interesse e degli annunci offerti
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class PropostaResponse {

    private Long idProposta;
    private String statoProposta;
    private LocalDateTime timestampProposta;
    private UtentePropostaSummary proponente;
    private AnnuncioInteresseSummary annuncioInteresse;
    private List<AnnuncioInclusoSummary> annunciOfferti;

    // classe interna per rappresentare un riassunto dell'utente proponente della proposta
    @Data
    @AllArgsConstructor
    public static class UtentePropostaSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
    }

    // classe interna per rappresentare un riassunto dell'annuncio di interesse della proposta
    @Data
    @AllArgsConstructor
    public static class AnnuncioInteresseSummary {
        private Long idAnnuncio;
        private String titolo;
        private BigDecimal prezzoStimato;
        private UtentePropostaSummary pubblicante;
    }

    // classe interna per rappresentare un riassunto di un annuncio incluso nella proposta
    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoSummary {
        private AnnuncioInclusoIdDto id;
        private Boolean flagSelezionato;
        private AnnuncioOffertoSummary annuncioOfferto;
    }

    // classe interna per rappresentare un DTO contenente gli ID di una proposta e di un annuncio offerto
    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoIdDto {
        private Long idProposta;
        private Long idAnnuncioOfferto;
    }

    // classe interna per rappresentare un riassunto di un annuncio offerto nella proposta
    @Data
    @AllArgsConstructor
    public static class AnnuncioOffertoSummary {
        private Long idAnnuncio;
        private String titolo;
        private BigDecimal prezzoStimato;
    }
}
