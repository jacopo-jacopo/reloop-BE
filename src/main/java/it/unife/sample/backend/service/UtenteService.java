package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.AnnuncioDao;
import it.unife.sample.backend.dao.BadgeDao;
import it.unife.sample.backend.dao.ChatDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.response.*;
import it.unife.sample.backend.model.Notifica;
import it.unife.sample.backend.model.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// service per la gestione degli utenti: fornisce metodi per ottenere informazioni sugli utenti, aggiornare il profilo dell'utente loggato,
// ottenere la classifica degli utenti, ottenere i badge ottenuti dall'utente loggato
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class UtenteService {

    private final UtenteDao utenteDao;
    private final BadgeDao badgeDao;
    private final AnnuncioDao annuncioDao;
    private final ChatDao chatDao;
    private final NotificaService notificaService;

    // metodo per ottenere le informazioni del profilo dell'utente loggato: chiama il metodo findProfiloById di UtenteDao
    public UtenteProfiloResponse getMe(Long idUtente) {
        return utenteDao.findProfiloById(idUtente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // metodo per ottenere le informazioni pubbliche di un utente: chiama il metodo findPublicoById di UtenteDao
    public UtentePublicoResponse getById(Long id) {
        long scambi = chatDao.countCompletateByUtente(id);
        return utenteDao.findPublicoById(id, scambi)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // metodo per ottenere la classifica degli utenti: chiama il metodo findLeaderboard di UtenteDao
    public List<LeaderboardItemResponse> getLeaderboard() {
        return utenteDao.findLeaderboard();
    }

    // metodo per ottenere i badge ottenuti dall'utente loggato: chiama il metodo findByUtente di BadgeDao
    public List<BadgeOttenutoResponse> getMieiBadge(Long idUtente) {
        return badgeDao.findByUtente(idUtente);
    }

    // metodo per ottenere tutti i badge disponibili: chiama il metodo findAll di BadgeDao
    public List<BadgeResponse> getTuttiBadge() {
        return badgeDao.findAll();
    }

    // metodo per ottenere gli annunci pubblicati dall'utente loggato: chiama il metodo findByPubblicante di AnnuncioDao
    public List<AnnuncioResponse> getMieiAnnunci(Long idUtente) {
        return annuncioDao.findByPubblicante(idUtente);
    }

    // metodo per aggiornare il profilo dell'utente loggato: chiama il metodo aggiorna di UtenteDao
    public UtenteSessioneResponse aggiorna(Long idUtente, AggiornaUtenteRequest req) {
        return utenteDao.aggiorna(idUtente, req);
    }

    // metodo per aggiornare la data e l'ora dell'ultima visita alle proposte dell'utente loggato:
    // chiama il metodo updateUltimaVisitaProposte di UtenteDao
    public void visitaProposte(Long idUtente) {
        utenteDao.updateUltimaVisitaProposte(idUtente);
    }

    // metodo per aggiornare la data e l'ora dell'ultima visita alla chat dell'utente loggato:
    // chiama il metodo updateUltimaVisitaChat di UtenteDao
    public void visitaChat(Long idUtente) {
        utenteDao.updateUltimaVisitaChat(idUtente);
    }

    // metodo  per ottenere la lista di tutti gli amministratori: chiama il metodo findAllAdmin di UtenteDao 
    // e mappa le entità in oggetti UtenteAdminResponse
    public List<UtenteAdminResponse> getAllAdmin() {
        return utenteDao.findAllAdmin().stream()
                .map(u -> new UtenteAdminResponse(
                        u.getIdUtenteReg(),
                        u.getNomeCompleto(),
                        u.getEmail(),
                        u.isBloccato(),
                        u.getPunteggio(),
                        new QuartiereResponse(
                                u.getQuartiere().getIdQuartiere(),
                                u.getQuartiere().getNomeQuartiere(),
                                u.getQuartiere().getCitta()
                        ),
                        u.getFotoProfilo()
                ))
                .toList();
    }

    // metodo per bloccare o sbloccare un utente: chiama il metodo blocca di UtenteDao e crea una notifica per l'utente
    // con il tipo di notifica ACCOUNT_BLOCCATO e il testo appropriato
    public UtenteAdminResponse blocca(Long id, boolean bloccato) {
        UtenteRegistrato u = utenteDao.blocca(id, bloccato);
        if (bloccato) {
            notificaService.crea(id, Notifica.TipoNotifica.ACCOUNT_BLOCCATO,
                    "Il tuo account è stato bloccato da un amministratore.");
        } else {
            notificaService.crea(id, Notifica.TipoNotifica.ACCOUNT_BLOCCATO,
                    "Il tuo account è stato sbloccato da un amministratore.");
        }
        return new UtenteAdminResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getEmail(),
                u.isBloccato(),
                u.getPunteggio(),
                new QuartiereResponse(
                        u.getQuartiere().getIdQuartiere(),
                        u.getQuartiere().getNomeQuartiere(),
                        u.getQuartiere().getCitta()
                ),
                u.getFotoProfilo()
        );
    }
}
