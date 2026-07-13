package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// classe che rappresenta una recensione scritta da un utente recensore su un utente recensito: 
// mappa la tabella "recensione" del db e contiene le informazioni sulla recensione
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "recensione")
public class Recensione {

    // PK composta: (id_utente_reg_recensore, id_utente_reg_recensito)
    @EmbeddedId
    private RecensioneId id;

    // FK: id_utente_reg_recensore → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("idUtenteRegRecensore") // indica che questo campo è mappato alla parte della chiave primaria composta rappresentata da idUtenteRegRecensore
    @JoinColumn(name = "id_utente_reg_recensore")
    private UtenteRegistrato recensore;

    // FK: id_utente_reg_recensito → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("idUtenteRegRecensito") // indica che questo campo è mappato alla parte della chiave primaria composta rappresentata da idUtenteRegRecensito
    @JoinColumn(name = "id_utente_reg_recensito")
    private UtenteRegistrato recensito;

    @Column(name = "voto", nullable = false)
    private Integer voto;

    @Column(name = "descrizione_recensione", columnDefinition = "TEXT") // TEXT per permettere descrizioni lunghe
    private String descrizioneRecensione;

    @Column(name = "data_recensione")
    private LocalDate dataRecensione;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel db
    public void prePersist() {
        this.dataRecensione = LocalDate.now();
    }



    // classe embeddable che rappresenta la chiave primaria composta della tabella "recensione"
    @Data
    @Embeddable // indica che questa classe è una classe embeddable, cioè può essere incorporata in un'altra classe come chiave primaria composta
    public static class RecensioneId implements java.io.Serializable {

        @Column(name = "id_utente_reg_recensore")
        private Long idUtenteRegRecensore;

        @Column(name = "id_utente_reg_recensito")
        private Long idUtenteRegRecensito;
    }
}