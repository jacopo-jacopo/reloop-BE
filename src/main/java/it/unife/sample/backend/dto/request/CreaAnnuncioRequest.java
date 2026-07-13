package it.unife.sample.backend.dto.request;

import it.unife.sample.backend.model.Annuncio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// DTO per la richiesta di creazione di un annuncio
@Data // annotazione Lombok che genera automaticamente i metodi getter, setter, equals, hashCode e toString
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

    @NotEmpty
    private List<String> foto;
}
