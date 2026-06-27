package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.MessaggioResponse;

import java.util.List;

public interface MessaggioDao {

    List<MessaggioResponse> findByChat(Long idChat);
    MessaggioResponse invia(Long idChat, Long idMittente, String contenuto);
    long findMaxIdByChat(Long idChat);
    void markAsRead(Long idChat, Long idUtente);
    List<Long> findUnreadChatIds(Long idUtente);
}
