package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO per la richiesta di accettazione di una proposta
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
public class AccettaPropostaRequest {

    @NotNull
    private Long idAnnuncioScelto;
}
