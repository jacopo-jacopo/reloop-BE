package it.unife.sample.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String tipo;        // "utente" o "admin"
    private Long id;
    private String nomeCompleto;
    private String email;
    private Object utente;      // dati completi utente o admin
}