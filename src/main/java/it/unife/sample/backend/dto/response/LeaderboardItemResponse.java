package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// DTO per la risposta della classifica: 
// contiene le informazioni principali di un utente nella leaderboard, inclusi il punteggio e la quantità di CO2 risparmiata
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class LeaderboardItemResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String fotoProfilo;
    private Integer punteggio;
    private BigDecimal co2Totale;
}
