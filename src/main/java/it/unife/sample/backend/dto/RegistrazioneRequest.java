package it.unife.sample.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO per la richiesta di registrazione: contiene i dati dell'utente da registrare, con le annotazioni per la validazione dei campi
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
public class RegistrazioneRequest {

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("nome_completo") // indica il nome del campo nel JSON della richiesta
    private String nomeCompleto;

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("email") // indica il nome del campo nel JSON della richiesta
    private String email;

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("password") // indica il nome del campo nel JSON della richiesta
    private String password;

    @NotBlank // indica che il campo non può essere vuoto o nullo
    @JsonProperty("indirizzo") // indica il nome del campo nel JSON della richiesta
    private String indirizzo;

    @NotNull // indica che il campo non può essere vuoto o nullo
    @JsonProperty("id_quartiere") // indica il nome del campo nel JSON della richiesta
    private Long idQuartiere;
}