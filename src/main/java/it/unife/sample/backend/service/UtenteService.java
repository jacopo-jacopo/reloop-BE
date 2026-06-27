package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.AnnuncioDao;
import it.unife.sample.backend.dao.BadgeDao;
import it.unife.sample.backend.dao.ChatDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteDao utenteDao;
    private final BadgeDao badgeDao;
    private final AnnuncioDao annuncioDao;
    private final ChatDao chatDao;

    public UtenteProfiloResponse getMe(Long idUtente) {
        return utenteDao.findProfiloById(idUtente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public UtentePublicoResponse getById(Long id) {
        long scambi = chatDao.countCompletateByUtente(id);
        return utenteDao.findPublicoById(id, scambi)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<LeaderboardItemResponse> getLeaderboard() {
        return utenteDao.findLeaderboard();
    }

    public List<BadgeOttenutoResponse> getMieiBadge(Long idUtente) {
        return badgeDao.findByUtente(idUtente);
    }

    public List<BadgeResponse> getTuttiBadge() {
        return badgeDao.findAll();
    }

    public List<AnnuncioResponse> getMieiAnnunci(Long idUtente) {
        return annuncioDao.findByPubblicante(idUtente);
    }

    public UtenteSessioneResponse aggiorna(Long idUtente, AggiornaUtenteRequest req) {
        return utenteDao.aggiorna(idUtente, req);
    }

    public void visitaProposte(Long idUtente) {
        utenteDao.updateUltimaVisitaProposte(idUtente);
    }

    public void visitaChat(Long idUtente) {
        utenteDao.updateUltimaVisitaChat(idUtente);
    }
}
