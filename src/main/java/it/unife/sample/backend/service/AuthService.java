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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

// servizio per la gestione dell'autenticazione: fornisce metodi per il login, la registrazione e la verifica della sessione
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, utenteDao e amministratoreDao)
public class AuthService {

    private final UtenteDao utenteDao;
    private final AmministratoreDao amministratoreDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // pattern per validare la password
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!?#\\-_]).{8,}$");

    // metodo per il login: verifica le credenziali dell'utente e restituisce un oggetto LoginResponse con le informazioni dell'utente e della sessione
    public LoginResponse login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        // verifica se l'email appartiene a un amministratore (e in tal caso, cerca l'amministratore nel database e verifica la password)
        if (email.toLowerCase().endsWith("@reloop.it")) {
            Amministratore admin = amministratoreDao.findByEmail(email)
                    .filter(a -> passwordEncoder.matches(password, a.getPassword()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));
            return new LoginResponse("admin", admin.getIdUtenteAdm(), admin.getNomeCompleto(), admin.getEmail(),
                    new AdminSessioneResponse(admin.getIdUtenteAdm(), admin.getNomeCompleto()));
        }

        // altrimenti, cerca l'utente registrato nel database e verifica la password
        UtenteRegistrato utente = utenteDao.findEntityByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));

        // se le credenziali sono valide, cerca la sessione dell'utente nel database e restituisce un oggetto LoginResponse
        // con le informazioni dell'utente e della sessione
        UtenteSessioneResponse sessione = utenteDao.findSessioneById(utente.getIdUtenteReg())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessione non valida"));
        return new LoginResponse("utente", utente.getIdUtenteReg(), utente.getNomeCompleto(), utente.getEmail(), sessione);
    }

    // metodo per la registrazione: verifica i dati dell'utente e crea un nuovo utente registrato nel database,
    // restituendo un oggetto LoginResponse con le informazioni dell'utente e della sessione
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

    // metodo per la verifica della sessione: riceve l'id dell'utente e il tipo di utente (admin o utente registrato)
    // e restituisce un oggetto LoginResponse con le informazioni dell'utente e della sessione
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
