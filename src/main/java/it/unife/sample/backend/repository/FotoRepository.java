package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// interfaccia per l'accesso ai dati delle foto: estende JpaRepository per fornire metodi CRUD e query personalizzate
public interface FotoRepository extends JpaRepository<Foto, Long> {

    // metodo per ottenere tutte le foto di un annuncio, ordinate per il campo "ordine"
    List<Foto> findByAnnuncio_IdAnnuncioOrderByOrdine(Long idAnnuncio);

    // metodo per eliminare tutte le foto di un annuncio
    void deleteByAnnuncio_IdAnnuncio(Long idAnnuncio);
}