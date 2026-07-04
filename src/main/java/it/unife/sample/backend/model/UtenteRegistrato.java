package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// rappresenta un utente registrato nel sistema, con le informazioni personali, le credenziali di accesso e le statistiche di utilizzo
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è una entità JPA, mappata alla tabella utente_registrato del database
@Table(name = "utente_registrato") // specifica il nome della tabella del database a cui è mappata questa entità
public class UtenteRegistrato {

    @Id // indica che questo campo è la chiave primaria della tabella
    @GeneratedValue(strategy = GenerationType.IDENTITY) // il valore della chiave primaria è generato automaticamente dal db con auto-increment 
    @Column(name = "id_utente_reg")
    private Long idUtenteReg;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore // indica che questo campo non deve essere serializzato in JSON (per motivi di sicurezza)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "indirizzo", nullable = false)
    private String indirizzo;

    @Column(name = "punteggio")
    private Integer punteggio = 0;

    @Column(name = "foto_profilo", columnDefinition = "MEDIUMTEXT") // specifica il tipo di colonna nel database come MEDIUMTEXT, 
                                                                    // che può contenere fino a 16 MB di testo
    private String fotoProfilo;

    @ManyToOne(fetch = FetchType.EAGER) // relazione molti-a-uno con la classe Quartiere: 
                                        // un utente registrato appartiene a un quartiere, e un quartiere può avere molti utenti registrati
    @JoinColumn(name = "id_quartiere", nullable = false) // specifica il nome della colonna della chiave esterna nel database e indica che è not-null
    private Quartiere quartiere;

    @Column(name = "co2_totale")
    private BigDecimal co2Totale = BigDecimal.ZERO; // inizializzata a zero per evitare valori null

    @Column(name = "ultima_visita_proposte")
    private LocalDateTime ultimaVisitaProposte;

    @Column(name = "ultima_visita_chat")
    private LocalDateTime ultimaVisitaChat;

    @Column(name = "bloccato", nullable = false)
    private boolean bloccato = false; // inizializzata a false (un nuovo utente non può essere bloccato di default)
}