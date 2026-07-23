package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// classe che rappresenta una chat tra due utenti, generata da una proposta: 
// mappa la tabella "interazione_chat" del database e contiene le informazioni sulla chat
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "interazione_chat") 
public class Chat {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica che il valore della PK deve essere generato automaticamente dal db (auto-increment)
    @Column(name = "id_chat")
    private Long idChat;

    @Enumerated(EnumType.STRING) // indica che il valore dell'enum deve essere salvato come stringa nel db (nome costante)
    @Column(name = "stato_chat", nullable = false)
    private StatoChat statoChat = StatoChat.aperta;

    @Column(name = "data_completamento")
    private LocalDateTime dataCompletamento;

    // FK: id_proposta_generante → proposta
    @OneToOne(fetch = FetchType.EAGER) // indica che questa relazione è di tipo uno-a-uno e che il caricamento dell'entità correlata 
                                        // deve avvenire in modo eager (cioè subito)
    @JoinColumn(name = "id_proposta_generante", nullable = false, unique = true) // unique=true indica che il valore della colonna deve essere 
                                                                                 // unico nel db (una proposta può generare al massimo una chat)
    private Proposta propostaGenerante;

    @Column(name = "timestamp_chat")
    private LocalDateTime timestampChat;

    @Column(name = "confermato_pubblicante", nullable = false)
    private boolean confermatoPubblicante = false;

    @Column(name = "confermato_proponente", nullable = false)
    private boolean confermatoProponente = false;

    @PrePersist // indica che questo metodo deve essere eseguito prima di salvare l'entità nel db, per impostare il timestamp della chat
    public void prePersist() {
        this.timestampChat = LocalDateTime.now();
    }

    // enumerazione per rappresentare lo stato della chat: aperta, completata o annullata
    public enum StatoChat {
        aperta, completata, annullata
    }
}