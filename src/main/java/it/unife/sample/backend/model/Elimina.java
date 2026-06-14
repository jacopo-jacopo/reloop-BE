package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Relazione "elimina": traccia quale amministratore ha oscurato (rimosso dalla
 * piattaforma) quale annuncio.
 */
@Data
@Entity
@Table(name = "elimina")
public class Elimina {

    @EmbeddedId
    private EliminaId id;

    // FK: id_utente_adm → amministratore
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUtenteAdm")
    @JoinColumn(name = "id_utente_adm")
    private Amministratore amministratore;

    // FK: id_annuncio_eliminato → annuncio
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idAnnuncioEliminato")
    @JoinColumn(name = "id_annuncio_eliminato")
    private Annuncio annuncioEliminato;

    @Data
    @Embeddable
    public static class EliminaId implements java.io.Serializable {

        @Column(name = "id_utente_adm")
        private Long idUtenteAdm;

        @Column(name = "id_annuncio_eliminato")
        private Long idAnnuncioEliminato;
    }
}
