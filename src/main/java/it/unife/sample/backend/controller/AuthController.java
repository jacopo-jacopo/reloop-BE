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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    private static final String COOKIE_NAME = "session";

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        ResponseCookie cookie = creaCookie(resp.getId(), resp.getTipo());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(resp);
    }

    @PostMapping("/registra")
    public ResponseEntity<LoginResponse> registra(@Valid @RequestBody RegistrazioneRequest req) {
        LoginResponse resp = authService.registra(req);
        ResponseCookie cookie = creaCookie(resp.getId(), resp.getTipo());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(
            @CookieValue(value = COOKIE_NAME, required = false) String token) {
        if (token == null) return ResponseEntity.status(401).build();
        Claims claims = jwtService.validaEDecodifica(token);
        if (claims == null) return ResponseEntity.status(401).build();

        Long idUtente = Long.valueOf(claims.getSubject());
        String tipo = claims.get("tipo", String.class);
        return ResponseEntity.ok(authService.me(idUtente, tipo));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("ok");
    }

    private ResponseCookie creaCookie(Long idUtente, String tipo) {
        String token = jwtService.generaToken(idUtente, tipo);
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true).secure(false).sameSite("Lax").path("/")
                .maxAge(Duration.ofDays(7)).build();
    }
}
