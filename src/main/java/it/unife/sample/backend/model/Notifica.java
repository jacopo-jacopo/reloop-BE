package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// classe che rappresenta una notifica: mappa la tabella "notifica" del database e contiene le informazioni della notifica
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "notifica") // specifica il nome della tabella del database a cui questa entità è mappata
public class Notifica {

    // enum che rappresenta i tipi di notifica disponibili
    public enum TipoNotifica {
        NUOVA_PROPOSTA,
        PROPOSTA_ACCETTATA,
        PROPOSTA_RIFIUTATA,
        NUOVO_MESSAGGIO,
        NUOVA_RECENSIONE,
        ANNUNCIO_ELIMINATO,
        ACCOUNT_BLOCCATO
    }

    // campi della classe Notifica, mappati alle colonne della tabella "notifica" del database

    @Id // indica che questo campo è la chiave primaria della tabella
    @GeneratedValue(strategy = GenerationType.IDENTITY) // il valore della chiave primaria è generato automaticamente dal db (auto-increment)
    @Column(name = "id_notifica")
    private Long idNotifica;

    @Enumerated(EnumType.STRING) // indica che il campo deve essere mappato come stringa nel db (i valori dell'enum saranno salvati come stringhe)
    @Column(name = "tipo", nullable = false)
    private TipoNotifica tipo;

    @Column(name = "testo", nullable = false, columnDefinition = "TEXT") // "TEXT" indica che il campo deve essere mappato come tipo TEXT nel db (per testi lunghi)
    private String testo;

    @Column(name = "letta", nullable = false)
    private boolean letta = false;

    @Column(name = "timestamp_notifica", nullable = false)
    private LocalDateTime timestampNotifica = LocalDateTime.now(); // inizializza il campo con la data e l'ora corrente al momento della 
                                                                   // creazione dell'oggetto Notifica

    @ManyToOne(fetch = FetchType.LAZY) // indica che questo campo rappresenta una relazione molti-a-uno con l'entità UtenteRegistrato; 
                                       // fetch = FetchType.LAZY indica che l'entità UtenteRegistrato associata non deve essere caricata automaticamente 
                                       // quando si carica la notifica
    @JoinColumn(name = "id_utente_reg", nullable = false)
    private UtenteRegistrato destinatario;
}
