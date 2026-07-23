package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.SegnalazioneDao;
import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;
import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.*;
import it.unife.sample.backend.service.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// implementazione dell'interfaccia SegnalazioneDao: fornisce i metodi per l'accesso ai dati delle segnalazioni nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, i repository)
public class SegnalazioneDaoImpl implements SegnalazioneDao {

    private final SegnalazioneRepository segnalazioneRepo;
    private final AnnuncioRepository annuncioRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final AmministratoreRepository adminRepo;
    private final EliminaRepository eliminaRepo;
    private final PropostaRepository propostaRepo;
    private final ChatRepository chatRepo;
    private final MessaggioRepository messaggioRepo;
    private final NotificaService notificaService;

    private static final String OSCURAMENTO_SUFFIX = "è stato rimosso da un amministratore e non è più disponibile.";

    // trova tutte le segnalazioni presenti nel db
    @Override
    public List<SegnalazioneResponse> findAll() {
        return segnalazioneRepo.findAll().stream().map(this::toResponse).toList();
    }

    // trova tutte le segnalazioni aperte da un utente specifico
    @Override
    public List<SegnalazioneResponse> findByUtente(Long idUtente) {
        return segnalazioneRepo.findBySegnalante_IdUtenteReg(idUtente).stream()
                .map(this::toResponse).toList();
    }

    // crea una nuova segnalazione e la salva nel db
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

    // segna una segnalazione come presa in carico da un amministratore
    @Override
    public SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin) {
        Segnalazione s = segnalazioneRepo.findById(idSegnalazione)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata"));
        s.setStatoSegnalazione(Segnalazione.StatoSegnalazione.presa_in_carico);
        adminRepo.findById(idAdmin).ifPresent(s::setAmministratore);
        return toResponse(segnalazioneRepo.save(s));
    }

    // chiude una segnalazione e, se richiesto, oscura l'annuncio segnalato
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

            segnalazioneRepo
                .findByAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(annuncio.getIdAnnuncio(), Segnalazione.StatoSegnalazione.chiusa)
                .stream()
                .filter(altra -> !altra.getIdSegnalazione().equals(s.getIdSegnalazione()))
                .forEach(altra -> { altra.setStatoSegnalazione(Segnalazione.StatoSegnalazione.chiusa); segnalazioneRepo.save(altra); });
        }

        return toResponse(segnalazioneRepo.save(s));
    }

    // verifica se esiste una segnalazione non chiusa per un utente e un annuncio specifici
    @Override
    public boolean existsSegnalazioneAperta(Long idUtente, Long idAnnuncio) {
        return segnalazioneRepo
                .existsBySegnalante_IdUtenteRegAndAnnuncioSegnalato_IdAnnuncioAndStatoSegnalazioneNot(
                        idUtente, idAnnuncio, Segnalazione.StatoSegnalazione.chiusa);
    }

    // gestisce l'oscuramento di un annuncio: 
    // chiude le proposte in attesa, annulla le chat aperte e invia un messaggio di sistema agli utenti coinvolti
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
                    .findFirst()
                    .ifPresent(ann -> {
                        if (!ann.getIdAnnuncio().equals(idAnnuncio) && ann.getStatoAnnuncio() == Annuncio.StatoAnnuncio.sospeso) {
                            ann.setStatoAnnuncio(Annuncio.StatoAnnuncio.attivo);
                            annuncioRepo.save(ann);
                        }
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

            Long idProponente = proposta.getProponente().getIdUtenteReg();
            notificaService.crea(idProponente, Notifica.TipoNotifica.SCAMBIO_ANNULLATO,
                    "Lo scambio relativo all'annuncio \"" + annuncio.getTitolo() + "\" è stato annullato perché l'annuncio è stato rimosso.");
        }
    }



    // mapping da oggetto Segnalazione a oggetto SegnalazioneResponse
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
