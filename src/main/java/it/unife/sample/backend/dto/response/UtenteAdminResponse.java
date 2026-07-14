package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta dell'utente amministratore: contiene le informazioni dell'utente amministratore
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class UtenteAdminResponse {
    private Long idUtenteReg;
    private String nomeCompleto;
    private String email;
    private boolean bloccato;
    private Integer punteggio;
    private QuartiereResponse quartiere;
    private String fotoProfiloUtente;
}
