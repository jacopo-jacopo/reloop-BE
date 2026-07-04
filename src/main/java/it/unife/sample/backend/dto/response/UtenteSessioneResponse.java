package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta della sessione dell'utente registrato: contiene le informazioni dell'utente e della sua sessione
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class UtenteSessioneResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String fotoProfilo;
    private String indirizzo;
    private QuartiereResponse quartiere;
}
