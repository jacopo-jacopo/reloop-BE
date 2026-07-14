package it.unife.sample.backend.dto.request;

import lombok.Data;

// DTO per la richiesta di blocco/sblocco di un utente
@Data // annotazione Lombok che genera automaticamente i metodi getter, setter, equals, hashCode e toString
public class BloccaUtenteRequest {
    private boolean bloccato;
}
