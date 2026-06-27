package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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
