package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifica")
public class Notifica {

    public enum TipoNotifica {
        NUOVA_PROPOSTA,
        PROPOSTA_ACCETTATA,
        PROPOSTA_RIFIUTATA,
        NUOVO_MESSAGGIO,
        NUOVA_RECENSIONE,
        ANNUNCIO_ELIMINATO,
        ACCOUNT_BLOCCATO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notifica")
    private Long idNotifica;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoNotifica tipo;

    @Column(name = "testo", nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(name = "letta", nullable = false)
    private boolean letta = false;

    @Column(name = "timestamp_notifica", nullable = false)
    private LocalDateTime timestampNotifica = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente_reg", nullable = false)
    private UtenteRegistrato destinatario;
}
