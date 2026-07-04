package it.unife.sample.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO per la richiesta di login: contiene i dati dell'utente (email e password) necessari per effettuare il login
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
public class LoginRequest {

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("email") // indica che il campo deve essere deserializzato dal JSON con il nome "email"
    private String email;

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("password") // indica che il campo deve essere deserializzato dal JSON con il nome "password"
    private String password;
}