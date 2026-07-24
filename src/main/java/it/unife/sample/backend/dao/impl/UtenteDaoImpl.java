package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.AggiornaUtenteRequest;
import it.unife.sample.backend.dto.RegistrazioneRequest;
import it.unife.sample.backend.dto.response.*;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.repository.QuartiereRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// DAO per la gestione degli utenti registrati: implementa le operazioni di accesso ai dati degli utenti nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, utenteRepo e quartiereRepo)
public class UtenteDaoImpl implements UtenteDao {

    private final UtenteRegistratoRepository utenteRepo; // repository per l'accesso ai dati degli utenti registrati nel database
    private final QuartiereRepository quartiereRepo;     // repository per l'accesso ai dati dei quartieri nel database
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // encoder per la cifratura delle password degli utenti

    // trova la sessione dell'utente registrato con l'id specificato, se esiste
    @Override
    public Optional<UtenteSessioneResponse> findSessioneById(Long id) {
        return utenteRepo.findById(id).map(this::toSessioneResponse); // mappa l'entità UtenteRegistrato alla DTO UtenteSessioneResponse
    }

    // trova il profilo dell'utente registrato con l'id specificato, se esiste
    @Override
    public Optional<UtenteProfiloResponse> findProfiloById(Long id) {
        return utenteRepo.findById(id).map(this::toProfiloResponse); // mappa l'entità UtenteRegistrato alla DTO UtenteProfiloResponse
    }

    // trova il profilo pubblico dell'utente registrato con l'id specificato, se esiste, e restituisce anche il numero di scambi completati
    @Override
    public Optional<UtentePublicoResponse> findPublicoById(Long id, long scambiCompletati) {
        return utenteRepo.findById(id).map(u -> toPublicoResponse(u, scambiCompletati)); // mappa l'entità UtenteRegistrato alla DTO UtentePublicoResponse
    }

    // trova il profilo pubblico dell'utente registrato con l'id specificato, se esiste, e restituisce anche il numero di scambi completati
    @Override
    public List<LeaderboardItemResponse> findLeaderboard(Long idQuartiere) {
        return utenteRepo.findLeaderboard(idQuartiere).stream()
                .map(this::toLeaderboardItem) // mappa l'entità UtenteRegistrato alla DTO LeaderboardItemResponse
                .toList();
    }

    // trova la sessione dell'utente registrato con l'email specificata, se esiste
    @Override
    public Optional<UtenteRegistrato> findEntityByEmail(String email) {
        return utenteRepo.findByEmail(email);
    }

    // trova l'entità dell'utente registrato con l'id specificato, se esiste
    @Override
    public Optional<UtenteRegistrato> findEntityById(Long id) {
        return utenteRepo.findById(id);
    }

    // verifica se esiste un utente registrato con l'email specificata
    @Override
    public boolean existsByEmail(String email) {
        return utenteRepo.existsByEmail(email);
    }

    // crea un nuovo utente registrato con i dati specificati nella richiesta di registrazione e restituisce la sessione dell'utente creato
    @Override
    public UtenteSessioneResponse crea(RegistrazioneRequest req) {
        quartiereRepo.findById(req.getIdQuartiere()).orElseThrow(
                () -> new IllegalArgumentException("Quartiere non trovato"));

        UtenteRegistrato u = new UtenteRegistrato();
        u.setNomeCompleto(req.getNomeCompleto());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setIndirizzo(req.getIndirizzo());
        u.setQuartiere(quartiereRepo.findById(req.getIdQuartiere()).orElseThrow());
        return toSessioneResponse(utenteRepo.save(u)); 
    }

    // aggiorna i dati dell'utente registrato con l'id specificato secondo i dati della richiesta di aggiornamento e 
    // restituisce la sessione dell'utente aggiornato
    @Override
    public UtenteSessioneResponse aggiorna(Long id, AggiornaUtenteRequest req) {
        UtenteRegistrato u = utenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (req.getNomeCompleto() != null) u.setNomeCompleto(req.getNomeCompleto());
        if (req.getIndirizzo() != null)    u.setIndirizzo(req.getIndirizzo());
        if (req.getPassword() != null)     u.setPassword(passwordEncoder.encode(req.getPassword()));
        if (req.getFotoProfilo() != null)  u.setFotoProfilo(req.getFotoProfilo().isEmpty() ? null : req.getFotoProfilo());
        if (req.getQuartiere() != null && req.getQuartiere().getIdQuartiere() != null) {
            quartiereRepo.findById(req.getQuartiere().getIdQuartiere()).ifPresent(u::setQuartiere);
        }

        return toSessioneResponse(utenteRepo.save(u));
    }

    // aggiorna il punteggio e la CO2 totale dell'utente registrato con l'id specificato aggiungendo i valori specificati
    @Override
    public void updateCo2AndPunteggio(Long id, BigDecimal co2DaAggiungere, int puntiDaAggiungere) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setCo2Totale(u.getCo2Totale().add(co2DaAggiungere));
            u.setPunteggio(u.getPunteggio() + puntiDaAggiungere);
            utenteRepo.save(u);
        });
    }

    // aggiorna la data e l'ora dell'ultima visita dell'utente registrato con l'id specificato alla sezione delle proposte
    @Override
    public void updateUltimaVisitaProposte(Long id) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setUltimaVisitaProposte(LocalDateTime.now());
            utenteRepo.save(u);
        });
    }

    // aggiorna la data e l'ora dell'ultima visita dell'utente registrato con l'id specificato alla chat
    @Override
    public void updateUltimaVisitaChat(Long id) {
        utenteRepo.findById(id).ifPresent(u -> {
            u.setUltimaVisitaChat(LocalDateTime.now());
            utenteRepo.save(u);
        });
    }

    // restituisce la data e l'ora dell'ultima visita dell'utente registrato con l'id specificato alla chat
    @Override
    public LocalDateTime getUltimaVisitaChat(Long id) {
        return utenteRepo.findById(id).map(UtenteRegistrato::getUltimaVisitaChat) // mappa UtenteRegistrato alla data e ora dell'ultima visita alla chat
                                      .orElse(null);
    }

    // restituisce la data e l'ora dell'ultima visita dell'utente registrato con l'id specificato alla sezione delle proposte
    @Override
    public LocalDateTime getUltimaVisitaProposte(Long id) {
        return utenteRepo.findById(id).map(UtenteRegistrato::getUltimaVisitaProposte) // mappa UtenteRegistrato alla data e ora dell'ultima visita alla sezione delle proposte
                                      .orElse(null);
    }

    // restituisce la lista di tutti gli utenti registrati che appartengono al quartiere con l'id specificato
    @Override
    public List<UtenteRegistrato> findByQuartiere(Long idQuartiere) {
        return utenteRepo.findByQuartiere_IdQuartiere(idQuartiere);
    }

    // restituisce il numero di utenti registrati presenti nel database
    @Override
    public long count() {
        return utenteRepo.count();
    }

    // restituisce una lista di tutti gli utenti registrati presenti nel database
    @Override
    public List<UtenteRegistrato> findAll() {
        return utenteRepo.findAll();
    }

    // trova tutti gli utenti per la dashboard admin
    @Override
    public List<UtenteRegistrato> findAllAdmin() {
        return utenteRepo.findAll();
    }

    // blocca o sblocca l'utente registrato con l'id specificato, a seconda del valore del parametro bloccato
    @Override
    public UtenteRegistrato blocca(Long id, boolean bloccato) {
        UtenteRegistrato u = utenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        u.setBloccato(bloccato);
        return utenteRepo.save(u);
    }


    /* 
        --- MAPPING PRIVATI ---
    */

    // mapping da entità Quartiere a DTO QuartiereResponse
    private QuartiereResponse toQuartiereResponse(Quartiere q) {
        return new QuartiereResponse(q.getIdQuartiere(), q.getNomeQuartiere(), q.getCitta());
    }

    // mapping da entità UtenteRegistrato a DTO UtenteSessioneResponse
    private UtenteSessioneResponse toSessioneResponse(UtenteRegistrato u) {
        return new UtenteSessioneResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getFotoProfilo(),
                u.getIndirizzo(),
                toQuartiereResponse(u.getQuartiere()), // mappa l'entità Quartiere alla DTO QuartiereResponse
                u.isBloccato()
        );
    }

    // mapping da entità UtenteRegistrato a DTO UtenteProfiloResponse
    private UtenteProfiloResponse toProfiloResponse(UtenteRegistrato u) {
        return new UtenteProfiloResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getPunteggio(),
                u.getCo2Totale(),
                toQuartiereResponse(u.getQuartiere()) // mappa l'entità Quartiere alla DTO QuartiereResponse
        );
    }

    // mapping da entità UtenteRegistrato a DTO UtentePublicoResponse
    private UtentePublicoResponse toPublicoResponse(UtenteRegistrato u, long scambiCompletati) {
        return new UtentePublicoResponse(
                u.getIdUtenteReg(),
                u.getNomeCompleto(),
                u.getIndirizzo(),
                u.getPunteggio(),
                u.getCo2Totale(),
                u.getFotoProfilo(),
                toQuartiereResponse(u.getQuartiere()), // mappa l'entità Quartiere alla DTO QuartiereResponse
                scambiCompletati
        );
    }

    // mapping da entità UtenteRegistrato a DTO LeaderboardItemResponse
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
