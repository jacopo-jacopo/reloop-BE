package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.NotificaDao;
import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.model.Notifica;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.NotificaRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificaDaoImpl implements NotificaDao {

    private final NotificaRepository notificaRepo;
    private final UtenteRegistratoRepository utenteRepo;

    @Override
    public void crea(Long idDestinatario, Notifica.TipoNotifica tipo, String testo) {
        utenteRepo.findById(idDestinatario).ifPresent(u -> {
            Notifica n = new Notifica();
            n.setDestinatario(u);
            n.setTipo(tipo);
            n.setTesto(testo);
            notificaRepo.save(n);
        });
    }

    @Override
    public List<NotificaResponse> findByUtente(Long idUtente) {
        return notificaRepo
                .findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(idUtente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void segnaLetta(Long idNotifica) {
        notificaRepo.findById(idNotifica).ifPresent(n -> {
            n.setLetta(true);
            notificaRepo.save(n);
        });
    }

    @Override
    public void segnaLutteLette(Long idUtente) {
        notificaRepo.findByDestinatario_IdUtenteRegOrderByTimestampNotificaDesc(idUtente)
                .forEach(n -> { n.setLetta(true); notificaRepo.save(n); });
    }

    @Override
    public long countNonLette(Long idUtente) {
        return notificaRepo.countByDestinatario_IdUtenteRegAndLettaFalse(idUtente);
    }

    private NotificaResponse toResponse(Notifica n) {
        return new NotificaResponse(
                n.getIdNotifica(),
                n.getTipo().name(),
                n.getTesto(),
                n.isLetta(),
                n.getTimestampNotifica()
        );
    }
}
