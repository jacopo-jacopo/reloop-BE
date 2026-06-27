package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.MessaggioDao;
import it.unife.sample.backend.dao.RecensioneDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;
import it.unife.sample.backend.model.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecensioneService {

    private final RecensioneDao recensioneDao;
    private final MessaggioDao messaggioDao;
    private final UtenteDao utenteDao;

    private static final String RECENSIONE_SUFFIX = "ha lasciato una recensione";

    public List<RecensioneResponse> getByUtente(Long idUtente) {
        return recensioneDao.findByRecensito(idUtente);
    }

    public RecensioneResponse invia(InviaRecensioneRequest req, Long idRecensore) {
        RecensioneResponse risposta = recensioneDao.salva(req, idRecensore);

        if (req.getIdChat() != null) {
            UtenteRegistrato recensore = utenteDao.findEntityById(idRecensore).orElse(null);
            if (recensore != null) {
                String contenuto = recensore.getNomeCompleto() + " " + RECENSIONE_SUFFIX;
                messaggioDao.invia(req.getIdChat(), idRecensore, contenuto);
            }
        }

        return risposta;
    }
}
