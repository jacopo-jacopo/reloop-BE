package it.unife.sample.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

// classe che rappresenta una foto: mappa la tabella "foto" del database e contiene le informazioni della foto
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per tutti i campi della classe
@Entity // indica che questa classe è un'entità JPA, mappata a una tabella del database
@Table(name = "foto")
public class Foto {

    @Id // indica che questo campo è la chiave primaria della tabella
    @GeneratedValue(strategy = GenerationType.IDENTITY) // il valore della chiave primaria è generato automaticamente dal db (auto-increment)
    @Column(name = "id_foto")
    private Long idFoto;

    // stringa base64 completa
    @Column(name = "url_foto", nullable = false, columnDefinition = "MEDIUMTEXT") 
    private String urlFoto;

    @Column(name = "ordine")
    private Integer ordine = 0;

    // FK: id_annuncio → annuncio (ON CASCADE DELETE)
    @ManyToOne(fetch = FetchType.LAZY) // indica che questo campo rappresenta una relazione molti-a-uno con l'entità Annuncio; 
                                       // fetch = FetchType.LAZY indica che l'entità Annuncio associata non deve essere caricata automaticamente 
                                       // quando si carica la foto
    @JoinColumn(name = "id_annuncio", nullable = false)
    @JsonIgnore
    private Annuncio annuncio;
}