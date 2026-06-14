package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/segnalazioni")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SegnalazioneController {

    private final SegnalazioneRepository segnalazioneRepo;
    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AmministratoreRepository adminRepo;
    private final EliminaRepository eliminaRepo;

    /**
     * GET /api/segnalazioni
     * Tutte le segnalazioni — solo admin.
     */
    @GetMapping
    public List<Segnalazione> getTutte() {
        return segnalazioneRepo.findAll();
    }

    /**
     * GET /api/segnalazioni/mie
     * Segnalazioni inviate dall'utente loggato.
     */
    @GetMapping("/mie")
    public List<Segnalazione> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return segnalazioneRepo.findBySegnalante_IdUtenteReg(idUtente);
    }

    /**
     * POST /api/segnalazioni
     * Invia una segnalazione su un annuncio.
     */
    @PostMapping
    public ResponseEntity<?> invia(
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") Long idUtente) {

        Long idAnnuncio    = Long.valueOf(body.get("id_annuncio_segnalato").toString());
        String motivazione = body.get("motivazione").toString();

        Annuncio annuncio = annuncioRepo.findById(idAnnuncio).orElse(null);
        if (annuncio == null) return ResponseEntity.badRequest().body("Annuncio non trovato");

        UtenteRegistrato segnalante = utenteRepo.findById(idUtente).orElse(null);
        if (segnalante == null) return ResponseEntity.badRequest().body("Utente non trovato");

        // Impedisce di segnalare di nuovo lo stesso annuncio se l'utente ha già
        // una segnalazione non chiusa per esso
        if (segnalazioneRepo.existsBySegnalante_IdUtenteRegAndAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(
                idUtente, idAnnuncio, Segnalazione.StatoSegnalazione.chiusa)) {
            return ResponseEntity.status(409).body("Hai già segnalato questo annuncio");
        }

        Segnalazione s = new Segnalazione();
        s.setAnnuncioSegnalato(annuncio);
        s.setSegnalante(segnalante);
        s.setMotivazione(motivazione);
        s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.in_attesa);

        return ResponseEntity.ok(segnalazioneRepo.save(s));
    }

    /**
     * PUT /api/segnalazioni/{id}/carico
     * Prende in carico una segnalazione — solo admin.
     * Associa l'admin e aggiorna lo stato a presa_in_carico.
     */
    @PutMapping("/{id}/carico")
    public ResponseEntity<?> prendiInCarico(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idAdmin) {

        return segnalazioneRepo.findById(id).map(s -> {
            s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.presa_in_carico);
            adminRepo.findById(idAdmin).ifPresent(s::setAmministratore);
            return ResponseEntity.ok(segnalazioneRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/segnalazioni/{id}/chiudi
     * Chiude la segnalazione. Se oscura_annuncio=true imposta stato oscurato
     * sull'annuncio e registra la relazione "elimina" (quale admin ha oscurato
     * quale annuncio) nel DB.
     */
    @PutMapping("/{id}/chiudi")
    public ResponseEntity<?> chiudi(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") Long idAdmin) {

        boolean oscura = Boolean.parseBoolean(body.get("oscura_annuncio").toString());

        return segnalazioneRepo.findById(id).map(s -> {
            s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.chiusa);

            if (oscura) {
                // Oscura l'annuncio — rimane nel DB ma non è più visibile agli utenti
                Annuncio annuncio = s.getAnnuncioSegnalato();
                annuncio.setStatoAnnuncio(Annuncio.StatoAnnuncio.oscurato);
                annuncioRepo.save(annuncio);

                // Registra chi ha oscurato l'annuncio
                Amministratore admin = adminRepo.findById(idAdmin).orElse(null);
                if (admin != null) {
                    Elimina.EliminaId eliminaId = new Elimina.EliminaId();
                    eliminaId.setIdUtenteAdm(admin.getIdUtenteAdm());
                    eliminaId.setIdAnnuncioEliminato(annuncio.getIdAnnuncio());

                    Elimina elimina = new Elimina();
                    elimina.setId(eliminaId);
                    elimina.setAmministratore(admin);
                    elimina.setAnnuncioEliminato(annuncio);
                    eliminaRepo.save(elimina);
                }
            }

            return ResponseEntity.ok(segnalazioneRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }
}