package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

// classe che rappresenta un amministratore: mappa la tabella "amministratore" del database e contiene le informazioni dell'amministratore
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "amministratore") // specifica il nome della tabella del database a cui questa entità è mappata
public class Amministratore {

    @Id // indica che questo campo è la chiave primaria della tabella
    @GeneratedValue(strategy = GenerationType.IDENTITY) // il valore della chiave primaria è generato automaticamente dal db (auto-increment)
    @Column(name = "id_utente_adm") 
    private Long idUtenteAdm;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore // indica che questo campo non deve essere serializzato in JSON (per motivi di sicurezza)
    @Column(name = "password", nullable = false)
    private String password;
}