package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO per la risposta del completamento di una chat: contiene informazioni sullo stato del completamento e sull'altro utente coinvolto
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class CompletaResponse {
    private boolean completato;
    private Long idAltroUtente;
}
