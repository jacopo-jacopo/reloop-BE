package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interazione_chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Long idChat;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_chat", nullable = false)
    private StatoChat statoChat = StatoChat.aperta;

    @Column(name = "data_completamento")
    private LocalDateTime dataCompletamento;

    // FK: id_proposta_generante → proposta (UNIQUE — 1:1)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proposta_generante", nullable = false, unique = true)
    private Proposta propostaGenerante;

    @Column(name = "timestamp_chat")
    private LocalDateTime timestampChat;

    @PrePersist
    public void prePersist() {
        this.timestampChat = LocalDateTime.now();
    }

    public enum StatoChat {
        aperta, completata, annullata
    }
}