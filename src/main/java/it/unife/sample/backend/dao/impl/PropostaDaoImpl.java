package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.PropostaDao;
import it.unife.sample.backend.dto.request.InviaPropostaRequest;
import it.unife.sample.backend.dto.response.PropostaResponse;
import it.unife.sample.backend.model.Annuncio;
import it.unife.sample.backend.model.AnnuncioIncluso;
import it.unife.sample.backend.model.Proposta;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.AnnuncioInclusoRepository;
import it.unife.sample.backend.repository.AnnuncioRepository;
import it.unife.sample.backend.repository.PropostaRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// implementazione dell'interfaccia PropostaDao: fornisce i metodi per l'accesso ai dati delle proposte nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, i repository)
public class PropostaDaoImpl implements PropostaDao {

    private final PropostaRepository propostaRepo;
    private final AnnuncioInclusoRepository annuncioInclusoRepo;
    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;

    // trova tutte le proposte ricevute da un utente specifico
    @Override
    public List<PropostaResponse> findRicevute(Long idUtente) {
        return propostaRepo.findByAnnuncioInteresse_Pubblicante_IdUtenteRegOrderByTimestampPropostaDesc(idUtente)
                .stream().map(this::toResponse).toList();
    }

    // trova tutte le proposte inviate da un utente specifico
    @Override
    public List<PropostaResponse> findInviate(Long idUtente) {
        return propostaRepo.findByProponente_IdUtenteRegOrderByTimestampPropostaDesc(idUtente)
                .stream().map(this::toResponse).toList();
    }

    // restituisce il numero di proposte ricevute da un utente specifico che sono state create dopo l'ultima visita dell'utente
    @Override
    public long countNuoveRicevute(Long idUtente, java.time.LocalDateTime ultimaVisita) {
        return propostaRepo.countNuoveProposteRicevute(idUtente, ultimaVisita);
    }

    // restituisce una proposta specifica in base al suo ID, se esiste
    @Override
    public Optional<PropostaResponse> findById(Long id) {
        return propostaRepo.findById(id).map(this::toResponse);
    }

    // crea una proposta e la salva nel database
    @Override
    public PropostaResponse crea(InviaPropostaRequest req, Long idProponente) {
        Annuncio interesse = annuncioRepo.findById(req.getIdAnnuncioInteresse())
                .orElseThrow(() -> new IllegalArgumentException("Annuncio di interesse non trovato"));
        UtenteRegistrato proponente = utenteRepo.findById(idProponente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Proposta proposta = new Proposta();
        proposta.setAnnuncioInteresse(interesse);
        proposta.setProponente(proponente);
        proposta.setStatoProposta(Proposta.StatoProposta.in_attesa);
        Proposta salvata = propostaRepo.save(proposta);

        for (Long idAnn : req.getIdAnnunciOfferti()) {
            Annuncio offerto = annuncioRepo.findById(idAnn)
                    .orElseThrow(() -> new IllegalArgumentException("Annuncio offerto non trovato: " + idAnn));
            AnnuncioIncluso.AnnuncioInclusoId inclId = new AnnuncioIncluso.AnnuncioInclusoId();
            inclId.setIdProposta(salvata.getIdProposta());
            inclId.setIdAnnuncioOfferto(idAnn);

            AnnuncioIncluso ai = new AnnuncioIncluso();
            ai.setId(inclId);
            ai.setProposta(salvata);
            ai.setAnnuncioOfferto(offerto);
            ai.setFlagSelezionato(false);
            annuncioInclusoRepo.save(ai);
        }

        return toResponse(propostaRepo.findById(salvata.getIdProposta()).orElseThrow());
    }

    // aggiorna lo stato di una proposta specifica
    @Override
    public PropostaResponse updateStato(Long idProposta, Proposta.StatoProposta stato) {
        Proposta p = propostaRepo.findById(idProposta)
                .orElseThrow(() -> new IllegalArgumentException("Proposta non trovata"));
        p.setStatoProposta(stato);
        return toResponse(propostaRepo.save(p));
    }

    // aggiorna lo stato di una proposta specifica e seleziona un annuncio offerto come scelto
    @Override
    public void accettaConAnnuncioScelto(Long idProposta, Long idAnnuncioScelto) {
        annuncioInclusoRepo.findById_IdProposta(idProposta).forEach(ai -> {
            ai.setFlagSelezionato(ai.getId().getIdAnnuncioOfferto().equals(idAnnuncioScelto));
            annuncioInclusoRepo.save(ai);
        });
    }

    // rifiuta tutte le proposte in attesa che coinvolgono due annunci specifici, escludendo una proposta specifica
    @Override
    public void rifiutaProposteInAttesaPerAnnunci(Long idAnnuncio1, Long idAnnuncio2, Long idPropostaEsclusa) {
        // proposte dove annuncioInteresse = annuncio1 o annuncio2 (esclusa quella accettata)
        Stream.concat(
            propostaRepo.findByAnnuncioInteresse_IdAnnuncioAndStatoPropostaAndIdPropostaNot(
                    idAnnuncio1, Proposta.StatoProposta.in_attesa, idPropostaEsclusa).stream(),
            propostaRepo.findByAnnuncioInteresse_IdAnnuncioAndStatoPropostaAndIdPropostaNot(
                    idAnnuncio2, Proposta.StatoProposta.in_attesa, idPropostaEsclusa).stream()
        ).distinct().forEach(p -> {
            p.setStatoProposta(Proposta.StatoProposta.rifiutata);
            propostaRepo.save(p);
        });

        // proposte dove annuncioOfferto = annuncio1 o annuncio2 (esclusa quella accettata)
        Stream.concat(
            propostaRepo.findInAttesaByAnnuncioOfferto(idAnnuncio1, idPropostaEsclusa).stream(),
            propostaRepo.findInAttesaByAnnuncioOfferto(idAnnuncio2, idPropostaEsclusa).stream()
        ).distinct().forEach(p -> {
            p.setStatoProposta(Proposta.StatoProposta.rifiutata);
            propostaRepo.save(p);
        });
    }

    // rifiuta tutte le proposte in attesa che coinvolgono un annuncio specifico
    @Override
    public void rifiutaProposteInAttesaPerAnnuncio(Long idAnnuncio) {
        propostaRepo.findByAnnuncioInteresse_IdAnnuncioAndStatoProposta(
                idAnnuncio, Proposta.StatoProposta.in_attesa)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });

        propostaRepo.findByAnnuncioOffertoAndStatoProposta(idAnnuncio, Proposta.StatoProposta.in_attesa)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });
    }



    // metodi di mappatura tra entità e DTO di risposta

    PropostaResponse toResponse(Proposta p) {
        List<PropostaResponse.AnnuncioInclusoSummary> offerti = p.getAnnunciOfferti() == null ? List.of() :
                p.getAnnunciOfferti().stream().map(this::toAnnuncioInclusoSummary).toList();
        return new PropostaResponse(
                p.getIdProposta(),
                p.getStatoProposta().name(),
                p.getTimestampProposta(),
                toUtenteSummary(p.getProponente()),
                toAnnuncioInteresseSummary(p.getAnnuncioInteresse()),
                offerti
        );
    }

    private PropostaResponse.UtentePropostaSummary toUtenteSummary(UtenteRegistrato u) {
        return new PropostaResponse.UtentePropostaSummary(
                u.getIdUtenteReg(), u.getNomeCompleto(), u.getFotoProfilo());
    }

    private PropostaResponse.AnnuncioInteresseSummary toAnnuncioInteresseSummary(Annuncio a) {
        return new PropostaResponse.AnnuncioInteresseSummary(
                a.getIdAnnuncio(), a.getTitolo(), a.getPrezzoStimato(),
                toUtenteSummary(a.getPubblicante()));
    }

    private PropostaResponse.AnnuncioInclusoSummary toAnnuncioInclusoSummary(AnnuncioIncluso ai) {
        PropostaResponse.AnnuncioInclusoIdDto idDto = new PropostaResponse.AnnuncioInclusoIdDto(
                ai.getId().getIdProposta(), ai.getId().getIdAnnuncioOfferto());
        PropostaResponse.AnnuncioOffertoSummary offerto = new PropostaResponse.AnnuncioOffertoSummary(
                ai.getAnnuncioOfferto().getIdAnnuncio(),
                ai.getAnnuncioOfferto().getTitolo(),
                ai.getAnnuncioOfferto().getPrezzoStimato());
        return new PropostaResponse.AnnuncioInclusoSummary(idDto, ai.getFlagSelezionato(), offerto);
    }
}
