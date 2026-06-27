package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.AnnuncioDao;
import it.unife.sample.backend.dto.request.AggiornaAnnuncioRequest;
import it.unife.sample.backend.dto.request.CreaAnnuncioRequest;
import it.unife.sample.backend.dto.response.AnnuncioResponse;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.model.Annuncio;
import it.unife.sample.backend.model.Foto;
import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.AnnuncioRepository;
import it.unife.sample.backend.repository.FotoRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnnuncioDaoImpl implements AnnuncioDao {

    private final AnnuncioRepository annuncioRepo;
    private final FotoRepository fotoRepo;
    private final UtenteRegistratoRepository utenteRepo;

    @Override
    public List<AnnuncioResponse> findByQuartiere(Long idQuartiere, Long idUtenteEscluso) {
        return annuncioRepo.findByPubblicante_Quartiere_IdQuartiereAndStatoAnnuncioAndPubblicante_IdUtenteRegNot(
                idQuartiere, Annuncio.StatoAnnuncio.attivo, idUtenteEscluso)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<AnnuncioResponse> cercaPerTitolo(String cerca) {
        return annuncioRepo.findByTitoloContainingIgnoreCase(cerca).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public List<AnnuncioResponse> cercaPerCategoria(String categoria) {
        return annuncioRepo.findByCategoriaContainingIgnoreCase(categoria).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public List<AnnuncioResponse> findAll() {
        return annuncioRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<AnnuncioResponse> findById(Long id) {
        return annuncioRepo.findById(id).map(this::toResponse);
    }

    @Override
    public List<AnnuncioResponse> findByPubblicante(Long idUtente) {
        return annuncioRepo.findByPubblicante_IdUtenteReg(idUtente).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public List<String> findFotoById(Long idAnnuncio) {
        return fotoRepo.findByAnnuncio_IdAnnuncioOrderByOrdine(idAnnuncio).stream()
                .map(Foto::getUrlFoto).toList();
    }

    @Override
    public AnnuncioResponse crea(Long idUtente, CreaAnnuncioRequest req) {
        UtenteRegistrato pubblicante = utenteRepo.findById(idUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Annuncio annuncio = new Annuncio();
        annuncio.setTitolo(req.getTitolo());
        annuncio.setCategoria(req.getCategoria());
        annuncio.setDescrizioneAnnuncio(req.getDescrizioneAnnuncio() != null ? req.getDescrizioneAnnuncio() : "");
        annuncio.setPrezzoStimato(req.getPrezzoStimato());
        annuncio.setCondizioni(req.getCondizioni());
        annuncio.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
        annuncio.setPubblicante(pubblicante);

        Annuncio salvato = annuncioRepo.save(annuncio);

        if (req.getFoto() != null) {
            int numFoto = Math.min(req.getFoto().size(), 4);
            for (int i = 0; i < numFoto; i++) {
                Foto foto = new Foto();
                foto.setUrlFoto(req.getFoto().get(i));
                foto.setOrdine(i);
                foto.setAnnuncio(salvato);
                fotoRepo.save(foto);
            }
        }

        return toResponse(salvato);
    }

    @Override
    public AnnuncioResponse aggiorna(Long id, AggiornaAnnuncioRequest req) {
        Annuncio ann = annuncioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));

        if (req.getTitolo() != null)                   ann.setTitolo(req.getTitolo());
        if (req.getDescrizioneAnnuncio() != null)      ann.setDescrizioneAnnuncio(req.getDescrizioneAnnuncio());
        if (req.getCategoria() != null)                ann.setCategoria(req.getCategoria());
        if (req.getPrezzoStimato() != null)            ann.setPrezzoStimato(req.getPrezzoStimato());
        if (req.getCondizioni() != null)               ann.setCondizioni(req.getCondizioni());
        if (req.getStatoAnnuncio() != null)            ann.setStatoAnnuncio(req.getStatoAnnuncio());
        if (req.getNotificaOscuramentoLetta() != null) ann.setNotificaOscuramentoLetta(req.getNotificaOscuramentoLetta());

        return toResponse(annuncioRepo.save(ann));
    }

    @Override
    public void elimina(Long id) {
        fotoRepo.deleteByAnnuncio_IdAnnuncio(id);
        annuncioRepo.deleteById(id);
    }

    @Override
    public void updateStato(Long id, Annuncio.StatoAnnuncio stato) {
        annuncioRepo.findById(id).ifPresent(ann -> {
            ann.setStatoAnnuncio(stato);
            annuncioRepo.save(ann);
        });
    }

    @Override
    public boolean existsById(Long id) {
        return annuncioRepo.existsById(id);
    }

    // --- mapping privati ---

    AnnuncioResponse toResponse(Annuncio a) {
        return new AnnuncioResponse(
                a.getIdAnnuncio(),
                a.getTitolo(),
                a.getDescrizioneAnnuncio(),
                a.getCategoria(),
                a.getPrezzoStimato(),
                a.getCondizioni().name(),
                a.getStatoAnnuncio().name(),
                a.isNotificaOscuramentoLetta(),
                toPubblicanteSummary(a.getPubblicante())
        );
    }

    private AnnuncioResponse.PubblicanteSummary toPubblicanteSummary(UtenteRegistrato u) {
        return new AnnuncioResponse.PubblicanteSummary(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getFotoProfilo(),
                toQuartiereResponse(u.getQuartiere())
        );
    }

    private QuartiereResponse toQuartiereResponse(Quartiere q) {
        return new QuartiereResponse(q.getIdQuartiere(), q.getNomeQuartiere(), q.getCitta());
    }
}
