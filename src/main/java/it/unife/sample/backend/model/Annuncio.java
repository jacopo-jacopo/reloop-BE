package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "annuncio")
public class Annuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_annuncio")
    private Long idAnnuncio;

    @Column(name = "titolo", nullable = false)
    private String titolo;

    @Column(name = "descrizione_annuncio", nullable = false, columnDefinition = "TEXT")
    private String descrizioneAnnuncio;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "prezzo_stimato", nullable = false)
    private BigDecimal prezzoStimato;

    @Enumerated(EnumType.STRING)
    @Column(name = "condizioni", nullable = false)
    private Condizioni condizioni;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_annuncio", nullable = false)
    private StatoAnnuncio statoAnnuncio = StatoAnnuncio.attivo;

    // FK: id_utente_reg_pubblicante → utente_registrato
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utente_reg_pubblicante", nullable = false)
    private UtenteRegistrato pubblicante;

    @Column(name = "notifica_oscuramento_letta", nullable = false)
    private boolean notificaOscuramentoLetta = false;

    public enum Condizioni {
        scarso, discreto, buono, ottimo, come_nuovo
    }

    public enum StatoAnnuncio {
        attivo, sospeso, chiuso, oscurato
    }
}