package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Annuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// repository per l'accesso ai dati degli annunci nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface AnnuncioRepository extends JpaRepository<Annuncio, Long> {

    // trova tutti gli annunci pubblicati da un utente specifico
    List<Annuncio> findByPubblicante_IdUtenteReg(Long idUtente);

    // trova tutti gli annunci di un quartiere specifico, con stato attivo e pubblicati da utenti diversi da quello specificato
    List<Annuncio> findByPubblicante_Quartiere_IdQuartiereAndStatoAnnuncioAndPubblicante_IdUtenteRegNot(
        Long idQuartiere,
        Annuncio.StatoAnnuncio stato,
        Long idUtenteEscluso
    );

    // trova tutti gli annunci di un quartiere specifico
    List<Annuncio> findByPubblicante_Quartiere_IdQuartiere(Long idQuartiere);

    // trova tutti gli annunci con un titolo che contiene una determinata stringa (case-insensitive, match parziale)
    List<Annuncio> findByTitoloContainingIgnoreCase(String titolo);

    // trova tutti gli annunci con una categoria che contiene una determinata stringa (case-insensitive, match parziale)
    List<Annuncio> findByCategoriaContainingIgnoreCase(String categoria);

    // trova tutti gli annunci con un determinato stato (ad esempio, attivo, completato, ecc.)
    List<Annuncio> findByStatoAnnuncio(Annuncio.StatoAnnuncio stato);

    // conta il numero di annunci con un determinato stato (ad esempio, attivo, completato, ecc.)
    long countByStatoAnnuncio(Annuncio.StatoAnnuncio stato);
}