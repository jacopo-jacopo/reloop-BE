package it.unife.sample.backend.dto.request;

import it.unife.sample.backend.model.Annuncio;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggiornaAnnuncioRequest {

    private String titolo;
    private String descrizioneAnnuncio;
    private String categoria;
    private BigDecimal prezzoStimato;
    private Annuncio.Condizioni condizioni;
    private Annuncio.StatoAnnuncio statoAnnuncio;
    private Boolean notificaOscuramentoLetta;
}
