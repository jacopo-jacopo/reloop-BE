package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// classe che rappresenta una segnalazione di un annuncio da parte di un utente: 
// mappa la tabella "segnalazione" del db e contiene le informazioni sulla segnalazione
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "segnalazione")
public class Segnalazione {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica che il valore della PK deve essere generato automaticamente dal db (auto-increment)
    @Column(name = "id_segnalazione")
    private Long idSegnalazione;

    @Column(name = "motivazione", nullable = false, columnDefinition = "TEXT") // TEXT per permettere motivazioni lunghe
    private String motivazione;

    @Enumerated(EnumType.STRING) // indica che il valore dell'enum deve essere salvato come stringa nel db (nome costante)
    @Column(name = "stato_segnalazione", nullable = false)
    private StatoSegnalazione statoSegnalazione = StatoSegnalazione.in_attesa; // default: in_attesa

    // FK: id_annuncio_segnalato → annuncio
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_annuncio_segnalato", nullable = false)
    private Annuncio annuncioSegnalato;

    // FK: id_utente_reg → utente_registrato (chi ha creato la segnalazione)
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_utente_reg", nullable = false)
    private UtenteRegistrato segnalante;

    // FK: id_utente_adm → amministratore (null finché non presa in carico)
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_utente_adm")
    private Amministratore amministratore;

    @Column(name = "timestamp_segnalazione")
    private LocalDateTime timestampSegnalazione;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel database
    public void prePersist() {
        this.timestampSegnalazione = LocalDateTime.now();
    }

    // enumerazione per lo stato della segnalazione
    public enum StatoSegnalazione {
        in_attesa, presa_in_carico, chiusa
    }
}