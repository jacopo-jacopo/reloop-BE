package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

// DTO per la risposta contenente le informazioni di un badge ottenuto da un utente
@Data // genera automaticamente i metodi getter, setter, equals, hashCode e toString
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class BadgeOttenutoResponse {

    private BadgeOttenutoIdDto id;
    private BadgeResponse badge;
    private LocalDate dataOttenimento;

    // DTO per la chiave primaria composta di un badge ottenuto da un utente
    @Data
    @AllArgsConstructor
    public static class BadgeOttenutoIdDto {
        private Long idUtenteReg;
        private String nomeBadge;
    }
}
