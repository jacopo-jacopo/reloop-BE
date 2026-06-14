package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface MessaggioRepository extends JpaRepository<Messaggio, Messaggio.MessaggioId> {

    @Query("SELECT m FROM Messaggio m WHERE m.chat.idChat = :idChat ORDER BY m.dataInvio ASC")
    List<Messaggio> findByIdChatOrderByDataInvio(@Param("idChat") Long idChat);

    @Query("SELECT COALESCE(MAX(m.id.idMessaggio), 0) FROM Messaggio m WHERE m.chat.idChat = :idChat")
    Long findMaxIdByIdChat(@Param("idChat") Long idChat);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE Messaggio m SET m.flagLettura = true
        WHERE m.chat.idChat = :idChat
          AND m.flagLettura = false
          AND (m.mittente.idUtenteReg != :idUtente OR m.contenuto LIKE '%è stato rimosso da un amministratore e non è più disponibile.%')
    """)
    void markAsRead(@Param("idChat") Long idChat, @Param("idUtente") Long idUtente);

    @Query(value = """
        SELECT DISTINCT m.id_chat
        FROM messaggio m
        INNER JOIN interazione_chat c ON m.id_chat = c.id_chat
        INNER JOIN proposta p        ON c.id_proposta_generante = p.id_proposta
        INNER JOIN annuncio a        ON p.id_annuncio_interesse = a.id_annuncio
        WHERE m.flag_lettura = false
          AND (m.id_mittente != :idUtente OR m.contenuto LIKE '%è stato rimosso da un amministratore e non è più disponibile.%')
          AND (p.id_utente_reg_proponente       = :idUtente
               OR a.id_utente_reg_pubblicante   = :idUtente)
    """, nativeQuery = true)
    List<Long> findUnreadChatIdsByUtente(@Param("idUtente") Long idUtente);
}