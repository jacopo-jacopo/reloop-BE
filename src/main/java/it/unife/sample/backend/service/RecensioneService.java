package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.MessaggioDao;
import it.unife.sample.backend.dao.RecensioneDao;
import it.unife.sample.backend.dao.UtenteDao;
import it.unife.sample.backend.dto.request.InviaRecensioneRequest;
import it.unife.sample.backend.dto.response.RecensioneResponse;
import it.unife.sample.backend.model.Notifica;
import it.unife.sample.backend.model.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// service per la gestione delle recensioni: fornisce metodi per ottenere le recensioni di un utente e inviare una nuova recensione
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class RecensioneService {

    private final RecensioneDao recensioneDao;
    private final MessaggioDao messaggioDao;
    private final UtenteDao utenteDao;
    private final NotificaService notificaService;

    // costante per il suffisso del messaggio di notifica quando un utente lascia una recensione
    private static final String RECENSIONE_SUFFIX = "ha lasciato una recensione";

    // metodo per ottenere le recensioni di un utente: riceve l'id dell'utente e chiama il metodo findByRecensito del RecensioneDao
    public List<RecensioneResponse> getByUtente(Long idUtente) {
        return recensioneDao.findByRecensito(idUtente);
    }

    // metodo per inviare una nuova recensione: riceve i dati della recensione e l'id del recensore
    public RecensioneResponse invia(InviaRecensioneRequest req, Long idRecensore) {
        RecensioneResponse risposta = recensioneDao.salva(req, idRecensore);

        if (req.getIdChat() != null) {
            UtenteRegistrato recensore = utenteDao.findEntityById(idRecensore).orElse(null);
            if (recensore != null) {
                String contenuto = recensore.getNomeCompleto() + " " + RECENSIONE_SUFFIX;
                messaggioDao.invia(req.getIdChat(), idRecensore, contenuto);
            }
        }

        notificaService.crea(req.getIdUtenteRegRecensito(), Notifica.TipoNotifica.NUOVA_RECENSIONE,
                "Hai ricevuto una nuova recensione.");

        return risposta;
    }
}
