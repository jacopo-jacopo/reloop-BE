package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per le recensioni tra utenti.
 * Le recensioni vengono lasciate dopo il completamento di uno scambio.
 * La chiave primaria è composita (id_recensore, id_recensito) —
 * una nuova recensione tra la stessa coppia di utenti sovrascrive quella precedente.
 */
@RestController
@RequestMapping("/api/recensioni")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RecensioneController {

    // Repository per recensioni, utenti, chat e messaggi
    private final RecensioneRepository recensioneRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final ChatRepository chatRepo;
    private final MessaggioRepository messaggioRepo;

    // Suffisso del messaggio di sistema inviato in chat quando si lascia una recensione
    public static final String RECENSIONE_SUFFIX = "ha lasciato una recensione";

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
     * Se la coppia (recensore, recensito) ha già una recensione (es. da un
     * precedente scambio), questa viene sovrascritta con i nuovi dati.
     * Se viene fornito id_chat, aggiunge anche un messaggio di sistema nella
     * chat per avvisare l'altro utente.
     *
     * @param body       Map con id_utente_reg_recensito, voto (1-5), descrizione_recensione, id_chat (opzionale)
     * @param idRecensore  ID dell'utente che lascia la recensione dall'header
     * @return 200 con la recensione salvata,
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

        // Verifica esistenza di entrambi gli utenti
        UtenteRegistrato recensore = utenteRepo.findById(idRecensore).orElse(null);
        UtenteRegistrato recensito = utenteRepo.findById(idRecensito).orElse(null);
        if (recensore == null || recensito == null)
            return ResponseEntity.badRequest().body("Utente non trovato");

        // Costruisce la chiave composita della recensione
        Recensione.RecensioneId recId = new Recensione.RecensioneId();
        recId.setIdUtenteRegRecensore(idRecensore);
        recId.setIdUtenteRegRecensito(idRecensito);

        // Recupera la recensione esistente per questa coppia (se presente) e la sovrascrive
        Recensione r = recensioneRepo.findById(recId).orElse(new Recensione());
        r.setId(recId);
        r.setRecensore(recensore);
        r.setRecensito(recensito);
        r.setVoto(voto);
        r.setDescrizioneRecensione(descrizione);
        r.setDataRecensione(LocalDate.now());

        Recensione salvata = recensioneRepo.save(r);

        // Messaggio di sistema nella chat per avvisare l'altro utente
        Object idChatObj = body.get("id_chat");
        if (idChatObj != null) {
            Long idChat = Long.valueOf(idChatObj.toString());
            chatRepo.findById(idChat).ifPresent(chat -> {
                Long maxId = messaggioRepo.findMaxIdByIdChat(idChat);
                Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
                msgId.setIdMessaggio(maxId + 1);
                msgId.setIdChat(idChat);

                Messaggio msg = new Messaggio();
                msg.setId(msgId);
                msg.setChat(chat);
                msg.setContenuto(recensore.getNomeCompleto() + " " + RECENSIONE_SUFFIX);
                msg.setMittente(recensore);
                messaggioRepo.save(msg);
            });
        }

        return ResponseEntity.ok(salvata);
    }
}