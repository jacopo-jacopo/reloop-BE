package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// repository per l'accesso ai dati dei messaggi nel database: estende JpaRepository per fornire le operazioni CRUD di base
public interface MessaggioRepository extends JpaRepository<Messaggio, Messaggio.MessaggioId> {

    // trova tutti i messaggi di una chat specifica, ordinati per data di invio in ordine crescente
    @Query("SELECT m FROM Messaggio m WHERE m.chat.idChat = :idChat ORDER BY m.dataInvio ASC")
    List<Messaggio> findByIdChatOrderByDataInvio(@Param("idChat") Long idChat);

    // trova l'id massimo dei messaggi di una chat specifica, utile per generare nuovi id univoci
    @Query("SELECT COALESCE(MAX(m.id.idMessaggio), 0) FROM Messaggio m WHERE m.chat.idChat = :idChat")
    Long findMaxIdByIdChat(@Param("idChat") Long idChat);

    // segna come letti tutti i messaggi di una chat specifica che non sono stati inviati dall'utente specificato, 
    // oppure che contengono un messaggio di sistema da parte di un amministratore
    @Modifying(clearAutomatically = true) // indica che questa query modifica i dati nel database e che il contesto di persistenza 
                                          // deve essere aggiornato automaticamente dopo l'esecuzione della query
    @Transactional // indica che questa operazione deve essere eseguita all'interno di una transazione, 
                   // garantendo quindi che tutte le modifiche siano atomiche
    @Query("""
        UPDATE Messaggio m SET m.flagLettura = true
        WHERE m.chat.idChat = :idChat
          AND m.flagLettura = false
          AND (m.mittente.idUtenteReg != :idUtente OR m.contenuto LIKE '%è stato rimosso da un amministratore e non è più disponibile.%')
    """)
    void markAsRead(@Param("idChat") Long idChat, @Param("idUtente") Long idUtente);

    // trova gli id delle chat che contengono messaggi non letti per un utente specifico,
    // escludendo i messaggi inviati dallo stesso utente o quelli che contengono un messaggio di sistema da parte di un amministratore
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