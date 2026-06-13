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

    @Column(name = "soglia_punti", nullable = false)
    private Integer sogliaPunti;

    @Column(name = "descrizione_badge", nullable = false)
    private String descrizioneBadge;

    @Column(name = "emoji")
    private String emoji;

    @Column(name = "colore")
    private String colore;
}