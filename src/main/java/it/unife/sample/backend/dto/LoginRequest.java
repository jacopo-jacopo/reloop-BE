package it.unife.sample.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO per la richiesta di login.
 * Contiene solo email e password.
 */
@Data
public class LoginRequest {

    // Email dell'utente o dell'amministratore
    @JsonProperty("email")
    private String email;

    // Password in chiaro
    @JsonProperty("password")
    private String password;
}