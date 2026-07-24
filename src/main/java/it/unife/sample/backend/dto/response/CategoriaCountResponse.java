package it.unife.sample.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoriaCountResponse {
    private String categoria;
    private long count;
}
