package it.unife.sample.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO per la richiesta di registrazione di un nuovo utente.
 * I @JsonProperty mappano i campi snake_case del JSON ai campi camelCase Java.
 */
@Data
public class RegistrazioneRequest {

    @NotBlank
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @NotBlank
    @JsonProperty("email")
    private String email;

    @NotBlank
    @JsonProperty("password")
    private String password;

    @NotBlank
    @JsonProperty("indirizzo")
    private String indirizzo;

    @NotNull
    @JsonProperty("id_quartiere")
    private Long idQuartiere;
}