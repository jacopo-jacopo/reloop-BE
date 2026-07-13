package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta delle statistiche pubbliche:
// contiene il numero di scambi completati, la quantità totale di CO2 risparmiata e il numero di utenti attivi
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class StatsResponse {

    private long scambiCompletati;
    private double co2TotaleKg;
    private long utentiAttivi;
}
