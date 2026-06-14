package it.unife.sample.backend.controller;

import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.repository.QuartiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per i quartieri.
 * Espone un solo endpoint pubblico usato dal form di registrazione
 * e dal modal modifica profilo per popolare i menu a cascata città → quartiere.
 */
@RestController
@RequestMapping("/api/quartieri")
@RequiredArgsConstructor
public class QuartiereController {

    // Repository per accedere alla tabella quartiere
    private final QuartiereRepository quartiereRepo;

    /**
     * GET /api/quartieri
     * Restituisce tutti i quartieri disponibili nel database.
     * Non richiede autenticazione — è usato anche nella pagina di login/registrazione.
     * Il frontend raggruppa i quartieri per città per costruire il menu a cascata.
     *
     * @return Lista completa di quartieri con id_quartiere, nome_quartiere, citta
     */
    @GetMapping
    public List<Quartiere> getAll() {
        return quartiereRepo.findAll();
    }
}