package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// classe che rappresenta una proposta di scambio tra due utenti, generata da un annuncio di interesse: 
// mappa la tabella "proposta" del database e contiene le informazioni sulla proposta
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "proposta")
public class Proposta {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica che il valore della PK deve essere generato automaticamente dal db (auto-increment)
    @Column(name = "id_proposta")
    private Long idProposta;

    @Enumerated(EnumType.STRING) // indica che il valore dell'enum deve essere salvato come stringa nel db (nome costante)
    @Column(name = "stato_proposta", nullable = false)
    private StatoProposta statoProposta = StatoProposta.in_attesa;

    // FK: id_annuncio_interesse → annuncio
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_annuncio_interesse", nullable = false)
    private Annuncio annuncioInteresse;

    // FK: id_utente_reg_proponente → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_utente_reg_proponente", nullable = false)
    private UtenteRegistrato proponente;

    @Column(name = "timestamp_proposta")
    private LocalDateTime timestampProposta;

    // relazione uno-a-molti con gli annunci inclusi nella proposta: una proposta può includere più annunci offerti
    @OneToMany(mappedBy = "proposta", fetch = FetchType.EAGER) // indica che questa relazione è di tipo uno-a-molti e che il caricamento delle 
                                                               // entità correlate deve avvenire in modo eager (cioè subito)
    private List<AnnuncioIncluso> annunciOfferti;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel db, per impostare il timestamp della proposta
    public void prePersist() {
        this.timestampProposta = LocalDateTime.now();
    }

    // enumerazione per rappresentare lo stato della proposta: in attesa, accettata o rifiutata
    public enum StatoProposta {
        in_attesa, accettata, rifiutata
    }
}