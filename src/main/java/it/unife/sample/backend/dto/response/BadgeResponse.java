package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta contenente le informazioni di un badge
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class BadgeResponse {

    private String nomeBadge;
    private Integer sogliaPunti;
    private String descrizioneBadge;
    private String iconaBadge;
    private String colore;
}
