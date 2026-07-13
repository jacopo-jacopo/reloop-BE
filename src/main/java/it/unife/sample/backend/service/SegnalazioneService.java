package it.unife.sample.backend.service;

import it.unife.sample.backend.dao.SegnalazioneDao;
import it.unife.sample.backend.dto.request.ChiudiSegnalazioneRequest;
import it.unife.sample.backend.dto.request.InviaSegnalazioneRequest;
import it.unife.sample.backend.dto.response.SegnalazioneResponse;
import it.unife.sample.backend.model.Notifica;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// service per la gestione delle segnalazioni: fornisce metodi per ottenere tutte le segnalazioni, ottenere le segnalazioni dell'utente loggato,
// inviare una nuova segnalazione, prendere in carico una segnalazione e chiudere una segnalazione (ultime due solo per admin)
@Service // indica che questa classe è un componente di tipo service, gestito da Spring
@RequiredArgsConstructor // genera un costruttore con un parametro per ogni campo finale non inizializzato
public class SegnalazioneService {

    private final SegnalazioneDao segnalazioneDao;
    private final NotificaService notificaService;

    // metodo per ottenere tutte le segnalazioni: chiama il metodo findAll del SegnalazioneDao
    public List<SegnalazioneResponse> getTutte() {
        return segnalazioneDao.findAll();
    }

    // metodo per ottenere le segnalazioni dell'utente loggato: riceve l'id dell'utente e chiama il metodo findByUtente del SegnalazioneDao
    public List<SegnalazioneResponse> getMie(Long idUtente) {
        return segnalazioneDao.findByUtente(idUtente);
    }

    // metodo per inviare una nuova segnalazione: riceve la richiesta di invio della segnalazione e l'id dell'utente loggato,
    // verifica che l'utente non abbia già segnalato lo stesso annuncio e chiama il metodo crea del SegnalazioneDao per salvare la segnalazione
    public SegnalazioneResponse invia(InviaSegnalazioneRequest req, Long idUtente) {
        if (segnalazioneDao.existsSegnalazioneAperta(idUtente, req.getIdAnnuncioSegnalato())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hai già segnalato questo annuncio");
        }
        return segnalazioneDao.crea(req, idUtente);
    }

    // metodo per prendere in carico una segnalazione: riceve l'id della segnalazione e l'id dell'admin che la prende in carico,
    // chiama il metodo prendiInCarico del SegnalazioneDao per aggiornare lo stato della segnalazione e restituisce la segnalazione aggiornata
    public SegnalazioneResponse prendiInCarico(Long idSegnalazione, Long idAdmin) {
        return segnalazioneDao.prendiInCarico(idSegnalazione, idAdmin);
    }

    // metodo per chiudere una segnalazione: riceve l'id della segnalazione, la richiesta di chiusura e l'id dell'admin che la chiude,
    // chiama il metodo chiudi del SegnalazioneDao per aggiornare lo stato della segnalazione e, se richiesto, crea una notifica 
    // per il proprietario dell'annuncio segnalato informandolo che il suo annuncio è stato oscurato
    public SegnalazioneResponse chiudi(Long idSegnalazione, ChiudiSegnalazioneRequest req, Long idAdmin) {
        SegnalazioneResponse risposta = segnalazioneDao.chiudi(idSegnalazione, req, idAdmin);
        if (req.isOscuraAnnuncio()) {
            Long idProprietario = risposta.getAnnuncioSegnalato().getPubblicante().getIdUtenteReg();
            notificaService.crea(idProprietario, Notifica.TipoNotifica.ANNUNCIO_ELIMINATO,
                    "Il tuo annuncio \"" + risposta.getAnnuncioSegnalato().getTitolo() + "\" è stato oscurato a seguito di una segnalazione.");
        }
        return risposta;
    }
}
