package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

// DTO per la richiesta di invio di una proposta
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString per la classe
public class InviaPropostaRequest {

    @NotNull
    private Long idAnnuncioInteresse;

    @NotEmpty
    private List<Long> idAnnunciOfferti;
}
