package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// classe che rappresenta un messaggio inviato in una chat: mappa la tabella "messaggio" del db e contiene le informazioni sul messaggio
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "messaggio")
public class Messaggio {

    @EmbeddedId // indica che questo campo è una chiave primaria composta, definita dalla classe MessaggioId
    private MessaggioId id;

    // FK: id_chat → interazione_chat
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @MapsId("idChat") // indica che il campo idChat della chiave primaria composta è mappato a questa relazione
    @JoinColumn(name = "id_chat")
    private Chat chat;

    @Column(name = "contenuto", nullable = false, columnDefinition = "TEXT") // TEXT per permettere messaggi lunghi
    private String contenuto;

    @Column(name = "data_invio")
    private LocalDateTime dataInvio;

    @Column(name = "flag_lettura")
    private Boolean flagLettura = false;

    // FK: id_mittente → utente_registrato (aggiunto al DB)
    @ManyToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo molti-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_mittente", nullable = false)
    private UtenteRegistrato mittente;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel database
    public void prePersist() {
        this.dataInvio = LocalDateTime.now();
    }

    @Data
    @Embeddable
    public static class MessaggioId implements java.io.Serializable {

        @Column(name = "id_messaggio")
        private Long idMessaggio;

        @Column(name = "id_chat")
        private Long idChat;
    }
}