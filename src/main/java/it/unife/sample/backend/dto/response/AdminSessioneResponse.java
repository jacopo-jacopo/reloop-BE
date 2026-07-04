package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta della sessione dell'amministratore: contiene l'id e il nome completo dell'amministratore
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString
@AllArgsConstructor // genera automaticamente un costruttore con tutti i campi come parametri
public class AdminSessioneResponse {

    private Long idUtenteAdm;
    private String nomeCompleto;
}
