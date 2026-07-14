package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Relazione "elimina": traccia quale amministratore ha oscurato (rimosso dalla
 * piattaforma) quale annuncio.
 */

// rappresenta la relazione tra un amministratore e un annuncio eliminato, 
// con le informazioni sull'amministratore che ha effettuato l'eliminazione e sull'annuncio eliminato
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è una entità JPA, mappata alla tabella elimina del database
@Table(name = "elimina")
public class Elimina {

    @EmbeddedId // indica che la chiave primaria della tabella è composta da più colonne, definite nella classe EliminaId
    private EliminaId id;

    // FK: id_utente_adm → amministratore
    @ManyToOne(fetch = FetchType.EAGER) // relazione molti-a-uno con la classe Amministratore: 
                                        // un amministratore può eliminare molti annunci, ma un annuncio eliminato è associato a un solo amministratore
    @MapsId("idUtenteAdm") // indica che la chiave primaria della tabella elimina include la colonna id_utente_adm, 
                           // che è anche una chiave esterna verso la tabella amministratore
    @JoinColumn(name = "id_utente_adm")
    private Amministratore amministratore;

    // FK: id_annuncio_eliminato → annuncio
    @ManyToOne(fetch = FetchType.EAGER) // relazione molti-a-uno con la classe Annuncio: 
                                        // un annuncio può essere eliminato da molti amministratori, ma un annuncio eliminato è associato a un solo annuncio
    @MapsId("idAnnuncioEliminato") // indica che la chiave primaria della tabella elimina include la colonna id_annuncio_eliminato, 
                                   // che è anche una chiave esterna verso la tabella annuncio
    @JoinColumn(name = "id_annuncio_eliminato")
    private Annuncio annuncioEliminato;

    // classe interna che rappresenta la chiave primaria composta della tabella elimina,
    // composta dai campi id_utente_adm e id_annuncio_eliminato
    @Data
    @Embeddable // indica che questa classe può essere incorporata in un'altra entità JPA come chiave primaria
    public static class EliminaId implements java.io.Serializable {

        @Column(name = "id_utente_adm")
        private Long idUtenteAdm;

        @Column(name = "id_annuncio_eliminato")
        private Long idAnnuncioEliminato;
    }
}
