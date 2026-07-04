package it.unife.sample.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

// DTO per la risposta al login: contiene le informazioni dell'utente e della sessione
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString
@AllArgsConstructor // genera automaticamente un costruttore con tutti i campi come parametri
public class LoginResponse {
    private String tipo;
    private Long id;
    private String nomeCompleto;
    private String email;
    private Object utente; 
}
