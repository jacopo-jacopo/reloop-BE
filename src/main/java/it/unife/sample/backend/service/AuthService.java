package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.AmministratoreDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.LoginRequest;
import it.unife.sample.backend.dto.LoginResponse;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.dto.response.AdminSessioneResponse;
import it.unife.sample.backend.dto.response.UtenteSessioneResponse;
import it.unife.sample.backend.model.Amministratore;
import it.unife.sample.backend.model.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtenteDao utenteDao;
    private final AmministratoreDao amministratoreDao;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!?#\\-_]).{8,}$");

    public LoginResponse login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        if (email.toLowerCase().endsWith("@reloop.it")) {
            Amministratore admin = amministratoreDao.findByEmail(email)
                    .filter(a -> a.getPassword().equals(password))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));
            return new LoginResponse("admin", admin.getIdUtenteAdm(), admin.getNomeCompleto(), admin.getEmail(),
                    new AdminSessioneResponse(admin.getIdUtenteAdm(), admin.getNomeCompleto()));
        }

        UtenteRegistrato utente = utenteDao.findEntityByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));

        UtenteSessioneResponse sessione = utenteDao.findSessioneById(utente.getIdUtenteReg())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessione non valida"));
        return new LoginResponse("utente", utente.getIdUtenteReg(), utente.getNomeCompleto(), utente.getEmail(), sessione);
    }

    public LoginResponse registra(RegistrazioneRequest req) {
        if (req.getEmail() != null && req.getEmail().toLowerCase().endsWith("@reloop.it")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email non valida");
        }
        if (utenteDao.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già in uso");
        }
        if (req.getPassword() == null || !PASSWORD_PATTERN.matcher(req.getPassword()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password non valida");
        }

        UtenteSessioneResponse sessione = utenteDao.crea(req);
        UtenteRegistrato entity = utenteDao.findEntityByEmail(req.getEmail()).orElseThrow();
        return new LoginResponse("utente", entity.getIdUtenteReg(), entity.getNomeCompleto(), entity.getEmail(), sessione);
    }

    public LoginResponse me(Long idUtente, String tipo) {
        if ("admin".equals(tipo)) {
            Amministratore admin = amministratoreDao.findById(idUtente)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessione non valida"));
            return new LoginResponse("admin", admin.getIdUtenteAdm(), admin.getNomeCompleto(), admin.getEmail(),
                    new AdminSessioneResponse(admin.getIdUtenteAdm(), admin.getNomeCompleto()));
        }

        UtenteRegistrato entity = utenteDao.findEntityById(idUtente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessione non valida"));
        UtenteSessioneResponse sessione = utenteDao.findSessioneById(idUtente).orElseThrow();
        return new LoginResponse("utente", entity.getIdUtenteReg(), entity.getNomeCompleto(), entity.getEmail(), sessione);
    }
}
