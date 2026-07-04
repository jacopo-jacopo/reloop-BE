package it.unife.sample.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// genera e valida i JWT usati per mantenere la sessione utente tramite un cookie HttpOnly (vedi AuthController)
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
public class JwtService {

    @Value("${jwt.secret}") // legge la stringa segreta dal file di configurazione application.properties
    private String secret;  

    @Value("${jwt.expiration-ms}") // legge il tempo di scadenza del token (in millisecondi) dal file di configurazione application.properties
    private long expirationMs;

    // genera la chiave segreta per firmare e verificare i JWT a partire dalla stringa segreta
    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // genera un JWT firmato contenente l'ID e il tipo (utente/admin) dell'utente loggato
    public String generaToken(Long idUtente, String tipo) {
        Date now = new Date();
        Date scadenza = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(idUtente))
                .claim("tipo", tipo) // aggiunge un claim personalizzato "tipo" al token
                .issuedAt(now)
                .expiration(scadenza)
                .signWith(key()) // firma il token con la chiave segreta
                .compact(); // restituisce il token come stringa
    }

    // valida il token e ne estrae i claim; restituisce null se il token non è valido o è scaduto
    public Claims validaEDecodifica(String token) {
        try {
            return Jwts.parser() // crea un parser per i JWT
                    .verifyWith(key()) // imposta la chiave segreta per verificare la firma del token
                    .build() // costruisce il parser
                    .parseSignedClaims(token) // analizza il token firmato e ne estrae i claim
                    .getPayload(); // restituisce i claim del token come oggetto Claims
        } catch (Exception e) {
            return null;
        }
    }
}
