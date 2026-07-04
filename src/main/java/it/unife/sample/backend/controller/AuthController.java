package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LoginRequest;
import it.unife.sample.backend.dto.LoginResponse;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.security.JwtService;
import it.unife.sample.backend.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

// controller per la gestione dell'autenticazione: espone le API REST per il login, la registrazione, il logout e la verifica della sessione
@RestController // indica che questa classe è un controller REST, gestito da Spring
@RequestMapping("/api/auth") // mappa tutte le richieste HTTP che iniziano con /api/auth a questo controller
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, authService e jwtService)
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    private static final String COOKIE_NAME = "session";

    // endpoint per il login: riceve una richiesta di login, chiama il servizio di autenticazione e restituisce la risposta con un cookie di sessione
    @PostMapping("/login") // mappa le richieste POST a /api/auth/login a questo metodo
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) { // riceve una richiesta di login con i dati dell'utente 
                                                                                       // (email e password) nel corpo della richiesta;
                                                                                       // @Valid indica che i dati devono essere validati secondo le regole definite nella classe LoginRequest
                                                                                       // @RequestBody indica che i dati devono essere deserializzati dal corpo della richiesta HTTP
        LoginResponse resp = authService.login(req);
        ResponseCookie cookie = creaCookie(resp.getId(), resp.getTipo());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // imposta il cookie di sessione nella risposta HTTP
                .body(resp); // restituisce la risposta con le informazioni dell'utente e della sessione
    }

    // endpoint per la registrazione: riceve una richiesta di registrazione, chiama il servizio di autenticazione e 
    // restituisce la risposta con un cookie di sessione
    @PostMapping("/registra") // mappa le richieste POST a /api/auth/registra a questo metodo
    public ResponseEntity<LoginResponse> registra(@Valid @RequestBody RegistrazioneRequest req) { // riceve una richiesta di registrazione con i dati 
                                                                                                  // dell'utente nel corpo della richiesta;
                                                                                                  // @Valid indica che i dati devono essere validati secondo
                                                                                                  // le regole definite nella classe RegistrazioneRequest;
                                                                                                  // @RequestBody indica che i dati devono essere 
                                                                                                  // deserializzati dal corpo della richiesta HTTP
        LoginResponse resp = authService.registra(req);
        ResponseCookie cookie = creaCookie(resp.getId(), resp.getTipo());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // imposta il cookie di sessione nella risposta HTTP
                .body(resp); // restituisce la risposta con le informazioni dell'utente e della sessione
    }

    // endpoint per la verifica della sessione: riceve un cookie di sessione, chiama il servizio di autenticazione e 
    // restituisce la risposta con le informazioni dell'utente e della sessione
    @GetMapping("/me")  // mappa le richieste GET a /api/auth/me a questo metodo
    public ResponseEntity<LoginResponse> me(
            @CookieValue(value = COOKIE_NAME, required = false) String token) { // riceve un cookie di sessione con il nome COOKIE_NAME (session) 
                                                                                // dalla richiesta HTTP;
                                                                                // @CookieValue indica che il valore del cookie deve essere 
                                                                                // iniettato nel parametro token;
                                                                                // required = false indica che il cookie non è 
                                                                                // obbligatorio (può essere null)

        if (token == null) return ResponseEntity.status(401).build();  // se il cookie non è presente, restituisce HTTP 401 Unauthorized
        Claims claims = jwtService.validaEDecodifica(token);
        if (claims == null) return ResponseEntity.status(401).build(); // se il token non è valido, restituisce HTTP 401 Unauthorized

        Long idUtente = Long.valueOf(claims.getSubject()); // estrae l'id dell'utente dal token JWT (subject)
        String tipo = claims.get("tipo", String.class); // estrae il tipo di utente dal token JWT (admin o utente registrato)
        return ResponseEntity.ok(authService.me(idUtente, tipo)); // chiama il servizio di autenticazione per ottenere le informazioni dell'utente 
                                                                  // e della sessione, poi restituisce HTTP 200 OK
    }

    // endpoint per il logout: cancella il cookie di sessione e restituisce HTTP 200 OK
    @PostMapping("/logout") // mappa le richieste POST a /api/auth/logout a questo metodo
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "") // crea un cookie con il nome COOKIE_NAME (session) e il valore vuoto, 
                                                                           // in modo da cancellare il cookie di sessione
                .httpOnly(true)     // imposta il cookie come HttpOnly, in modo che non sia accessibile tramite JavaScript
                .secure(false)        // imposta il cookie come http (non https)
                .sameSite("Lax")    // imposta il cookie come SameSite=Lax, in modo che sia inviato solo per le richieste non cross-site
                .path("/")              // imposta il cookie come valido per tutte le richieste al dominio
                .maxAge(0)
                .build();                    // costruisce l'oggetto ResponseCookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("ok");
    }

    // metodo privato per creare un cookie di sessione: restituisce un oggetto ResponseCookie con \il token JWT generato dal servizio di autenticazione, 
    // impostando le proprietà del cookie (httpOnly, secure, sameSite, path e maxAge)
    private ResponseCookie creaCookie(Long idUtente, String tipo) {
        String token = jwtService.generaToken(idUtente, tipo);
        return ResponseCookie.from(COOKIE_NAME, token) // crea un cookie con il nome COOKIE_NAME e il valore token
                .httpOnly(true) // imposta il cookie come HttpOnly, in modo che non sia accessibile tramite JavaScript
                .secure(false) // imposta il cookie come http (non https)
                .sameSite("Lax") // imposta il cookie come SameSite=Lax, in modo che sia inviato solo per le richieste non cross-site
                .path("/") // imposta il cookie come valido per tutte le richieste al dominio
                .maxAge(Duration.ofDays(7)) 
                .build(); // costruisce l'oggetto ResponseCookie
    }
}
