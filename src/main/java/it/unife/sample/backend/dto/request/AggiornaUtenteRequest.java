package it.unife.sample.backend.dto.request;

import lombok.Data;

// DTO per la richiesta di aggiornamento dei dati dell'utente registrato: contiene i campi che possono essere aggiornati dall'utente stesso
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
public class AggiornaUtenteRequest {

    private String nomeCompleto;
    private String indirizzo;
    private String password;
    private String fotoProfilo;
    private QuartiereRef quartiere;

    // DTO per la rappresentazione del quartiere a cui appartiene l'utente registrato: contiene solo l'id del quartiere
    @Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
    public static class QuartiereRef {
        private Long idQuartiere;
    }
}
