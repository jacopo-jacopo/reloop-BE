package it.unife.sample.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InviaPropostaRequest {

    @NotNull
    private Long idAnnuncioInteresse;

    @NotEmpty
    private List<Long> idAnnunciOfferti;
}
