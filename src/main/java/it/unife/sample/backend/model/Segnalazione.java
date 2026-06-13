package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "segnalazione")
public class Segnalazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_segnalazione")
    private Long idSegnalazione;

    @Column(name = "motivazione", nullable = false, columnDefinition = "TEXT")
    private String motivazione;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_segnalazione", nullable = false)
    private StatoSegnalazione statoSegnalazione = StatoSegnalazione.in_attesa;

    // FK: id_annuncio_segnalato → annuncio
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_annuncio_segnalato", nullable = false)
    private Annuncio annuncioSegnalato;

    // FK: id_utente_reg → utente_registrato (chi ha creato la segnalazione)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utente_reg", nullable = false)
    private UtenteRegistrato segnalante;

    // FK: id_utente_adm → amministratore (null finché non presa in carico)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utente_adm")
    private Amministratore amministratore;

    @Column(name = "timestamp_segnalazione")
    private LocalDateTime timestampSegnalazione;

    @PrePersist
    public void prePersist() {
        this.timestampSegnalazione = LocalDateTime.now();
    }

    public enum StatoSegnalazione {
        in_attesa, presa_in_carico, chiusa
    }
}