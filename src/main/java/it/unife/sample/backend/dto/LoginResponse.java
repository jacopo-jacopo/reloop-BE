package it.unife.sample.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String tipo;
    private Long id;
    private String nomeCompleto;
    private String email;
    private Object utente;  // UtenteSessioneResponse oppure AdminSessioneResponse — mai entity grezza
}
