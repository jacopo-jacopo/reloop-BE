package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadgeResponse {

    private String nomeBadge;
    private Integer sogliaPunti;
    private String descrizioneBadge;
    private String iconaBadge;
    private String colore;
}
