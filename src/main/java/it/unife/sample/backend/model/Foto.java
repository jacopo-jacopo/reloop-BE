package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    // Stringa base64 completa — MEDIUMTEXT nel DB
    @Column(name = "url_foto", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String urlFoto;

    @Column(name = "ordine")
    private Integer ordine = 0;

    // FK: id_annuncio → annuncio (CASCADE DELETE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annuncio", nullable = false)
    @JsonIgnore
    private Annuncio annuncio;
}