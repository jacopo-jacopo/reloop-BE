package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST per le proposte di scambio.
 * Gestisce il ciclo di vita di una proposta:
 * creazione → accettazione/rifiuto → creazione automatica della chat.
 */
@RestController
@RequestMapping("/api/proposte")
@RequiredArgsConstructor
public class PropostaController {

    // Repository necessari per la gestione delle proposte
    private final PropostaRepository propostaRepo;
    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AnnuncioInclusoRepository annuncioInclusoRepo;
    private final ChatRepository chatRepo;

    /**
     * GET /api/proposte/ricevute
     * Restituisce le proposte ricevute dall'utente loggato,
     * ovvero quelle relative agli annunci che lui ha pubblicato.
     *
     * @param idUtente  ID dell'utente loggato dall'header
     * @return Lista di proposte ricevute
     */
    /**
     * GET /api/proposte/badge
     * Conta le proposte in_attesa ricevute dopo l'ultima visita alla sezione.
     */
    @GetMapping("/badge")
    public long getBadge(@RequestHeader("X-User-Id") Long idUtente) {
        java.time.LocalDateTime ultimaVisita = utenteRepo.findById(idUtente)
            .map(u -> u.getUltimaVisitaProposte())
            .orElse(null);
        return propostaRepo.countNuoveProposteRicevute(idUtente, ultimaVisita);
    }

    @GetMapping("/ricevute")
    public List<Proposta> getRicevute(@RequestHeader("X-User-Id") Long idUtente) {
        // Cerca le proposte dove il pubblicante dell'annuncio di interesse è l'utente loggato
        return propostaRepo.findByAnnuncioInteresse_Pubblicante_IdUtenteRegOrderByTimestampPropostaDesc(idUtente);
    }

    /**
     * GET /api/proposte/inviate
     * Restituisce le proposte inviate dall'utente loggato.
     *
     * @param idUtente  ID dell'utente loggato dall'header
     * @return Lista di proposte inviate
     */
    @GetMapping("/inviate")
    public List<Proposta> getInviate(@RequestHeader("X-User-Id") Long idUtente) {
        // Cerca le proposte dove il proponente è l'utente loggato
        return propostaRepo.findByProponente_IdUtenteRegOrderByTimestampPropostaDesc(idUtente);
    }

    /**
     * POST /api/proposte
     * Crea una nuova proposta di scambio.
     * Il proponente è ricavato dall'header X-User-Id.
     * Gli annunci offerti vengono salvati nella tabella annuncio_incluso.
     *
     * @param body      Map con id_annuncio_interesse e id_annunci_offerti (lista di ID)
     * @param idUtente  ID dell'utente loggato dall'header
     * @return 200 con la proposta creata, 400 se annuncio o utente non trovati
     */
    @PostMapping
    public ResponseEntity<?> invia(
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") Long idUtente) {

        // Estrae l'ID dell'annuncio di interesse dal body
        Long idAnnuncioInteresse = Long.valueOf(body.get("id_annuncio_interesse").toString());

        // Estrae la lista degli ID degli annunci offerti
        List<Integer> idAnnunciOfferti = (List<Integer>) body.get("id_annunci_offerti");

        // Verifica che l'annuncio di interesse esista
        Annuncio annuncioInteresse = annuncioRepo.findById(idAnnuncioInteresse).orElse(null);
        if (annuncioInteresse == null)
            return ResponseEntity.badRequest().body("Annuncio non trovato");

        // Verifica che il proponente esista
        UtenteRegistrato proponente = utenteRepo.findById(idUtente).orElse(null);
        if (proponente == null)
            return ResponseEntity.badRequest().body("Utente non trovato");

        // Crea la proposta con stato iniziale "in_attesa"
        Proposta proposta = new Proposta();
        proposta.setAnnuncioInteresse(annuncioInteresse);
        proposta.setProponente(proponente);
        proposta.setStatoProposta(Proposta.StatoProposta.in_attesa);

        // Salva la proposta per ottenere l'ID generato
        Proposta salvata = propostaRepo.save(proposta);

        // Salva gli annunci offerti nella tabella annuncio_incluso (relazione ternaria)
        for (Integer idAnn : idAnnunciOfferti) {
            annuncioRepo.findById(Long.valueOf(idAnn)).ifPresent(ann -> {

                // Costruisce la chiave composita (id_proposta, id_annuncio_offerto)
                AnnuncioIncluso incluso = new AnnuncioIncluso();
                AnnuncioIncluso.AnnuncioInclusoId incId = new AnnuncioIncluso.AnnuncioInclusoId();
                incId.setIdProposta(salvata.getIdProposta());
                incId.setIdAnnuncioOfferto(ann.getIdAnnuncio());

                incluso.setId(incId);
                incluso.setProposta(salvata);
                incluso.setAnnuncioOfferto(ann);
                // flag_selezionato inizialmente false — diventa true quando accettato
                annuncioInclusoRepo.save(incluso);
            });
        }

        return ResponseEntity.ok(salvata);
    }

    /**
     * PUT /api/proposte/{id}/accetta
     * Accetta una proposta di scambio.
     * Effetti collaterali (tutti atomici nella stessa transazione logica):
     * 1. Stato proposta → accettata
     * 2. Annuncio di interesse → sospeso
     * 3. Annuncio scelto tra gli offerti → sospeso + flagSelezionato = true
     * 4. Crea automaticamente una chat tra i due utenti
     *
     * @param id    ID della proposta da accettare
     * @param body  Map con id_annuncio_scelto (quale degli annunci offerti è stato scelto)
     * @return 200 con la proposta aggiornata, 404 se non trovata
     */
    @PutMapping("/{id}/accetta")
    public ResponseEntity<?> accetta(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        // Verifica che la proposta esista
        Proposta proposta = propostaRepo.findById(id).orElse(null);
        if (proposta == null) return ResponseEntity.notFound().build();

        // Aggiorna lo stato della proposta
        proposta.setStatoProposta(Proposta.StatoProposta.accettata);
        propostaRepo.save(proposta);

        // Sospendi l'annuncio di interesse (non più disponibile per altre proposte)
        Annuncio annuncioInteresse = proposta.getAnnuncioInteresse();
        annuncioInteresse.setStatoAnnuncio(Annuncio.StatoAnnuncio.sospeso);
        annuncioRepo.save(annuncioInteresse);

        // Sospendi l'annuncio offerto scelto, marcalo come selezionato,
        // e rifiuta automaticamente tutte le proposte in_attesa che coinvolgono i due annunci sospesi
        Long idScelto = body.containsKey("id_annuncio_scelto")
                ? Long.valueOf(body.get("id_annuncio_scelto").toString())
                : null;

        if (idScelto != null) {
            annuncioRepo.findById(idScelto).ifPresent(ann -> {
                ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.sospeso);
                annuncioRepo.save(ann);

                AnnuncioIncluso.AnnuncioInclusoId incId = new AnnuncioIncluso.AnnuncioInclusoId();
                incId.setIdProposta(id);
                incId.setIdAnnuncioOfferto(idScelto);
                annuncioInclusoRepo.findById(incId).ifPresent(inc -> {
                    inc.setFlagSelezionato(true);
                    annuncioInclusoRepo.save(inc);
                });
            });
        }

        // Rifiuta automaticamente tutte le proposte in_attesa che coinvolgono
        // l'annuncio_interesse o l'annuncio scelto (ora entrambi sospesi)
        for (Long idAnn : new Long[]{annuncioInteresse.getIdAnnuncio(), idScelto}) {
            if (idAnn == null) continue;
            // Proposte dove questo annuncio è l'annuncio_interesse
            propostaRepo.findByAnnuncioInteresse_IdAnnuncioAndStatoPropostaAndIdPropostaNot(
                    idAnn, Proposta.StatoProposta.in_attesa, id)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });
            // Proposte dove questo annuncio compare tra gli annunci_offerti
            propostaRepo.findInAttesaByAnnuncioOfferto(idAnn, id)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });
        }

        // Crea la chat automaticamente dopo l'accettazione della proposta
        Chat chat = new Chat();
        chat.setPropostaGenerante(proposta); // Collega la chat alla proposta
        chat.setStatoChat(Chat.StatoChat.aperta);
        chatRepo.save(chat);

        return ResponseEntity.ok(proposta);
    }

    /**
     * PUT /api/proposte/{id}/rifiuta
     * Rifiuta una proposta di scambio.
     * Gli annunci coinvolti rimangono attivi — nessun effetto collaterale.
     *
     * @param id  ID della proposta da rifiutare
     * @return 200 con la proposta aggiornata, 404 se non trovata
     */
    @PutMapping("/{id}/rifiuta")
    public ResponseEntity<?> rifiuta(@PathVariable Long id) {
        return propostaRepo.findById(id).map(p -> {
            // Semplice aggiornamento dello stato — nessun effetto su annunci o chat
            p.setStatoProposta(Proposta.StatoProposta.rifiutata);
            return ResponseEntity.ok(propostaRepo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }
}