package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.dto.response.*;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.QuartiereRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UtenteDaoImpl implements UtenteDao {

    private final UtenteRegistratoRepository utenteRepo;
    private final QuartiereRepository quartiereRepo;

    @Override
    public Optional<UtenteSessioneResponse> findSessioneById(Long id) {
        return utenteRepo.findById(id).map(this::toSessioneResponse);
    }

    @Override
    public Optional<UtenteProfiloResponse> findProfiloById(Long id) {
        return utenteRepo.findById(id).map(this::toProfiloResponse);
    }

    @Override
    public Optional<UtentePublicoResponse> findPublicoById(Long id, long scambiCompletati) {
        return utenteRepo.findById(id).map(u -> toPublicoResponse(u, scambiCompletati));
    }

    @Override
    public List<LeaderboardItemResponse> findLeaderboard() {
        return utenteRepo.findLeaderboard().stream()
                .map(this::toLeaderboardItem)
                .toList();
    }

    @Override
    public Optional<UtenteRegistrato> findEntityByEmail(String email) {
        return utenteRepo.findByEmail(email);
    }

    @Override
    public Optional<UtenteRegistrato> findEntityById(Long id) {
        return utenteRepo.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return utenteRepo.existsByEmail(email);
    }

    @Override
    public UtenteSessioneResponse crea(RegistrazioneRequest req) {
        quartiereRepo.findById(req.getIdQuartiere()).orElseThrow(
                () -> new IllegalArgumentException("Quartiere non trovato"));

        UtenteRegistrato u = new UtenteRegistrato();
        u.setNomeCompleto(req.getNomeCompleto());
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword());
        u.setIndirizzo(req.getIndirizzo());
        u.setQuartiere(quartiereRepo.findById(req.getIdQuartiere()).orElseThrow());
        return toSessioneResponse(utenteRepo.save(u));
    }

    @Override
    public UtenteSessioneResponse aggiorna(Long id, AggiornaUtenteRequest req) {
        UtenteRegistrato u = utenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (req.getNomeCompleto() != null) u.setNomeCompleto(req.getNomeCompleto());
        if (req.getIndirizzo() != null)    u.setIndirizzo(req.getIndirizzo());
        if (req.getPassword() != null)     u.setPassword(req.getPassword());
        if (req.getFotoProfilo() != null)  u.setFotoProfilo(req.getFotoProfilo().isEmpty() ? null : req.getFotoProfilo());
        if (req.getQuartiere() != null && req.getQuartiere().getIdQuartiere() != null) {
            quartiereRepo.findById(req.getQuartiere().getIdQuartiere()).ifPresent(u::setQuartiere);
        }

        return toSessioneResponse(utenteRepo.save(u));
    }

    @Override
    public void updateCo2AndPunteggio(Long id, BigDecimal co2DaAggiungere, int puntiDaAggiungere) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setCo2Totale(u.getCo2Totale().add(co2DaAggiungere));
            u.setPunteggio(u.getPunteggio() + puntiDaAggiungere);
            utenteRepo.save(u);
        });
    }

    @Override
    public void updateUltimaVisitaProposte(Long id) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setUltimaVisitaProposte(LocalDateTime.now());
            utenteRepo.save(u);
        });
    }

    @Override
    public void updateUltimaVisitaChat(Long id) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setUltimaVisitaChat(LocalDateTime.now());
            utenteRepo.save(u);
        });
    }

    @Override
    public LocalDateTime getUltimaVisitaChat(Long id) {
        return utenteRepo.findById(id).map(UtenteRegistrato::getUltimaVisitaChat).orElse(null);
    }

    @Override
    public LocalDateTime getUltimaVisitaProposte(Long id) {
        return utenteRepo.findById(id).map(UtenteRegistrato::getUltimaVisitaProposte).orElse(null);
    }

    @Override
    public List<UtenteRegistrato> findByQuartiere(Long idQuartiere) {
        return utenteRepo.findByQuartiere_IdQuartiere(idQuartiere);
    }

    @Override
    public long count() {
        return utenteRepo.count();
    }

    @Override
    public List<UtenteRegistrato> findAll() {
        return utenteRepo.findAll();
    }

    // --- mapping privati ---

    private QuartiereResponse toQuartiereResponse(it.unife.sample.backend.model.Quartiere q) {
        return new QuartiereResponse(q.getIdQuartiere(), q.getNomeQuartiere(), q.getCitta());
    }

    private UtenteSessioneResponse toSessioneResponse(UtenteRegistrato u) {
        return new UtenteSessioneResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getFotoProfilo(),
                u.getIndirizzo(),
                toQuartiereResponse(u.getQuartiere())
        );
    }

    private UtenteProfiloResponse toProfiloResponse(UtenteRegistrato u) {
        return new UtenteProfiloResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getPunteggio(),
                u.getCo2Totale(),
                toQuartiereResponse(u.getQuartiere())
        );
    }

    private UtentePublicoResponse toPublicoResponse(UtenteRegistrato u, long scambiCompletati) {
        return new UtentePublicoResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getIndirizzo(),
                u.getPunteggio(),
                u.getCo2Totale(),
                u.getFotoProfilo(),
                toQuartiereResponse(u.getQuartiere()),
                scambiCompletati
        );
    }

    private LeaderboardItemResponse toLeaderboardItem(UtenteRegistrato u) {
        return new LeaderboardItemResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getFotoProfilo(),
                u.getPunteggio(),
                u.getCo2Totale()
        );
    }
}
