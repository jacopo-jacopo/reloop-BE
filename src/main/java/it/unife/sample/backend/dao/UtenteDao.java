package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.dto.response.LeaderboardItemResponse;
import it.unife.sample.backend.dto.response.UtenteProfiloResponse;
import it.unife.sample.backend.dto.response.UtentePublicoResponse;
import it.unife.sample.backend.dto.response.UtenteSessioneResponse;
import it.unife.sample.backend.model.UtenteRegistrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UtenteDao {

    Optional<UtenteSessioneResponse> findSessioneById(Long id);
    Optional<UtenteProfiloResponse> findProfiloById(Long id);
    Optional<UtentePublicoResponse> findPublicoById(Long id, long scambiCompletati);
    List<LeaderboardItemResponse> findLeaderboard();
    Optional<UtenteRegistrato> findEntityByEmail(String email);
    Optional<UtenteRegistrato> findEntityById(Long id);
    boolean existsByEmail(String email);
    UtenteSessioneResponse crea(RegistrazioneRequest req);
    UtenteSessioneResponse aggiorna(Long id, AggiornaUtenteRequest req);
    void updateCo2AndPunteggio(Long id, BigDecimal co2DaAggiungere, int puntiDaAggiungere);
    void updateUltimaVisitaProposte(Long id);
    void updateUltimaVisitaChat(Long id);
    LocalDateTime getUltimaVisitaChat(Long id);
    LocalDateTime getUltimaVisitaProposte(Long id);
    List<UtenteRegistrato> findByQuartiere(Long idQuartiere);
    long count();
    List<UtenteRegistrato> findAll();
}
