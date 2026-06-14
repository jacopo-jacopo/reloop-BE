package it.unife.sample.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera e valida i JWT usati per mantenere la sessione utente
 * tramite un cookie HttpOnly (vedi AuthController).
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un JWT firmato contenente l'ID e il tipo (utente/admin) dell'utente loggato.
     */
    public String generaToken(Long idUtente, String tipo) {
        Date now = new Date();
        Date scadenza = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(idUtente))
                .claim("tipo", tipo)
                .issuedAt(now)
                .expiration(scadenza)
                .signWith(key())
                .compact();
    }

    /**
     * Valida il token e ne estrae i claim. Restituisce null se il token
     * non è valido o è scaduto.
     */
    public Claims validaEDecodifica(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
