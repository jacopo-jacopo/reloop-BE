package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Annuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository JPA per l'entità Annuncio.
 * Spring Data genera automaticamente le query dai nomi dei metodi.
 */
public interface AnnuncioRepository extends JpaRepository<Annuncio, Long> {

    /**
     * Trova tutti gli annunci di un utente specifico (per il profilo personale).
     * Restituisce tutti gli stati (attivo, sospeso, chiuso).
     */
    List<Annuncio> findByPubblicante_IdUtenteReg(Long idUtente);

    /**
     * Trova gli annunci di un quartiere specifico con un dato stato,
     * escludendo quelli dell'utente loggato.
     * Usato dalla pagina Annunci: mostra solo annunci altrui nel proprio quartiere.
     */
    List<Annuncio> findByPubblicante_Quartiere_IdQuartiereAndStatoAnnuncioAndPubblicante_IdUtenteRegNot(
        Long idQuartiere,
        Annuncio.StatoAnnuncio stato,
        Long idUtenteEscluso
    );

    /**
     * Trova tutti gli annunci di un quartiere (senza filtro stato né utente).
     * Usato internamente per calcoli aggregati come la CO₂.
     */
    List<Annuncio> findByPubblicante_Quartiere_IdQuartiere(Long idQuartiere);

    /**
     * Ricerca testuale nel titolo (case-insensitive).
     * Usato dalla barra di ricerca nella pagina annunci.
     */
    List<Annuncio> findByTitoloContainingIgnoreCase(String titolo);

    /**
     * Ricerca per categoria (case-insensitive, match parziale).
     */
    List<Annuncio> findByCategoriaContainingIgnoreCase(String categoria);

    /**
     * Filtra per stato — usato dallo StatsController per contare gli annunci attivi.
     */
    List<Annuncio> findByStatoAnnuncio(Annuncio.StatoAnnuncio stato);

    /**
     * Conta gli annunci per stato — usato dalle statistiche pubbliche.
     */
    long countByStatoAnnuncio(Annuncio.StatoAnnuncio stato);
}