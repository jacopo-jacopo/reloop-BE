package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.RecensioneDao;
import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;
import it.unife.sample.backend.model.Recensione;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.RecensioneRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecensioneDaoImpl implements RecensioneDao {

    private final RecensioneRepository recensioneRepo;
    private final UtenteRegistratoRepository utenteRepo;

    @Override
    public List<RecensioneResponse> findByRecensito(Long idUtente) {
        return recensioneRepo.findById_IdUtenteRegRecensito(idUtente).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public RecensioneResponse salva(InviaRecensioneRequest req, Long idRecensore) {
        UtenteRegistrato recensore = utenteRepo.findById(idRecensore)
                .orElseThrow(() -> new IllegalArgumentException("Utente recensore non trovato"));
        UtenteRegistrato recensito = utenteRepo.findById(req.getIdUtenteRegRecensito())
                .orElseThrow(() -> new IllegalArgumentException("Utente recensito non trovato"));

        Recensione.RecensioneId id = new Recensione.RecensioneId();
        id.setIdUtenteRegRecensore(idRecensore);
        id.setIdUtenteRegRecensito(req.getIdUtenteRegRecensito());

        Recensione r = new Recensione();
        r.setId(id);
        r.setRecensore(recensore);
        r.setRecensito(recensito);
        r.setVoto(req.getVoto());
        r.setDescrizioneRecensione(req.getDescrizioneRecensione());

        return toResponse(recensioneRepo.save(r));
    }

    // --- mapping ---

    private RecensioneResponse toResponse(Recensione r) {
        RecensioneResponse.RecensoreResponse recensore = new RecensioneResponse.RecensoreResponse(
                r.getRecensore().getIdUtenteReg(),
                r.getRecensore().getNomeCompleto(),
                r.getRecensore().getFotoProfilo());
        return new RecensioneResponse(recensore, r.getVoto(), r.getDescrizioneRecensione());
    }
}
