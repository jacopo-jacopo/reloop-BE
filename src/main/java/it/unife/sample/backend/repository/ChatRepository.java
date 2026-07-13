package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// repository per l'accesso ai dati delle chat nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByPropostaGenerante_IdProposta(Long idProposta); // trova una chat in base all'id della proposta generante

    // trova tutte le chat a cui un utente ha partecipato (come proponente o pubblicante),
    // ordinate per data dell'ultimo messaggio o della creazione della chat
    @Query(value = """
        SELECT c.* FROM interazione_chat c
        INNER JOIN proposta p ON c.id_proposta_generante = p.id_proposta
        INNER JOIN annuncio a ON p.id_annuncio_interesse = a.id_annuncio
        WHERE p.id_utente_reg_proponente = :idUtente
           OR a.id_utente_reg_pubblicante = :idUtente
        ORDER BY COALESCE(
            (SELECT MAX(m.data_invio) FROM messaggio m WHERE m.id_chat = c.id_chat),
            c.timestamp_chat
        ) DESC
    """, nativeQuery = true)
    List<Chat> findByUtente(@Param("idUtente") Long idUtente);

    // trova tutte le chat a cui un utente ha partecipato (come proponente o pubblicante) che non hanno messaggi,
    // e che sono state create dopo l'ultima visita dell'utente (se specificata)
    @Query(value = """
        SELECT c.id_chat
        FROM interazione_chat c
        INNER JOIN proposta p ON c.id_proposta_generante = p.id_proposta
        INNER JOIN annuncio a ON p.id_annuncio_interesse = a.id_annuncio
        WHERE (p.id_utente_reg_proponente     = :idUtente
               OR a.id_utente_reg_pubblicante = :idUtente)
          AND NOT EXISTS (SELECT 1 FROM messaggio m WHERE m.id_chat = c.id_chat)
          AND (:ultimaVisita IS NULL OR c.timestamp_chat > :ultimaVisita)
    """, nativeQuery = true)
    List<Long> findVuoteByUtente(@Param("idUtente") Long idUtente, @Param("ultimaVisita") LocalDateTime ultimaVisita);


    long countByStatoChat(Chat.StatoChat stato); // conta il numero di chat con un determinato stato

    // trova tutte le chat a cui un utente ha partecipato e che sono state completate
    @Query(value = """
        SELECT COUNT(*) FROM interazione_chat c
        INNER JOIN proposta p ON c.id_proposta_generante = p.id_proposta
        INNER JOIN annuncio a ON p.id_annuncio_interesse = a.id_annuncio
        WHERE c.stato_chat = 'completata'
          AND (p.id_utente_reg_proponente = :idUtente OR a.id_utente_reg_pubblicante = :idUtente)
    """, nativeQuery = true)
    long countCompletateByUtente(@Param("idUtente") Long idUtente);

    // trova tutte le chat aperte che coinvolgono un annuncio specifico, sia come annuncio di interesse che come annuncio offerto
    @Query(value = """
        SELECT DISTINCT c.* FROM interazione_chat c
        INNER JOIN proposta p ON c.id_proposta_generante = p.id_proposta
        LEFT JOIN annuncio_incluso ai ON ai.id_proposta = p.id_proposta
        WHERE c.stato_chat = 'aperta'
          AND (p.id_annuncio_interesse = :idAnnuncio OR ai.id_annuncio_offerto = :idAnnuncio)
    """, nativeQuery = true)
    List<Chat> findAperteByAnnuncio(@Param("idAnnuncio") Long idAnnuncio);
}