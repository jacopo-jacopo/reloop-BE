package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.ChatDao;
import it.unife.sample.backend.dto.response.ChatResponse;
import it.unife.sample.backend.model.*;
import it.unife.sample.backend.repository.ChatRepository;
import it.unife.sample.backend.repository.PropostaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatDaoImpl implements ChatDao {

    private final ChatRepository chatRepo;
    private final PropostaRepository propostaRepo;

    @Override
    public List<ChatResponse> findByUtente(Long idUtente) {
        return chatRepo.findByUtente(idUtente).stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<ChatResponse> findById(Long id) {
        return chatRepo.findById(id).map(this::toResponse);
    }

    @Override
    public List<Long> findVuoteByUtente(Long idUtente, LocalDateTime ultimaVisita) {
        return chatRepo.findVuoteByUtente(idUtente, ultimaVisita);
    }

    @Override
    public long countCompletateByUtente(Long idUtente) {
        return chatRepo.countCompletateByUtente(idUtente);
    }

    @Override
    public long countByStato(Chat.StatoChat stato) {
        return chatRepo.countByStatoChat(stato);
    }

    @Override
    public List<ChatResponse> findAperteByAnnuncio(Long idAnnuncio) {
        return chatRepo.findAperteByAnnuncio(idAnnuncio).stream().map(this::toResponse).toList();
    }

    @Override
    public ChatResponse crea(Long idProposta) {
        Proposta proposta = propostaRepo.findById(idProposta)
                .orElseThrow(() -> new IllegalArgumentException("Proposta non trovata"));
        Chat chat = new Chat();
        chat.setPropostaGenerante(proposta);
        chat.setStatoChat(Chat.StatoChat.aperta);
        return toResponse(chatRepo.save(chat));
    }

    @Override
    public ChatResponse updateStato(Long idChat, Chat.StatoChat stato, LocalDateTime dataCompletamento) {
        Chat chat = chatRepo.findById(idChat)
                .orElseThrow(() -> new IllegalArgumentException("Chat non trovata"));
        chat.setStatoChat(stato);
        if (dataCompletamento != null) chat.setDataCompletamento(dataCompletamento);
        return toResponse(chatRepo.save(chat));
    }

    // --- mapping ---

    ChatResponse toResponse(Chat c) {
        return new ChatResponse(
                c.getIdChat(),
                c.getStatoChat().name(),
                c.getDataCompletamento(),
                c.getTimestampChat(),
                toPropostaSummary(c.getPropostaGenerante())
        );
    }

    private ChatResponse.PropostaGeneranteSummary toPropostaSummary(Proposta p) {
        List<ChatResponse.AnnuncioInclusoSummary> offerti = p.getAnnunciOfferti().stream()
                .map(this::toAnnuncioInclusoSummary).toList();
        return new ChatResponse.PropostaGeneranteSummary(
                p.getIdProposta(),
                toUtenteSummary(p.getProponente()),
                toAnnuncioInteresseSummary(p.getAnnuncioInteresse()),
                offerti
        );
    }

    private ChatResponse.UtenteChatSummary toUtenteSummary(UtenteRegistrato u) {
        return new ChatResponse.UtenteChatSummary(u.getIdUtenteReg(), u.getNomeCompleto(), u.getFotoProfilo());
    }

    private ChatResponse.AnnuncioInteresseSummary toAnnuncioInteresseSummary(Annuncio a) {
        return new ChatResponse.AnnuncioInteresseSummary(
                a.getIdAnnuncio(), a.getTitolo(), toUtenteSummary(a.getPubblicante()));
    }

    private ChatResponse.AnnuncioInclusoSummary toAnnuncioInclusoSummary(AnnuncioIncluso ai) {
        ChatResponse.AnnuncioInclusoIdDto idDto = new ChatResponse.AnnuncioInclusoIdDto(
                ai.getId().getIdProposta(), ai.getId().getIdAnnuncioOfferto());
        ChatResponse.AnnuncioOffertoSummary offerto = new ChatResponse.AnnuncioOffertoSummary(
                ai.getAnnuncioOfferto().getIdAnnuncio(), ai.getAnnuncioOfferto().getTitolo());
        return new ChatResponse.AnnuncioInclusoSummary(idDto, ai.getFlagSelezionato(), offerto);
    }
}
