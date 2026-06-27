package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "annuncio_incluso")
public class AnnuncioIncluso {

    @EmbeddedId
    private AnnuncioInclusoId id;

    // FK: id_proposta → proposta
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProposta")
    @JoinColumn(name = "id_proposta")
    private Proposta proposta;

    // FK: id_annuncio_offerto → annuncio
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idAnnuncioOfferto")
    @JoinColumn(name = "id_annuncio_offerto")
    private Annuncio annuncioOfferto;

    // Di default 0 — diventa 1 sull'annuncio scelto al momento dell'accettazione
    @Column(name = "flag_selezionato")
    private Boolean flagSelezionato = false;

    @Data
    @Embeddable
    public static class AnnuncioInclusoId implements java.io.Serializable {

        @Column(name = "id_proposta")
        private Long idProposta;

        @Column(name = "id_annuncio_offerto")
        private Long idAnnuncioOfferto;
    }
}