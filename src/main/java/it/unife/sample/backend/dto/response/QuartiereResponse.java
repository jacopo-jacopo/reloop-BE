package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


// DTO per la risposta contenente le informazioni di un quartiere
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class QuartiereResponse {
    private Long idQuartiere;
    private String nomeQuartiere;
    private String citta;
}
