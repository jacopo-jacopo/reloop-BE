package it.unife.sample.backend.dto.request;

import it.unife.sample.backend.model.Annuncio;
import lombok.Data;

import java.math.BigDecimal;

// rappresenta la richiesta per aggiornare un annuncio esistente, contiene i campi che possono essere modificati dell'annuncio
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per i campi della classe
public class AggiornaAnnuncioRequest {

    private String titolo;
    private String descrizioneAnnuncio;
    private String categoria;
    private BigDecimal prezzoStimato;
    private Annuncio.Condizioni condizioni;
    private Annuncio.StatoAnnuncio statoAnnuncio;
    private Boolean notificaOscuramentoLetta;
}
