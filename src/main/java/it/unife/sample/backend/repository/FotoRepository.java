package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository per la gestione delle foto degli annunci.
 */
public interface FotoRepository extends JpaRepository<Foto, Long> {

    /**
     * Trova tutte le foto di un annuncio ordinate per ordine.
     */
    List<Foto> findByAnnuncio_IdAnnuncioOrderByOrdine(Long idAnnuncio);

    /**
     * Elimina tutte le foto di un annuncio.
     * Usato prima di eliminare l'annuncio per rispettare i vincoli FK.
     */
    void deleteByAnnuncio_IdAnnuncio(Long idAnnuncio);
}