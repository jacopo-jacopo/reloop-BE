package it.unife.sample.backend.controller;


import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per la gestione del profilo utente.
 * Espone gli endpoint sotto /api/utenti.
 */
@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UtenteController {

    // Repository per utenti, badge, annunci, chat
    private final UtenteRegistratoRepository utenteRepo;
    private final BadgeOttenutoRepository badgeOttenutoRepo;
    private final BadgeRepository badgeRepo;
    private final AnnuncioRepository annuncioRepo;
    private final QuartiereRepository quartiereRepo;
    private final ChatRepository chatRepo;

    /**
     * GET /api/utenti/me
     * Restituisce il profilo dell'utente loggato.
     * Usato dalla pagina profilo per mostrare nome, punteggio, quartiere, CO₂.
     *
     * @param idUtente  ID dall'header X-User-Id
     * @return 200 con il profilo, 404 se non trovato
     */
    @GetMapping("/me")
    public ResponseEntity<UtenteRegistrato> getMe(
            @RequestHeader("X-User-Id") Long idUtente) {
        return utenteRepo.findById(idUtente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/utenti/{id}
     * Restituisce il profilo pubblico di un utente per ID, incluso il numero
     * di scambi completati. Usato dall'overlay profilo pubblico quando si
     * clicca su un utente.
     *
     * @param id  ID dell'utente da visualizzare
     * @return 200 con il profilo, 404 se non trovato
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return utenteRepo.findById(id)
                .map(u -> ResponseEntity.ok(Map.of(
                        "id_utente_reg",      u.getIdUtenteReg(),
                        "nome_completo",      u.getNomeCompleto(),
                        "indirizzo",          u.getIndirizzo(),
                        "punteggio",          u.getPunteggio(),
                        "co2_totale",         u.getCo2Totale(),
                        "foto_profilo",       u.getFotoProfilo() != null ? u.getFotoProfilo() : "",
                        "quartiere",          u.getQuartiere(),
                        "scambi_completati",  chatRepo.countCompletateByUtente(id)
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/utenti/leaderboard
     * Restituisce tutti gli utenti ordinati per punteggio decrescente.
     * Usato dalla sezione classifica nella pagina profilo.
     *
     * @return Lista ordinata per punteggio (il primo è il migliore)
     */
    @GetMapping("/leaderboard")
    public List<UtenteRegistrato> getLeaderboard() {
        return utenteRepo.findLeaderboard();
    }

    /**
     * GET /api/utenti/me/badge
     * Restituisce i badge già sbloccati dall'utente loggato.
     * Confrontato con tutti i badge per calcolare i bloccati e le progress bar.
     *
     * @param idUtente  ID dall'header X-User-Id
     * @return Lista di BadgeOttenuto con data di sblocco
     */
    @GetMapping("/me/badge")
    public List<BadgeOttenuto> getMieiBadge(
            @RequestHeader("X-User-Id") Long idUtente) {
        return badgeOttenutoRepo.findById_IdUtenteReg(idUtente);
    }

    /**
     * GET /api/utenti/badge/tutti
     * Restituisce tutti i badge disponibili nella piattaforma.
     * Usato per mostrare i badge bloccati con la progress bar verso la soglia.
     *
     * @return Lista completa di badge con nome, soglia, emoji, colore
     */
    @GetMapping("/badge/tutti")
    public List<Badge> getTuttiBadge() {
        return badgeRepo.findAll();
    }

    /**
     * GET /api/utenti/me/annunci
     * Restituisce tutti gli annunci dell'utente loggato (tutti gli stati).
     * Usato dalla sezione "I tuoi annunci" nel profilo.
     *
     * @param idUtente  ID dall'header X-User-Id
     * @return Lista di annunci (attivi, sospesi, chiusi)
     */
    @GetMapping("/me/annunci")
    public List<Annuncio> getMieiAnnunci(
            @RequestHeader("X-User-Id") Long idUtente) {
        return annuncioRepo.findByPubblicante_IdUtenteReg(idUtente);
    }

    /**
     * PUT /api/utenti/me
     * Aggiorna parzialmente il profilo dell'utente loggato.
     * Usato dal modal "Modifica profilo" per aggiornare indirizzo, quartiere, password.
     * Solo i campi non-null nel body vengono aggiornati (PATCH semantics).
     * La foto profilo viene gestita lato frontend in localStorage (base64).
     *
     * @param idUtente  ID dall'header X-User-Id
     * @param dati      Campi da aggiornare
     * @return 200 con il profilo aggiornato, 404 se non trovato
     */
   @PutMapping("/me")
public ResponseEntity<UtenteRegistrato> aggiorna(
        @RequestHeader("X-User-Id") Long idUtente,
        @RequestBody Map<String, Object> dati) {

    return utenteRepo.findById(idUtente).map(u -> {

        // Aggiorna solo i campi presenti nel body
        if (dati.containsKey("nome_completo"))
            u.setNomeCompleto(dati.get("nome_completo").toString());

        if (dati.containsKey("indirizzo"))
            u.setIndirizzo(dati.get("indirizzo").toString());

        if (dati.containsKey("password"))
            u.setPassword(dati.get("password").toString());

        // Foto profilo — stringa base64 completa
        if (dati.containsKey("foto_profilo")) {
            Object foto = dati.get("foto_profilo");
            u.setFotoProfilo(foto != null ? foto.toString() : null);
        }

        // Quartiere — oggetto con id_quartiere
        if (dati.containsKey("quartiere")) {
            Map<String, Object> q = (Map<String, Object>) dati.get("quartiere");
            if (q != null && q.containsKey("id_quartiere")) {
                Long idQ = Long.valueOf(q.get("id_quartiere").toString());
                quartiereRepo.findById(idQ).ifPresent(u::setQuartiere);
            }
        }

        return ResponseEntity.ok(utenteRepo.save(u));

    }).orElse(ResponseEntity.notFound().build());
}

/** PUT /api/utenti/visita-proposte — aggiorna ultima_visita_proposte a ora */
@PutMapping("/visita-proposte")
public ResponseEntity<?> visitaProposte(@RequestHeader("X-User-Id") Long idUtente) {
    utenteRepo.findById(idUtente).ifPresent(u -> {
        u.setUltimaVisitaProposte(LocalDateTime.now());
        utenteRepo.save(u);
    });
    return ResponseEntity.ok().build();
}

/** PUT /api/utenti/visita-chat — aggiorna ultima_visita_chat a ora */
@PutMapping("/visita-chat")
public ResponseEntity<?> visitaChat(@RequestHeader("X-User-Id") Long idUtente) {
    utenteRepo.findById(idUtente).ifPresent(u -> {
        u.setUltimaVisitaChat(LocalDateTime.now());
        utenteRepo.save(u);
    });
    return ResponseEntity.ok().build();
}
}