package it.unife.sample.backend.dto.request;

import it.unife.sample.backend.model.Annuncio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreaAnnuncioRequest {

    @NotBlank
    private String titolo;

    @NotBlank
    private String categoria;

    private String descrizioneAnnuncio = "";

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal prezzoStimato;

    @NotNull
    private Annuncio.Condizioni condizioni;

    private List<String> foto;
}
