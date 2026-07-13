package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// classe che rappresenta un badge ottenuto da un utente: mappa la tabella "badge_ottenuto" del database e contiene le informazioni sul badge ottenuto
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "badge_ottenuto")
public class BadgeOttenuto {

    @EmbeddedId // indica che questo campo è una chiave primaria composta, mappata a una classe embeddable 
                // (cioè una classe che può essere incorporata in un'altra classe)
    private BadgeOttenutoId id;

    // FK: id_utente_reg → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("idUtenteReg") 
    @JoinColumn(name = "id_utente_reg")
    private UtenteRegistrato utente;

    // FK: nome_badge → badge
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("nomeBadge")
    @JoinColumn(name = "nome_badge")
    private Badge badge;

    @Column(name = "data_ottenimento")
    private LocalDate dataOttenimento;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel database
    public void prePersist() {
        this.dataOttenimento = LocalDate.now();
    }

    @Data
    @Embeddable // indica che questa classe è una classe embeddable, cioè può essere incorporata in un'altra classe come chiave primaria composta
    public static class BadgeOttenutoId implements java.io.Serializable {

        @Column(name = "id_utente_reg")
        private Long idUtenteReg;

        @Column(name = "nome_badge")
        private String nomeBadge;
    }
}