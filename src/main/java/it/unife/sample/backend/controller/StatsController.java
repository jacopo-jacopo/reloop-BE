package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.Chat;
import it.unife.sample.backend.repository.AnnuncioRepository;
import it.unife.sample.backend.repository.ChatRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller REST per le statistiche aggregate della piattaforma.
 * Espone endpoint pubblici (senza autenticazione) e protetti.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    // Repository per accedere a chat, utenti e annunci
    private final ChatRepository chatRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AnnuncioRepository annuncioRepo;

    /**
     * GET /api/stats/pubbliche
     * Statistiche globali della piattaforma mostrate nella pagina di login.
     * Non richiede autenticazione — sono dati pubblici.
     *
     * @return Map con: scambi_completati, co2_totale_kg, utenti_attivi
     */
    @GetMapping("/pubbliche")
    public Map<String, Object> getPubbliche() {

        // Conta le chat nello stato "completata" — corrisponde agli scambi riusciti
        long scambi = chatRepo.countByStatoChat(Chat.StatoChat.completata);

        // Conta tutti gli utenti registrati
        long utenti = utenteRepo.count();

        // Somma la co2_totale di tutti gli utenti registrati
        // Ogni utente accumula CO₂ ogni volta che completa uno scambio
        double co2 = utenteRepo.findAll()
                .stream()
                .mapToDouble(u -> u.getCo2Totale().doubleValue())
                .sum();

        // Restituisce una Map che Jackson serializza automaticamente in JSON
        return Map.of(
            "scambi_completati", scambi,
            "co2_totale_kg",     co2,
            "utenti_attivi",     utenti
        );
    }

    /**
     * GET /api/stats/co2-quartiere?quartiere={idQuartiere}
     * Calcola la CO₂ totale risparmiata da tutti gli utenti di un quartiere.
     * Usato dalla home page per mostrare l'impatto ambientale del quartiere.
     * Richiede autenticazione (l'utente deve essere loggato per vedere la propria home).
     *
     * @param idQuartiere  ID del quartiere di cui calcolare la CO₂
     * @return Valore numerico (BigDecimal) dei kg di CO₂ risparmiati nel quartiere
     */
    @GetMapping("/co2-quartiere")
    public BigDecimal getCo2Quartiere(@RequestParam Long quartiere) {

        // Recupera tutti gli utenti del quartiere specificato
        // e somma il loro campo co2_totale (aggiornato ad ogni scambio completato)
        return utenteRepo.findByQuartiere_IdQuartiere(quartiere)
                .stream()
                .map(u -> u.getCo2Totale())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}