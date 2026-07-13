package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta delle statistiche dell'admin
// contiene il numero di segnalazioni in attesa, il numero di segnalazioni chiuse e il numero di utenti bloccati
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class AdminStatsResponse {
    private long segnalazioniInAttesa;
    private long segnalazioniChiuse;
    private long utentiBloccati;
}
