package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.ChatDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.response.StatsResponse;
import it.unife.sample.backend.model.Chat;
import it.unife.sample.backend.model.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ChatDao chatDao;
    private final UtenteDao utenteDao;

    public StatsResponse getPubbliche() {
        long scambi = chatDao.countByStato(Chat.StatoChat.completata);
        long utenti = utenteDao.count();
        double co2 = utenteDao.findAll().stream()
                .mapToDouble(u -> u.getCo2Totale().doubleValue())
                .sum();
        return new StatsResponse(scambi, co2, utenti);
    }

    public BigDecimal getCo2Quartiere(Long idQuartiere) {
        return utenteDao.findByQuartiere(idQuartiere).stream()
                .map(UtenteRegistrato::getCo2Totale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
