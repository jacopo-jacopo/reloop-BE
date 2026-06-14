package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "badge")
public class Badge {

    // PK è il nome del badge — stringa univoca
    @Id
    @Column(name = "nome_badge")
    private String nomeBadge;

    // NULL = la condizione di sblocco va estrapolata da descrizioneBadge
    @Column(name = "soglia_punti")
    private Integer sogliaPunti;

    @Column(name = "descrizione_badge", nullable = false)
    private String descrizioneBadge;

    // Nome/percorso del file .png dell'icona, relativo a assets/badges/
    @Column(name = "icona_badge")
    private String iconaBadge;

    @Column(name = "colore")
    private String colore;
}