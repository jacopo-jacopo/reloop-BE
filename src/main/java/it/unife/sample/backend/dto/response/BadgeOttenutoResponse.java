package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BadgeOttenutoResponse {

    private BadgeOttenutoIdDto id;
    private BadgeResponse badge;
    private LocalDate dataOttenimento;

    @Data
    @AllArgsConstructor
    public static class BadgeOttenutoIdDto {
        private Long idUtenteReg;
        private String nomeBadge;
    }
}
