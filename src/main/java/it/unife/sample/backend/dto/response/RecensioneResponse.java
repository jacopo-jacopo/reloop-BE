package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta delle recensioni: contiene le informazioni principali di una recensione, inclusi il recensore, il voto e la descrizione
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class RecensioneResponse {

    private RecensoreResponse recensore;
    private Integer voto;
    private String descrizioneRecensione;


    // classe interna per rappresentare il recensore di una recensione: 
    // contiene le informazioni principali dell'utente che ha scritto la recensione
    @Data
    @AllArgsConstructor
    public static class RecensoreResponse {
        private Long idUtenteReg;
        private String nomeCompleto;
        private String fotoProfilo;
    }
}
