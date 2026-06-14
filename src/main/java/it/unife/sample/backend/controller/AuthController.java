package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LoginRequest;
import it.unife.sample.backend.dto.LoginResponse;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.model.Amministratore;
import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.AmministratoreRepository;
import it.unife.sample.backend.repository.QuartiereRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import it.unife.sample.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controller REST per autenticazione e registrazione.
 * Espone gli endpoint sotto /api/auth.
 * La sessione viene mantenuta tramite un cookie HttpOnly contenente un JWT,
 * mentre l'header X-User-Id resta usato come identificativo applicativo
 * nelle altre richieste.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Repository per utenti registrati, amministratori e quartieri
    private final UtenteRegistratoRepository utenteRepo;
    private final AmministratoreRepository adminRepo;
    private final QuartiereRepository quartiereRepo;
    private final JwtService jwtService;

    private static final String COOKIE_NAME = "session";

    /**
     * Costruisce il cookie HttpOnly contenente il JWT di sessione.
     */
    private ResponseCookie creaCookieSessione(Long idUtente, String tipo) {
        String token = jwtService.generaToken(idUtente, tipo);
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(java.time.Duration.ofDays(7))
                .build();
    }

    // Almeno 8 caratteri, 1 maiuscola, 1 minuscola, 1 numero, 1 carattere speciale tra !?#-_
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!?#\\-_]).{8,}$");

    /**
     * POST /api/auth/login
     * Gestisce il login di utenti e amministratori.
     * La distinzione avviene tramite il dominio email:
     * - @reloop.it → cerca nella tabella amministratore
     * - altri domini → cerca nella tabella utente_registrato
     *
     * @param req  Body con email e password
     * @return 200 con LoginResponse (tipo + id + nomeCompleto + email + oggetto utente),
     *         401 se le credenziali non sono valide
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String email    = req.getEmail();
        String password = req.getPassword();

        // RBAC basato sul dominio email — @reloop.it indica un moderatore/admin
        if (email.toLowerCase().endsWith("@reloop.it")) {

            // Cerca l'amministratore per email
            Optional<Amministratore> admin = adminRepo.findByEmail(email);

            // Verifica esistenza e password (confronto in chiaro — in produzione usare BCrypt)
            if (admin.isEmpty() || !admin.get().getPassword().equals(password)) {
                return ResponseEntity.status(401).body("Credenziali non valide");
            }

            Amministratore a = admin.get();

            // Restituisce LoginResponse con tipo "admin" e imposta il cookie di sessione
            ResponseCookie cookie = creaCookieSessione(a.getIdUtenteAdm(), "admin");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new LoginResponse(
                        "admin",
                        a.getIdUtenteAdm(),
                        a.getNomeCompleto(),
                        a.getEmail(),
                        a  // L'oggetto completo admin viene incluso per il frontend
                    ));
        }

        // Per tutti gli altri domini — cerca tra gli utenti registrati
        Optional<UtenteRegistrato> utente = utenteRepo.findByEmail(email);

        // Verifica esistenza e password
        if (utente.isEmpty() || !utente.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Credenziali non valide");
        }

        UtenteRegistrato u = utente.get();

        // Restituisce LoginResponse con tipo "utente" e imposta il cookie di sessione
        ResponseCookie cookie = creaCookieSessione(u.getIdUtenteReg(), "utente");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(
                    "utente",
                    u.getIdUtenteReg(),
                    u.getNomeCompleto(),
                    u.getEmail(),
                    u  // L'oggetto completo utente viene incluso per il frontend
                ));
    }

    /**
     * POST /api/auth/registra
     * Registra un nuovo utente nella piattaforma.
     * Verifica che l'email non sia già in uso e che il quartiere esista.
     * Dopo la registrazione restituisce direttamente una LoginResponse
     * così il frontend può loggare l'utente immediatamente.
     *
     * @param req  Body con nomeCompleto, email, password, indirizzo, idQuartiere
     * @return 200 con LoginResponse, 409 se email già in uso, 400 se quartiere non trovato
     */
    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody RegistrazioneRequest req) {

        // Il dominio @reloop.it è riservato agli amministratori
        if (req.getEmail() != null && req.getEmail().toLowerCase().endsWith("@reloop.it")) {
            return ResponseEntity.status(400).body("Email non valida");
        }

        // Vincolo di unicità email — restituisce 409 Conflict se già esiste
        if (utenteRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(409).body("Email già in uso");
        }

        // La password deve avere almeno 8 caratteri, 1 maiuscola, 1 minuscola, 1 numero e 1 carattere speciale (!?#-_)
        if (req.getPassword() == null || !PASSWORD_PATTERN.matcher(req.getPassword()).matches()) {
            return ResponseEntity.status(400).body("Password non valida");
        }

        // Verifica che il quartiere selezionato esista nel database
        Optional<Quartiere> quartiere = quartiereRepo.findById(req.getIdQuartiere());
        if (quartiere.isEmpty()) {
            return ResponseEntity.status(400).body("Quartiere non trovato");
        }

        // Costruisce il nuovo utente con i dati della richiesta
        UtenteRegistrato nuovo = new UtenteRegistrato();
        nuovo.setNomeCompleto(req.getNomeCompleto());
        nuovo.setEmail(req.getEmail());
        nuovo.setPassword(req.getPassword());   // In produzione: BCryptPasswordEncoder
        nuovo.setIndirizzo(req.getIndirizzo());
        nuovo.setQuartiere(quartiere.get());
        // punteggio e co2_totale vengono inizializzati a 0 dal model (@Column default)

        // Salva nel database e ottiene l'utente con l'ID generato
        UtenteRegistrato salvato = utenteRepo.save(nuovo);

        // Restituisce LoginResponse identica al login — il frontend fa login automatico
        ResponseCookie cookie = creaCookieSessione(salvato.getIdUtenteReg(), "utente");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(
                    "utente",
                    salvato.getIdUtenteReg(),
                    salvato.getNomeCompleto(),
                    salvato.getEmail(),
                    salvato
                ));
    }

    /**
     * GET /api/auth/me
     * Verifica il cookie di sessione e, se valido, restituisce i dati
     * dell'utente/admin loggato — usato all'avvio dell'app per ripristinare
     * la sessione dopo un refresh della pagina.
     *
     * @param token  JWT letto dal cookie "session" (assente se non loggato)
     * @return 200 con LoginResponse se il cookie è valido, 401 altrimenti
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(value = COOKIE_NAME, required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body("Non autenticato");
        }

        Claims claims = jwtService.validaEDecodifica(token);
        if (claims == null) {
            return ResponseEntity.status(401).body("Sessione non valida");
        }

        Long idUtente = Long.valueOf(claims.getSubject());
        String tipo = claims.get("tipo", String.class);

        if ("admin".equals(tipo)) {
            Optional<Amministratore> admin = adminRepo.findById(idUtente);
            if (admin.isEmpty()) return ResponseEntity.status(401).body("Sessione non valida");
            Amministratore a = admin.get();
            return ResponseEntity.ok(new LoginResponse(
                "admin", a.getIdUtenteAdm(), a.getNomeCompleto(), a.getEmail(), a
            ));
        }

        Optional<UtenteRegistrato> utente = utenteRepo.findById(idUtente);
        if (utente.isEmpty()) return ResponseEntity.status(401).body("Sessione non valida");
        UtenteRegistrato u = utente.get();
        return ResponseEntity.ok(new LoginResponse(
            "utente", u.getIdUtenteReg(), u.getNomeCompleto(), u.getEmail(), u
        ));
    }

    /**
     * POST /api/auth/logout
     * Rimuove il cookie di sessione.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("ok");
    }
}