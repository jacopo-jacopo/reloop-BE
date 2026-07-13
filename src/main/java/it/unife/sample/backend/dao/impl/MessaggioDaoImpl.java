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

// implementazione dell'interfaccia MessaggioDao: fornisce i metodi per l'accesso ai dati dei messaggi nel database
@Repository // indica che questa classe è un componente di tipo repository, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class MessaggioDaoImpl implements MessaggioDao {

    private final MessaggioRepository messaggioRepo;
    private final ChatRepository chatRepo;
    private final UtenteRegistratoRepository utenteRepo;

    // trova tutti i messaggi di una chat specifica, ordinati per data di invio
    @Override
    public List<MessaggioResponse> findByChat(Long idChat) {
        return messaggioRepo.findByIdChatOrderByDataInvio(idChat).stream()
                .map(this::toResponse).toList();
    }

    // invia un messaggio in una chat specifica e lo salva nel db
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

    // trova il numero massimo di ID dei messaggi in una chat specifica
    @Override
    public long findMaxIdByChat(Long idChat) {
        return messaggioRepo.findMaxIdByIdChat(idChat);
    }

    // segna tutti i messaggi di una chat come letti da un utente specifico
    @Override
    public void markAsRead(Long idChat, Long idUtente) {
        messaggioRepo.markAsRead(idChat, idUtente);
    }

    // trova gli id delle chat con messaggi non letti da un utente specifico
    @Override
    public List<Long> findUnreadChatIds(Long idUtente) {
        return messaggioRepo.findUnreadChatIdsByUtente(idUtente);
    }


    
    // mapping da Messaggio a MessaggioResponse
    MessaggioResponse toResponse(Messaggio m) {
        MessaggioResponse.MessaggioIdDto idDto = new MessaggioResponse.MessaggioIdDto(
                m.getId().getIdMessaggio(), m.getId().getIdChat());
        MessaggioResponse.MittenteSummary mittente = new MessaggioResponse.MittenteSummary(
                m.getMittente().getIdUtenteReg(), m.getMittente().getNomeCompleto());
        return new MessaggioResponse(idDto, m.getContenuto(), m.getDataInvio(), m.getFlagLettura(), mittente);
    }
}
