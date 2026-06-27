package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.model.Chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatDao {

    List<ChatResponse> findByUtente(Long idUtente);
    Optional<ChatResponse> findById(Long id);
    List<Long> findVuoteByUtente(Long idUtente, LocalDateTime ultimaVisita);
    long countCompletateByUtente(Long idUtente);
    long countByStato(Chat.StatoChat stato);
    List<ChatResponse> findAperteByAnnuncio(Long idAnnuncio);
    ChatResponse crea(Long idProposta);
    ChatResponse updateStato(Long idChat, Chat.StatoChat stato, LocalDateTime dataCompletamento);
}
