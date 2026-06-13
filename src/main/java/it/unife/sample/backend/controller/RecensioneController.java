package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST per le recensioni tra utenti.
 * Le recensioni vengono lasciate dopo il completamento di uno scambio.
 * La chiave primaria è composita (id_recensore, id_recensito) —
 * questo garantisce che ogni coppia possa recensirsi al massimo una volta.
 */
@RestController
@RequestMapping("/api/recensioni")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RecensioneController {

    // Repository per recensioni e utenti
    private final RecensioneRepository recensioneRepo;
    private final UtenteRegistratoRepository utenteRepo;

    /**
     * GET /api/recensioni/{idUtente}
     * Restituisce tutte le recensioni ricevute da un utente.
     * Usato nel profilo pubblico (user-overlay) e nella pagina profilo personale.
     *
     * @param idUtente  ID dell'utente di cui visualizzare le recensioni
     * @return Lista di recensioni ricevute con voto e testo
     */
    @GetMapping("/{idUtente}")
    public List<Recensione> getByUtente(@PathVariable Long idUtente) {
        // Cerca per id_utente_reg_recensito (chi ha ricevuto la recensione)
        return recensioneRepo.findById_IdUtenteRegRecensito(idUtente);
    }

    /**
     * POST /api/recensioni
     * Invia una recensione dopo uno scambio completato.
     * Vincoli:
     * - Una coppia (recensore, recensito) può avere al massimo una recensione
     * - Sia recensore che recensito devono esistere nel database
     *
     * @param body       Map con id_utente_reg_recensito, voto (1-5), descrizione_recensione
     * @param idRecensore  ID dell'utente che lascia la recensione dall'header
     * @return 200 con la recensione salvata,
     *         409 se già recensito,
     *         400 se utente non trovato
     */
    @PostMapping
    public ResponseEntity<?> invia(
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") Long idRecensore) {

        // Estrae i dati dal body
        Long idRecensito = Long.valueOf(body.get("id_utente_reg_recensito").toString());
        Integer voto     = Integer.valueOf(body.get("voto").toString());
        String descrizione = body.get("descrizione_recensione").toString();

        // Vincolo di unicità: controlla se la recensione esiste già
        if (recensioneRepo.existsById_IdUtenteRegRecensoreAndId_IdUtenteRegRecensito(
                idRecensore, idRecensito)) {
            return ResponseEntity.status(409).body("Recensione già inviata");
        }

        // Verifica esistenza di entrambi gli utenti
        UtenteRegistrato recensore = utenteRepo.findById(idRecensore).orElse(null);
        UtenteRegistrato recensito = utenteRepo.findById(idRecensito).orElse(null);
        if (recensore == null || recensito == null)
            return ResponseEntity.badRequest().body("Utente non trovato");

        // Costruisce la chiave composita della recensione
        Recensione.RecensioneId recId = new Recensione.RecensioneId();
        recId.setIdUtenteRegRecensore(idRecensore);
        recId.setIdUtenteRegRecensito(idRecensito);

        // Costruisce e salva la recensione
        Recensione r = new Recensione();
        r.setId(recId);
        r.setRecensore(recensore);
        r.setRecensito(recensito);
        r.setVoto(voto);
        r.setDescrizioneRecensione(descrizione);
        // data_recensione viene impostata automaticamente dal @PrePersist nel model

        return ResponseEntity.ok(recensioneRepo.save(r));
    }
}