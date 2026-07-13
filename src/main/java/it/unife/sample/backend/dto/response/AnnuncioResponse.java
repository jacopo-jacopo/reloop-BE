package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// DTO per la risposta degli annunci: contiene le informazioni principali di un annuncio, inclusi i dettagli del pubblicante
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
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

    // classe interna per rappresentare un riassunto del pubblicante dell'annuncio: 
    // contiene le informazioni principali dell'utente che ha pubblicato l'annuncio
    @Data
    @AllArgsConstructor
    public static class PubblicanteSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
        private QuartiereResponse quartiere;
    }
}
