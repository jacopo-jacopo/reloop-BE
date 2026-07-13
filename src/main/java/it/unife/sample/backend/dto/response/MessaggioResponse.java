package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// DTO per la risposta di un messaggio in una chat: contiene informazioni sul messaggio, sul mittente e sulla chat
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class MessaggioResponse {

    private MessaggioIdDto id; // chiave composta del messaggio: contiene l'id del messaggio e l'id della chat a cui appartiene
    private String contenuto;
    private LocalDateTime dataInvio;
    private Boolean flagLettura;
    private MittenteSummary mittente;

    // DTO per la chiave composta
    @Data
    @AllArgsConstructor
    public static class MessaggioIdDto {
        private Long idMessaggio;
        private Long idChat;
    }

    // classe interna per rappresentare un riassunto del mittente del messaggio: 
    // contiene le informazioni principali dell'utente che ha inviato il messaggio
    @Data
    @AllArgsConstructor
    public static class MittenteSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
    }
}
