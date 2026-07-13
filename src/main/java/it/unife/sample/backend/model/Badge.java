package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

// classe che rappresenta un badge: mappa la tabella "badge" del database e contiene le informazioni sul badge
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "badge")
public class Badge {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @Column(name = "nome_badge")
    private String nomeBadge;

    // se è "NULL" la condizione di sblocco va estrapolata da descrizioneBadge
    @Column(name = "soglia_punti")
    private Integer sogliaPunti;

    @Column(name = "descrizione_badge", nullable = false)
    private String descrizioneBadge;

    // Nome percorso del file .png dell'icona (in assets/badges/)
    @Column(name = "icona_badge")
    private String iconaBadge;

    @Column(name = "colore")
    private String colore;
}