package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviaSegnalazioneRequest {

    @NotNull
    private Long idAnnuncioSegnalato;

    @NotBlank
    private String motivazione;
}
