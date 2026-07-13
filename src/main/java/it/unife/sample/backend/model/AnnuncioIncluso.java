package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

// classe che rappresenta un annuncio incluso in una proposta:
// mappa la tabella "annuncio_incluso" del database e contiene le informazioni sull'annuncio incluso
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "annuncio_incluso")
public class AnnuncioIncluso {

    @EmbeddedId // indica che questa classe utilizza una chiave primaria composta, rappresentata dalla classe AnnuncioInclusoId
    private AnnuncioInclusoId id;

    // FK: id_proposta → proposta
    @ManyToOne(fetch = FetchType.LAZY) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                       // deve avvenire in modo lazy (cioè solo quando necessario)
    @MapsId("idProposta") // indica che il campo idProposta della chiave primaria composta è mappato alla relazione con la proposta
    @JoinColumn(name = "id_proposta")
    private Proposta proposta;

    // FK: id_annuncio_offerto → annuncio
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("idAnnuncioOfferto") // indica che il campo idAnnuncioOfferto della chiave primaria composta è mappato alla relazione con l'annuncio offerto
    @JoinColumn(name = "id_annuncio_offerto")
    private Annuncio annuncioOfferto;

    @Column(name = "flag_selezionato")
    private Boolean flagSelezionato = false;


    // classe interna per rappresentare la chiave primaria composta della tabella "annuncio_incluso"
    @Data 
    @Embeddable // indica che questa classe può essere incorporata in un'altra entità JPA come chiave primaria
    public static class AnnuncioInclusoId implements java.io.Serializable {

        @Column(name = "id_proposta")
        private Long idProposta;

        @Column(name = "id_annuncio_offerto")
        private Long idAnnuncioOfferto;
    }
}