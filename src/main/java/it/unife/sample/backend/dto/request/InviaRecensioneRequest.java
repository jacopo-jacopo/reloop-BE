package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO per la richiesta di invio di una recensione
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
public class InviaRecensioneRequest {

    @NotNull
    private Long idUtenteRegRecensito;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;

    @NotBlank
    private String descrizioneRecensione;

    private Long idChat;
}
