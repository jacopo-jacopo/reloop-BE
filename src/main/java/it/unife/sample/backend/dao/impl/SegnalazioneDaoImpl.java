package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.SegnalazioneDao;
import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;
import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SegnalazioneDaoImpl implements SegnalazioneDao {

    private final SegnalazioneRepository segnalazioneRepo;
    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AmministratoreRepository adminRepo;
    private final EliminaRepository eliminaRepo;
    private final PropostaRepository propostaRepo;
    private final ChatRepository chatRepo;
    private final MessaggioRepository messaggioRepo;

    private static final String OSCURAMENTO_SUFFIX =
            "è stato rimosso da un amministratore e non è più disponibile.";

    @Override
    public List<SegnalazioneResponse> findAll() {
        return segnalazioneRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<SegnalazioneResponse> findByUtente(Long idUtente) {
        return segnalazioneRepo.findBySegnalante_IdUtenteReg(idUtente).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public SegnalazioneResponse crea(InviaSegnalazioneRequest req, Long idUtente) {
        Annuncio annuncio = annuncioRepo.findById(req.getIdAnnuncioSegnalato())
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));
        UtenteRegistrato segnalante = utenteRepo.findById(idUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Segnalazione s = new Segnalazione();
        s.setAnnuncioSegnalato(annuncio);
        s.setSegnalante(segnalante);
        s.setMotivazione(req.getMotivazione());
        s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.in_attesa);

        return toResponse(segnalazioneRepo.save(s));
    }

    @Override
    public SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin) {
        Segnalazione s = segnalazioneRepo.findById(idSegnalazione)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata"));
        s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.presa_in_carico);
        adminRepo.findById(idAdmin).ifPresent(s::setAmministratore);
        return toResponse(segnalazioneRepo.save(s));
    }

    @Override
    public SegnalazioneResponse chiudi(Long idSegnalazione, ChiudiSegnalazioneRequest req, Long idAdmin) {
        Segnalazione s = segnalazioneRepo.findById(idSegnalazione)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata"));
        s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.chiusa);

        if (req.isOscuraAnnuncio()) {
            Annuncio annuncio = s.getAnnuncioSegnalato();
            annuncio.setStatoAnnuncio(Annuncio.StatoAnnuncio.oscurato);
            annuncioRepo.save(annuncio);

            adminRepo.findById(idAdmin).ifPresent(admin -> {
                Elimina.EliminaId eliminaId = new Elimina.EliminaId();
                eliminaId.setIdUtenteAdm(admin.getIdUtenteAdm());
                eliminaId.setIdAnnuncioEliminato(annuncio.getIdAnnuncio());

                Elimina elimina = new Elimina();
                elimina.setId(eliminaId);
                elimina.setAmministratore(admin);
                elimina.setAnnuncioEliminato(annuncio);
                eliminaRepo.save(elimina);
            });

            gestisciOscuramento(annuncio);
        }

        return toResponse(segnalazioneRepo.save(s));
    }

    @Override
    public boolean existsSegnalazioneAperta(Long idUtente, Long idAnnuncio) {
        return segnalazioneRepo
                .existsBySegnalante_IdUtenteRegAndAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(
                        idUtente, idAnnuncio, Segnalazione.StatoSegnalazione.chiusa);
    }

    private void gestisciOscuramento(Annuncio annuncio) {
        Long idAnnuncio = annuncio.getIdAnnuncio();

        propostaRepo.findByAnnuncioInteresse_IdAnnuncioAndStatoProposta(idAnnuncio, Proposta.StatoProposta.in_attesa)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });
        propostaRepo.findByAnnuncioOffertoAndStatoProposta(idAnnuncio, Proposta.StatoProposta.in_attesa)
                .forEach(p -> { p.setStatoProposta(Proposta.StatoProposta.rifiutata); propostaRepo.save(p); });

        for (Chat chat : chatRepo.findAperteByAnnuncio(idAnnuncio)) {
            chat.setStatoChat(Chat.StatoChat.annullata);
            chatRepo.save(chat);

            Proposta proposta = chat.getPropostaGenerante();
            Annuncio annuncioInteresse = proposta.getAnnuncioInteresse();

            if (!annuncioInteresse.getIdAnnuncio().equals(idAnnuncio)
                    && annuncioInteresse.getStatoAnnuncio() == Annuncio.StatoAnnuncio.sospeso) {
                annuncioInteresse.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
                annuncioRepo.save(annuncioInteresse);
            }

            proposta.getAnnunciOfferti().stream()
                    .filter(AnnuncioIncluso::getFlagSelezionato)
                    .map(AnnuncioIncluso::getAnnuncioOfferto)
                    .filter(ann -> !ann.getIdAnnuncio().equals(idAnnuncio))
                    .filter(ann -> ann.getStatoAnnuncio() == Annuncio.StatoAnnuncio.sospeso)
                    .forEach(ann -> {
                        ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
                        annuncioRepo.save(ann);
                    });

            Long maxId = messaggioRepo.findMaxIdByIdChat(chat.getIdChat());
            Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
            msgId.setIdMessaggio(maxId + 1);
            msgId.setIdChat(chat.getIdChat());

            Messaggio msg = new Messaggio();
            msg.setId(msgId);
            msg.setChat(chat);
            msg.setContenuto("L'annuncio '" + annuncio.getTitolo() + "' " + OSCURAMENTO_SUFFIX);
            msg.setMittente(annuncioInteresse.getPubblicante());
            messaggioRepo.save(msg);
        }
    }

    // --- mapping ---

    private SegnalazioneResponse toResponse(Segnalazione s) {
        Annuncio ann = s.getAnnuncioSegnalato();
        SegnalazioneResponse.AutoreSummary autore = new SegnalazioneResponse.AutoreSummary(
                ann.getPubblicante().getIdUtenteReg(), ann.getPubblicante().getNomeCompleto());
        SegnalazioneResponse.AnnuncioSegnalatoSummary annSummary = new SegnalazioneResponse.AnnuncioSegnalatoSummary(
                ann.getIdAnnuncio(), ann.getTitolo(), ann.getCategoria(),
                ann.getCondizioni().name(), ann.getPrezzoStimato(), ann.getDescrizioneAnnuncio(), autore);

        SegnalazioneResponse.AmministratoreSummary adminSummary = s.getAmministratore() == null ? null :
                new SegnalazioneResponse.AmministratoreSummary(
                        s.getAmministratore().getIdUtenteAdm(), s.getAmministratore().getNomeCompleto());

        return new SegnalazioneResponse(
                s.getIdSegnalazione(),
                s.getMotivazione(),
                s.getStatoSegnalazione().name(),
                s.getTimestampSegnalazione(),
                annSummary,
                adminSummary
        );
    }
}
