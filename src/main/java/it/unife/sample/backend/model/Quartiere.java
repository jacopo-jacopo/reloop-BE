package it.unife.sample.backend.model;

import jakarta.persistence.*;
import lombok.Data;

// rappresenta un quartiere, con un ID, un nome e una città in cui si trova
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "quartiere") // specifica il nome della tabella del database a cui questa entità è mappata
public class Quartiere {

    @Id // indica che questo campo è la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // specifica che il valore della chiave primaria sarà generato automaticamente dal database, 
                                                        // utilizzando una strategia di incremento automatico
    @Column(name = "id_quartiere") // specifica il nome della colonna del database a cui questo campo è mappato
    private Long idQuartiere;

    @Column(name = "nome_quartiere", nullable = false) // specifica il nome della colonna del database a cui questo campo è mappato (e che è not-null)
    private String nomeQuartiere;

    @Column(name = "citta", nullable = false) // specifica il nome della colonna del database a cui questo campo è mappato (e che è not-null)
    private String citta;
}