package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

// interfaccia per l'accesso ai dati delle proposte: estende JpaRepository per fornire metodi CRUD e query personalizzate per le entità Proposta
public interface PropostaRepository extends JpaRepository<Proposta, Long> {
    
    // trova tutte le proposte ricevute da un utente (come pubblicante) ordinate per data di creazione decrescente
    List<Proposta> findByAnnuncioInteresse_Pubblicante_IdUtenteRegOrderByTimestampPropostaDesc(Long idUtente); 
    
    // trova tutte le proposte inviate da un utente (come proponente) ordinate per data di creazione decrescente
    List<Proposta> findByProponente_IdUtenteRegOrderByTimestampPropostaDesc(Long idUtente);

    // conta il numero di nuove proposte ricevute da un utente (come pubblicante) che sono in stato "in_attesa"
    // e che sono state create dopo l'ultima visita dell'utente (se specificata)
    @Query(value = """
        SELECT COUNT(*) FROM proposta p
        INNER JOIN annuncio a ON p.id_annuncio_interesse = a.id_annuncio
        WHERE a.id_utente_reg_pubblicante = :idUtente
          AND p.stato_proposta = 'in_attesa'
          AND (:ultimaVisita IS NULL OR p.timestamp_proposta > :ultimaVisita)
    """, nativeQuery = true)
    long countNuoveProposteRicevute(@Param("idUtente") Long idUtente, @Param("ultimaVisita") LocalDateTime ultimaVisita);

    // trova tutte le proposte che hanno un determinato annuncio come annuncio di interesse, in uno stato specifico,
    // e che non corrispondono a un id di proposta specifica
    List<Proposta> findByAnnuncioInteresse_IdAnnuncioAndStatoPropostaAndIdPropostaNot(
        Long idAnnuncio, Proposta.StatoProposta stato, Long idPropostaEsclusa);

    // trova tutte le proposte che hanno un determinato annuncio come annuncio offerto, in stato "in_attesa",
    // e che non corrispondono a un id di proposta specifica
    @Query("""
        SELECT DISTINCT ai.proposta FROM AnnuncioIncluso ai
        WHERE ai.annuncioOfferto.idAnnuncio = :idAnnuncio
        AND ai.proposta.statoProposta = 'in_attesa'
        AND ai.proposta.idProposta != :idPropostaEsclusa
    """)
    List<Proposta> findInAttesaByAnnuncioOfferto(
        @Param("idAnnuncio") Long idAnnuncio,
        @Param("idPropostaEsclusa") Long idPropostaEsclusa);

    // trova tutte le proposte in un dato stato che hanno questo annuncio come annuncio di interesse
    List<Proposta> findByAnnuncioInteresse_IdAnnuncioAndStatoProposta(
        Long idAnnuncio, Proposta.StatoProposta stato);

    // trova tutte le proposte in un dato stato che hanno un certo annuncio tra quelli offerti
    @Query("""
        SELECT DISTINCT ai.proposta FROM AnnuncioIncluso ai
        WHERE ai.annuncioOfferto.idAnnuncio = :idAnnuncio
        AND ai.proposta.statoProposta = :stato
    """)
    List<Proposta> findByAnnuncioOffertoAndStatoProposta(@Param("idAnnuncio") Long idAnnuncio, @Param("stato") Proposta.StatoProposta stato);

    // conta il numero totale di proposte nel database
    long count();
}