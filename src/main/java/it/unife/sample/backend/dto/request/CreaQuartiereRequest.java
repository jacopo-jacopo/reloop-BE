package it.unife.sample.backend.dto.request;

import lombok.Data;

// rappresenta la richiesta per creare un nuovo quartiere, contiene il nome del quartiere e la città in cui si trova
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per i campi della classe
public class CreaQuartiereRequest {
    private String nomeQuartiere;
    private String citta;
}
