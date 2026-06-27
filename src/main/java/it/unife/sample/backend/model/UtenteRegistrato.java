package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "utente_registrato")
public class UtenteRegistrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente_reg")
    private Long idUtenteReg;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "indirizzo", nullable = false)
    private String indirizzo;

    @Column(name = "punteggio")
    private Integer punteggio = 0;

    // MEDIUMTEXT — stringa base64 della foto profilo
    @Column(name = "foto_profilo", columnDefinition = "MEDIUMTEXT")
    private String fotoProfilo;

    // FK: id_quartiere → quartiere
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quartiere", nullable = false)
    private Quartiere quartiere;

    // Co2 totale accumulata — mantenuta nel DB come cache
    @Column(name = "co2_totale")
    private BigDecimal co2Totale = BigDecimal.ZERO;

    private LocalDateTime ultimaVisitaProposte;

    private LocalDateTime ultimaVisitaChat;
}