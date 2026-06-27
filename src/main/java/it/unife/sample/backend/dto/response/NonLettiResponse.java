package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NonLettiResponse {
    private List<Long> messaggiNonLetti;
    private List<Long> chatVuote;
}
