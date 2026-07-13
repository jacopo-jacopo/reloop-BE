package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// DTO per la risposta pubblica di un utente:
// contiene le informazioni principali di un utente, inclusi il nome completo, l'indirizzo, il punteggio e la foto del profilo
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class UtentePublicoResponse {

    private Long idUtenteReg;
    private String nomeCompleto;
    private String indirizzo;
    private Integer punteggio;
    private BigDecimal co2Totale;
    private String fotoProfilo;
    private QuartiereResponse quartiere;
    private Long scambiCompletati;
}
