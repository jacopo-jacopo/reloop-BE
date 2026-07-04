package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificaResponse {
    private Long idNotifica;
    private String tipo;
    private String testo;
    private boolean letta;
    private LocalDateTime timestampNotifica;
}
