package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "badge_ottenuto")
public class BadgeOttenuto {

    @EmbeddedId
    private BadgeOttenutoId id;

    // FK: id_utente_reg → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUtenteReg")
    @JoinColumn(name = "id_utente_reg")
    @JsonIgnore
    private UtenteRegistrato utente;

    // FK: nome_badge → badge
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("nomeBadge")
    @JoinColumn(name = "nome_badge")
    private Badge badge;

    @Column(name = "data_ottenimento")
    private LocalDate dataOttenimento;

    @PrePersist
    public void prePersist() {
        this.dataOttenimento = LocalDate.now();
    }

    @Data
    @Embeddable
    public static class BadgeOttenutoId implements java.io.Serializable {

        @Column(name = "id_utente_reg")
        private Long idUtenteReg;

        @Column(name = "nome_badge")
        private String nomeBadge;
    }
}