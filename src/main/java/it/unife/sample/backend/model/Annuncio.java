package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

// classe che rappresenta un annuncio pubblicato da un utente: mappa la tabella "annuncio" del database e contiene le informazioni sull'annuncio
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "annuncio")
public class Annuncio {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica che il valore della PK deve essere generato automaticamente dal db (auto-increment)
    @Column(name = "id_annuncio")
    private Long idAnnuncio;

    @Column(name = "titolo", nullable = false)
    private String titolo;

    @Column(name = "descrizione_annuncio", nullable = false, columnDefinition = "TEXT") // TEXT per permettere descrizioni lunghe
    private String descrizioneAnnuncio;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "prezzo_stimato", nullable = false)
    private BigDecimal prezzoStimato;

    @Enumerated(EnumType.STRING) // indica che il valore dell'enum deve essere salvato come stringa nel db (nome costante)
    @Column(name = "condizioni", nullable = false)
    private Condizioni condizioni;

    @Enumerated(EnumType.STRING) // indica che il valore dell'enum deve essere salvato come stringa nel db (nome costante)
    @Column(name = "stato_annuncio", nullable = false)
    private StatoAnnuncio statoAnnuncio = StatoAnnuncio.attivo;

    // FK: id_utente_reg_pubblicante → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_utente_reg_pubblicante", nullable = false)
    private UtenteRegistrato pubblicante;

    @Column(name = "notifica_oscuramento_letta", nullable = false)
    private boolean notificaOscuramentoLetta = false;

    
    // enumerazioni per condizioni e stato annuncio
    public enum Condizioni {
        scarso, discreto, buono, ottimo, come_nuovo
    }
    public enum StatoAnnuncio {
        attivo, sospeso, chiuso, oscurato
    }
}