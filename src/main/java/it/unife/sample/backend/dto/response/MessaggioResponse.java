package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessaggioResponse {

    private MessaggioIdDto id;
    private String contenuto;
    private LocalDateTime dataInvio;
    private Boolean flagLettura;
    private MittenteSummary mittente;

    @Data
    @AllArgsConstructor
    public static class MessaggioIdDto {
        private Long idMessaggio;
        private Long idChat;
    }

    @Data
    @AllArgsConstructor
    public static class MittenteSummary {
        private Long idUtenteReg;
        private String nomeCompleto;
    }
}
