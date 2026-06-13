package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "quartiere")
public class Quartiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quartiere")
    private Long idQuartiere;

    @Column(name = "nome_quartiere", nullable = false)
    private String nomeQuartiere;

    @Column(name = "citta", nullable = false)
    private String citta;
}