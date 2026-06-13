package it.unife.sample.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO per la richiesta di registrazione di un nuovo utente.
 * I @JsonProperty mappano i campi snake_case del JSON ai campi camelCase Java.
 */
@Data
public class RegistrazioneRequest {

    // Nome completo dell'utente — campo obbligatorio
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    // Email univoca — usata per il login
    @JsonProperty("email")
    private String email;

    // Password in chiaro — in produzione usare BCrypt
    @JsonProperty("password")
    private String password;

    // Indirizzo fisico dell'utente nel quartiere
    @JsonProperty("indirizzo")
    private String indirizzo;

    // ID del quartiere selezionato durante la registrazione
    @JsonProperty("id_quartiere")
    private Long idQuartiere;
}