package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "recensione")
public class Recensione {

    // PK composita: (id_utente_reg_recensore, id_utente_reg_recensito)
    @EmbeddedId
    private RecensioneId id;

    // FK: id_utente_reg_recensore → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUtenteRegRecensore")
    @JoinColumn(name = "id_utente_reg_recensore")
    private UtenteRegistrato recensore;

    // FK: id_utente_reg_recensito → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUtenteRegRecensito")
    @JoinColumn(name = "id_utente_reg_recensito")
    private UtenteRegistrato recensito;

    @Column(name = "voto", nullable = false)
    private Integer voto;

    @Column(name = "descrizione_recensione", columnDefinition = "TEXT")
    private String descrizioneRecensione;

    @Column(name = "data_recensione")
    private LocalDate dataRecensione;

    @PrePersist
    public void prePersist() {
        this.dataRecensione = LocalDate.now();
    }

    @Data
    @Embeddable
    public static class RecensioneId implements java.io.Serializable {

        @Column(name = "id_utente_reg_recensore")
        private Long idUtenteRegRecensore;

        @Column(name = "id_utente_reg_recensito")
        private Long idUtenteRegRecensito;
    }
}