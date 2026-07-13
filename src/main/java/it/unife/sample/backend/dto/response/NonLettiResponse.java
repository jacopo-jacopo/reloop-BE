package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// DTO per la risposta delle notifiche non lette di un utente: contiene le liste degli id dei messaggi non letti e delle chat vuote
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class NonLettiResponse {
    private List<Long> messaggiNonLetti;
    private List<Long> chatVuote;
}
