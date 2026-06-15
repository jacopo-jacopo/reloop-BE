package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.Annuncio;
import it.unife.sample.backend.model.Foto;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.AnnuncioRepository;
import it.unife.sample.backend.repository.FotoRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annunci")
@RequiredArgsConstructor
public class AnnuncioController {

    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final FotoRepository fotoRepo;

    /**
     * GET /api/annunci
     * Annunci attivi del quartiere dell'utente, esclusi i propri.
     */
    @GetMapping
    public List<Annuncio> getAll(
            @RequestParam(required = false) String cerca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long quartiere,
            @RequestHeader("X-User-Id") Long idUtente) {

        if (cerca != null)
            return annuncioRepo.findByTitoloContainingIgnoreCase(cerca);

        if (categoria != null)
            return annuncioRepo.findByCategoriaContainingIgnoreCase(categoria);

        if (quartiere != null)
            return annuncioRepo
                .findByPubblicante_Quartiere_IdQuartiereAndStatoAnnuncioAndPubblicante_IdUtenteRegNot(
                    quartiere,
                    Annuncio.StatoAnnuncio.attivo,
                    idUtente
                );

        return annuncioRepo.findAll();
    }

    /**
     * GET /api/annunci/{id}
     * Singolo annuncio — usato dall'admin nell'ispezione segnalazioni.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Annuncio> getById(@PathVariable Long id) {
        return annuncioRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/annunci/{id}/foto
     * Lista di stringhe base64 delle foto di un annuncio, ordinate.
     */
    @GetMapping("/{id}/foto")
    public List<String> getFoto(@PathVariable Long id) {
        return fotoRepo.findByAnnuncio_IdAnnuncioOrderByOrdine(id)
                .stream()
                .map(Foto::getUrlFoto)
                .toList();
    }

    /**
     * POST /api/annunci
     * Crea un annuncio. Il pubblicante viene ricavato da X-User-Id (mai dal body).
     */
    @PostMapping
    public ResponseEntity<Annuncio> crea(
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") Long idUtente) {

        UtenteRegistrato pubblicante = utenteRepo.findById(idUtente).orElse(null);
        if (pubblicante == null) return ResponseEntity.badRequest().build();

        Annuncio annuncio = new Annuncio();
        annuncio.setTitolo(body.get("titolo").toString());
        annuncio.setCategoria(body.get("categoria").toString());
        annuncio.setDescrizioneAnnuncio(
            body.getOrDefault("descrizione_annuncio", "").toString()
        );
        annuncio.setPrezzoStimato(
            new java.math.BigDecimal(body.get("prezzo_stimato").toString())
        );
        annuncio.setCondizioni(
            Annuncio.Condizioni.valueOf(body.get("condizioni").toString())
        );
        annuncio.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
        annuncio.setPubblicante(pubblicante);

        Annuncio salvato = annuncioRepo.save(annuncio);

        // Salva le foto se presenti nel body come array base64 (massimo 4 per annuncio)
        if (body.containsKey("foto")) {
            List<String> fotoBase64 = (List<String>) body.get("foto");
            int numFoto = Math.min(fotoBase64.size(), 4);
            for (int i = 0; i < numFoto; i++) {
                Foto foto = new Foto();
                foto.setUrlFoto(fotoBase64.get(i));
                foto.setOrdine(i);
                foto.setAnnuncio(salvato);
                fotoRepo.save(foto);
            }
        }

        return ResponseEntity.ok(salvato);
    }

    /**
     * PUT /api/annunci/{id}
     * Aggiornamento parziale — usato per chiudere/oscurare un annuncio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Annuncio> aggiorna(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        return annuncioRepo.findById(id).map(ann -> {
            if (body.containsKey("titolo"))
                ann.setTitolo(body.get("titolo").toString());
            if (body.containsKey("descrizione_annuncio"))
                ann.setDescrizioneAnnuncio(body.get("descrizione_annuncio").toString());
            if (body.containsKey("categoria"))
                ann.setCategoria(body.get("categoria").toString());
            if (body.containsKey("prezzo_stimato"))
                ann.setPrezzoStimato(new java.math.BigDecimal(body.get("prezzo_stimato").toString()));
            if (body.containsKey("condizioni"))
                ann.setCondizioni(Annuncio.Condizioni.valueOf(body.get("condizioni").toString()));
            if (body.containsKey("stato_annuncio"))
                ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.valueOf(body.get("stato_annuncio").toString()));
            if (body.containsKey("notifica_oscuramento_letta"))
                ann.setNotificaOscuramentoLetta(Boolean.parseBoolean(body.get("notifica_oscuramento_letta").toString()));
            return ResponseEntity.ok(annuncioRepo.save(ann));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/annunci/{id}
     * Elimina annuncio e tutte le sue foto (CASCADE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        if (!annuncioRepo.existsById(id)) return ResponseEntity.notFound().build();
        fotoRepo.deleteByAnnuncio_IdAnnuncio(id);
        annuncioRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}