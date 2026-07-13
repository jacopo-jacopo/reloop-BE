package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO per la richiesta di invio di un messaggio in una chat
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
public class InviaMessaggioRequest {

    @NotBlank
    private String contenuto;
}
