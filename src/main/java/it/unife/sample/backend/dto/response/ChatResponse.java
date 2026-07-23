package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// DTO per la risposta delle chat: contiene le informazioni principali di una chat, inclusi i dettagli della proposta generante e degli annunci coinvolti
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class ChatResponse {

    private Long idChat;
    private String statoChat;
    private LocalDateTime dataCompletamento;
    private LocalDateTime timestampChat;
    private PropostaGeneranteSummary propostaGenerante;
    private boolean confermatoPubblicante;
    private boolean confermatoProponente;

    // classe interna per rappresentare un riassunto della proposta generante della chat: 
    // contiene le informazioni principali della proposta, del proponente e degli annunci coinvolti
    @Data
    @AllArgsConstructor
    public static class PropostaGeneranteSummary {
        private Long idProposta;
        private UtenteChatSummary proponente;
        private AnnuncioInteresseSummary annuncioInteresse;
        private List<AnnuncioInclusoSummary> annunciOfferti;
    }

    // classe interna per rappresentare un riassunto dell'utente coinvolto nella chat: 
    // contiene le informazioni principali dell'utente, come id, nome completo e foto profilo
    @Data
    @AllArgsConstructor
    public static class UtenteChatSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
        private String indirizzo;
    }

    // classe interna per rappresentare un riassunto dell'annuncio di interesse della chat: 
    // contiene le informazioni principali dell'annuncio, come id, titolo e pubblicante
    @Data
    @AllArgsConstructor
    public static class AnnuncioInteresseSummary {
        private Long idAnnuncio;
        private String titolo;
        private UtenteChatSummary pubblicante;
    }

    // classe interna per rappresentare un riassunto di un annuncio incluso nella proposta generante della chat: 
    // contiene le informazioni principali dell'annuncio offerto, come id, titolo e flag di selezione
    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoSummary {
        private AnnuncioInclusoIdDto id;
        private Boolean flagSelezionato;
        private AnnuncioOffertoSummary annuncioOfferto;
    }

    // classe interna per rappresentare l'identificativo di un annuncio incluso nella proposta generante della chat: 
    // contiene l'id della proposta e l'id dell'annuncio offerto
    @Data
    @AllArgsConstructor
    public static class AnnuncioInclusoIdDto {
        private Long idProposta;
        private Long idAnnuncioOfferto;
    }

    // classe interna per rappresentare un riassunto di un annuncio offerto nella proposta generante della chat: 
    // contiene le informazioni principali dell'annuncio, come id e titolo
    @Data
    @AllArgsConstructor
    public static class AnnuncioOffertoSummary {
        private Long idAnnuncio;
        private String titolo;
    }
}
