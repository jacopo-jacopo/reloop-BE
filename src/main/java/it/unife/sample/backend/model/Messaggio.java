package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messaggio")
public class Messaggio {

    // Chiave primaria composita: (id_messaggio, id_chat)
    @EmbeddedId
    private MessaggioId id;

    // FK: id_chat → interazione_chat
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idChat")
    @JoinColumn(name = "id_chat")
    private Chat chat;

    @Column(name = "contenuto", nullable = false, columnDefinition = "TEXT")
    private String contenuto;

    @Column(name = "data_invio")
    private LocalDateTime dataInvio;

    @Column(name = "flag_lettura")
    private Boolean flagLettura = false;

    // FK: id_mittente → utente_registrato (aggiunto al DB)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mittente", nullable = false)
    private UtenteRegistrato mittente;

    @PrePersist
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