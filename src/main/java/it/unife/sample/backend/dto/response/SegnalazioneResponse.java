package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO per la risposta delle segnalazioni: contiene le informazioni principali di una segnalazione, 
// inclusi i dettagli dell'annuncio segnalato e dell'amministratore che l'ha presa in carico
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class SegnalazioneResponse {

    private Long idSegnalazione;
    private String motivazione;
    private String statoSegnalazione;
    private LocalDateTime timestampSegnalazione;
    private AnnuncioSegnalatoSummary annuncioSegnalato;
    private AmministratoreSummary amministratore;

    // classe interna per rappresentare un riassunto dell'annuncio segnalato:
    // contiene le informazioni principali dell'annuncio e del pubblicante
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

    // classe interna per rappresentare un riassunto dell'autore della segnalazione:
    // contiene le informazioni principali dell'utente che ha inviato la segnalazione
    @Data
    @AllArgsConstructor
    public static class AutoreSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
    }

    // classe interna per rappresentare un riassunto dell'amministratore che ha preso in carico la segnalazione:
    // contiene le informazioni principali dell'utente amministratore
    @Data
    @AllArgsConstructor
    public static class AmministratoreSummary {
        private Long idUtenteAdm;
        private String nomeCompleto;
    }
}
