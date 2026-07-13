package it.unife.sample.backend.dto.request;

import lombok.Data;

// DTO per la richiesta di chiusura di una segnalazione
@Data
public class ChiudiSegnalazioneRequest {

    private boolean oscuraAnnuncio = false; // campo booleano che indica se l'annuncio segnalato deve essere oscurato o meno
}
