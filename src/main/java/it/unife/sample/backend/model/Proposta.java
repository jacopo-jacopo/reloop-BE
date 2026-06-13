package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "proposta")
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proposta")
    private Long idProposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_proposta", nullable = false)
    private StatoProposta statoProposta = StatoProposta.in_attesa;

    // FK: id_annuncio_interesse → annuncio
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_annuncio_interesse", nullable = false)
    private Annuncio annuncioInteresse;

    // FK: id_utente_reg_proponente → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utente_reg_proponente", nullable = false)
    @JsonIgnoreProperties({"password"})
    private UtenteRegistrato proponente;

    @Column(name = "timestamp_proposta")
    private LocalDateTime timestampProposta;

    // Annunci inclusi nella proposta (relazione con annuncio_incluso)
    @OneToMany(mappedBy = "proposta", fetch = FetchType.EAGER)
    private List<AnnuncioIncluso> annunciOfferti;

    @PrePersist
    public void prePersist() {
        this.timestampProposta = LocalDateTime.now();
    }

    public enum StatoProposta {
        in_attesa, accettata, rifiutata
    }
}