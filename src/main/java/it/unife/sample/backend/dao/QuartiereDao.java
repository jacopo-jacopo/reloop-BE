package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.model.Quartiere;

import java.util.List;
import java.util.Optional;

// interfaccia DAO per la gestione dei quartieri: definisce i metodi per ottenere tutti i quartieri,
// trovare un quartiere per ID, creare un nuovo quartiere e aggiornare un quartiere esistente
public interface QuartiereDao {

    List<QuartiereResponse> findAll();
    Optional<Quartiere> findById(Long id);
    QuartiereResponse crea(String nomeQuartiere, String citta);
    QuartiereResponse aggiorna(Long id, String nomeQuartiere, String citta);
}
