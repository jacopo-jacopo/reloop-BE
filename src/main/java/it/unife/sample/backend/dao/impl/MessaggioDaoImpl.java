package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.MessaggioDao;
import it.unife.sample.backend.dto.response.MessaggioResponse;
import it.unife.sample.backend.model.Chat;
import it.unife.sample.backend.model.Messaggio;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.ChatRepository;
import it.unife.sample.backend.repository.MessaggioRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessaggioDaoImpl implements MessaggioDao {

    private final MessaggioRepository messaggioRepo;
    private final ChatRepository chatRepo;
    private final UtenteRegistratoRepository utenteRepo;

    @Override
    public List<MessaggioResponse> findByChat(Long idChat) {
        return messaggioRepo.findByIdChatOrderByDataInvio(idChat).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public MessaggioResponse invia(Long idChat, Long idMittente, String contenuto) {
        Chat chat = chatRepo.findById(idChat)
                .orElseThrow(() -> new IllegalArgumentException("Chat non trovata"));
        UtenteRegistrato mittente = utenteRepo.findById(idMittente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Long maxId = messaggioRepo.findMaxIdByIdChat(idChat);
        Messaggio.MessaggioId msgId = new Messaggio.MessaggioId();
        msgId.setIdMessaggio(maxId + 1);
        msgId.setIdChat(idChat);

        Messaggio msg = new Messaggio();
        msg.setId(msgId);
        msg.setChat(chat);
        msg.setContenuto(contenuto);
        msg.setMittente(mittente);

        return toResponse(messaggioRepo.save(msg));
    }

    @Override
    public long findMaxIdByChat(Long idChat) {
        return messaggioRepo.findMaxIdByIdChat(idChat);
    }

    @Override
    public void markAsRead(Long idChat, Long idUtente) {
        messaggioRepo.markAsRead(idChat, idUtente);
    }

    @Override
    public List<Long> findUnreadChatIds(Long idUtente) {
        return messaggioRepo.findUnreadChatIdsByUtente(idUtente);
    }

    // --- mapping ---

    MessaggioResponse toResponse(Messaggio m) {
        MessaggioResponse.MessaggioIdDto idDto = new MessaggioResponse.MessaggioIdDto(
                m.getId().getIdMessaggio(), m.getId().getIdChat());
        MessaggioResponse.MittenteSummary mittente = new MessaggioResponse.MittenteSummary(
                m.getMittente().getIdUtenteReg(), m.getMittente().getNomeCompleto());
        return new MessaggioResponse(idDto, m.getContenuto(), m.getDataInvio(), m.getFlagLettura(), mittente);
    }
}
