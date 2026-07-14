package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

// DTO per la risposta della notifica: contiene le informazioni della notifica da inviare al frontend
@Data // genera automaticamente i metodi getter, setter, toString, equals e hashCode per tutti i campi della classe
@AllArgsConstructor // genera automaticamente un costruttore con un parametro per ogni campo della classe
public class NotificaResponse {
    private Long idNotifica;
    private String tipo;
    private String testo;
    private boolean letta;
    private LocalDateTime timestampNotifica;
}
