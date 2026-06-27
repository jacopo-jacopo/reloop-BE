package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccettaPropostaRequest {

    @NotNull
    private Long idAnnuncioScelto;
}
