package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviaMessaggioRequest {

    @NotBlank
    private String contenuto;
}
