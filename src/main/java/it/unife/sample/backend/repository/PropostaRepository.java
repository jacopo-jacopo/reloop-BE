package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {
    List<Proposta> findByAnnuncioInteresse_Pubblicante_IdUtenteRegOrderByTimestampPropostaDesc(Long idUtente);
    List<Proposta> findByProponente_IdUtenteRegOrderByTimestampPropostaDesc(Long idUtente);
    /** Proposte in_attesa ricevute dopo l'ultima visita (null = nessuna visita → conta tutto). */
    @Query(value = """
        SELECT COUNT(*) FROM proposta p
        INNER JOIN annuncio a ON p.id_annuncio_interesse = a.id_annuncio
        WHERE a.id_utente_reg_pubblicante = :idUtente
          AND p.stato_proposta = 'in_attesa'
          AND (:ultimaVisita IS NULL OR p.timestamp_proposta > :ultimaVisita)
    """, nativeQuery = true)
    long countNuoveProposteRicevute(
        @Param("idUtente") Long idUtente,
        @Param("ultimaVisita") LocalDateTime ultimaVisita);

    /** Proposte in_attesa che hanno questo annuncio come annuncio_interesse, esclusa quella già gestita. */
    List<Proposta> findByAnnuncioInteresse_IdAnnuncioAndStatoPropostaAndIdPropostaNot(
        Long idAnnuncio, Proposta.StatoProposta stato, Long idPropostaEsclusa);

    /** Proposte in_attesa che hanno questo annuncio tra gli annunci_offerti, esclusa quella già gestita. */
    @Query("""
        SELECT DISTINCT ai.proposta FROM AnnuncioIncluso ai
        WHERE ai.annuncioOfferto.idAnnuncio = :idAnnuncio
        AND ai.proposta.statoProposta = 'in_attesa'
        AND ai.proposta.idProposta != :idPropostaEsclusa
    """)
    List<Proposta> findInAttesaByAnnuncioOfferto(
        @Param("idAnnuncio") Long idAnnuncio,
        @Param("idPropostaEsclusa") Long idPropostaEsclusa);

    long count();
}