package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.QuartiereDao;
import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.model.Quartiere;
import it.unife.sample.backend.repository.QuartiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// implementazione del DAO dei quartieri, utilizza il repository dei quartieri per accedere ai dati nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato (in questo caso, quartiereRepo)
public class QuartiereDaoImpl implements QuartiereDao {

    private final QuartiereRepository quartiereRepo;

    // restituisce la lista di tutti i quartieri, mappando ogni entità Quartiere in un oggetto QuartiereResponse
    @Override
    public List<QuartiereResponse> findAll() {
        return quartiereRepo.findAll().stream() 
                .map(this::toResponse) // per ogni quartiere nello stream, chiama il metodo toResponse per convertirlo in un oggetto QuartiereResponse
                                       // che mette in uno Stream<QuartiereResponse>
                .toList();
    }

    // restituisce un Optional contenente il quartiere con l'ID specificato, se esiste
    @Override
    public Optional<Quartiere> findById(Long id) {
        return quartiereRepo.findById(id);
    }

    // restituisce la lista di quartieri che appartengono alla città specificata, mappando ogni entità Quartiere in un oggetto QuartiereResponse
    @Override
    public QuartiereResponse crea(String nomeQuartiere, String citta) {
        Quartiere q = new Quartiere();
        q.setNomeQuartiere(nomeQuartiere);
        q.setCitta(citta);
        return toResponse(quartiereRepo.save(q)); // salva il nuovo quartiere nel database e restituisce un oggetto QuartiereResponse corrispondente
    }

    // aggiorna il quartiere con l'ID specificato, se esiste, e restituisce un oggetto QuartiereResponse corrispondente
    @Override
    public QuartiereResponse aggiorna(Long id, String nomeQuartiere, String citta) {
        Quartiere q = quartiereRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quartiere non trovato"));
        if (nomeQuartiere != null) q.setNomeQuartiere(nomeQuartiere);
        if (citta != null)         q.setCitta(citta);
        return toResponse(quartiereRepo.save(q)); // salva le modifiche al quartiere nel db e restituisce un oggetto QuartiereResponse corrispondente
    }

    // converte un'entità Quartiere in un oggetto QuartiereResponse
    private QuartiereResponse toResponse(Quartiere q) {
        return new QuartiereResponse(q.getIdQuartiere(), q.getNomeQuartiere(), q.getCitta());
    }
}
