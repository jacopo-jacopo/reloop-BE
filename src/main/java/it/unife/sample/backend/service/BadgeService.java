package it.unife.sample.backend.service;

import it.unife.sample.backend.model.Badge;
import it.unife.sample.backend.model.BadgeOttenuto;
import it.unife.sample.backend.model.UtenteRegistrato;
import it.unife.sample.backend.repository.BadgeOttenutoRepository;
import it.unife.sample.backend.repository.BadgeRepository;
import it.unife.sample.backend.repository.RecensioneRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Verifica e assegna i badge a un utente in base alle sue statistiche attuali.
 */
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepo;
    private final BadgeOttenutoRepository badgeOttenutoRepo;
    private final UtenteRegistratoRepository utenteRepo;
    private final RecensioneRepository recensioneRepo;

    /**
     * Controlla tutti i badge disponibili e assegna all'utente quelli non ancora ottenuti
     * la cui condizione di sblocco è soddisfatta:
     * - se soglia_punti non è NULL → punteggio >= soglia_punti
     * - se soglia_punti è NULL → vedi {@link #condizioneSpecialeSoddisfatta}
     */
    public void assegnaBadge(UtenteRegistrato utente) {

        List<Badge> tuttiBadge = badgeRepo.findAll();

        List<String> nomiBadgeGiaOttenuti = badgeOttenutoRepo
                .findById_IdUtenteReg(utente.getIdUtenteReg())
                .stream()
                .map(b -> b.getBadge().getNomeBadge())
                .toList();

        for (Badge badge : tuttiBadge) {

            if (nomiBadgeGiaOttenuti.contains(badge.getNomeBadge())) continue;

            boolean sblocca = badge.getSogliaPunti() != null
                    ? utente.getPunteggio() >= badge.getSogliaPunti()
                    : condizioneSpecialeSoddisfatta(badge.getNomeBadge(), utente);

            if (sblocca) {
                BadgeOttenuto.BadgeOttenutoId badgeId = new BadgeOttenuto.BadgeOttenutoId();
                badgeId.setIdUtenteReg(utente.getIdUtenteReg());
                badgeId.setNomeBadge(badge.getNomeBadge());

                BadgeOttenuto badgeOttenuto = new BadgeOttenuto();
                badgeOttenuto.setId(badgeId);
                badgeOttenuto.setUtente(utente);
                badgeOttenuto.setBadge(badge);
                badgeOttenutoRepo.save(badgeOttenuto);
            }
        }
    }

    /**
     * Condizione di sblocco per i badge con soglia_punti NULL.
     * Ogni badge ha una query/condizione dedicata e scritta a mano: quando se ne
     * aggiungono di nuovi va aggiunto un nuovo case qui.
     */
    private boolean condizioneSpecialeSoddisfatta(String nomeBadge, UtenteRegistrato utente) {
        return switch (nomeBadge) {

            // "Raggiungi la prima posizione nella classifica locale."
            case "In cima al mondo" ->
                    utenteRepo.countConPunteggioMaggiore(utente.getPunteggio()) == 0;

            // "Ottieni 5 recensioni a 5 stelle."
            case "Vicino di fiducia" ->
                    recensioneRepo.countById_IdUtenteRegRecensitoAndVoto(utente.getIdUtenteReg(), 5) >= 5;

            // "Supera le 10 recensioni mantenendo la reputazione sopra le 4 stelle!"
            case "Punto di riferimento" -> {
                long totali = recensioneRepo.countById_IdUtenteRegRecensito(utente.getIdUtenteReg());
                Double media = recensioneRepo.mediaVotoById_IdUtenteRegRecensito(utente.getIdUtenteReg());
                yield totali > 10 && media != null && media > 4;
            }

            // "Risparmia 30kg di CO2!"
            case "Verde profondo" ->
                    utente.getCo2Totale().compareTo(new BigDecimal("30")) >= 0;

            // "Risparmia 100kg di CO2!"
            case "Eco-Mostro" ->
                    utente.getCo2Totale().compareTo(new BigDecimal("100")) >= 0;

            // "Supera i 10000 punti, ottieni più di 100 recensioni a 5 stelle e risparmia più di 500kg di CO2!"
            case "Irraggiungibilmente green" -> {
                long recensioni5Stelle = recensioneRepo.countById_IdUtenteRegRecensitoAndVoto(utente.getIdUtenteReg(), 5);
                yield utente.getPunteggio() > 10000
                        && recensioni5Stelle > 100
                        && utente.getCo2Totale().compareTo(new BigDecimal("500")) > 0;
            }

            // Badge senza soglia_punti e senza condizione ancora definita: non sbloccabile
            default -> false;
        };
    }
}
