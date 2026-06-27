package it.unife.sample.backend.dao.impl;

import it.unife.sample.backend.dao.BadgeDao;
import it.unife.sample.backend.dto.response.BadgeOttenutoResponse;
import it.unife.sample.backend.dto.response.BadgeResponse;
import it.unife.sample.backend.model.Badge;
import it.unife.sample.backend.model.BadgeOttenuto;
import it.unife.sample.backend.repository.BadgeOttenutoRepository;
import it.unife.sample.backend.repository.BadgeRepository;
import it.unife.sample.backend.repository.UtenteRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BadgeDaoImpl implements BadgeDao {

    private final BadgeRepository badgeRepo;
    private final BadgeOttenutoRepository badgeOttenutoRepo;
    private final UtenteRegistratoRepository utenteRepo;

    @Override
    public List<BadgeOttenutoResponse> findByUtente(Long idUtente) {
        return badgeOttenutoRepo.findById_IdUtenteReg(idUtente).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<BadgeResponse> findAll() {
        return badgeRepo.findAll().stream()
                .map(this::toBadgeResponse)
                .toList();
    }

    @Override
    public List<Badge> findAllEntity() {
        return badgeRepo.findAll();
    }

    @Override
    public boolean giaOttenuto(Long idUtente, String nomeBadge) {
        return badgeOttenutoRepo.findById_IdUtenteReg(idUtente).stream()
                .anyMatch(b -> b.getBadge().getNomeBadge().equals(nomeBadge));
    }

    @Override
    public void assegna(Long idUtente, String nomeBadge) {
        utenteRepo.findById(idUtente).ifPresent(utente -> {
            badgeRepo.findById(nomeBadge).ifPresent(badge -> {
                BadgeOttenuto.BadgeOttenutoId id = new BadgeOttenuto.BadgeOttenutoId();
                id.setIdUtenteReg(idUtente);
                id.setNomeBadge(nomeBadge);

                BadgeOttenuto bo = new BadgeOttenuto();
                bo.setId(id);
                bo.setUtente(utente);
                bo.setBadge(badge);
                badgeOttenutoRepo.save(bo);
            });
        });
    }

    private BadgeOttenutoResponse toResponse(BadgeOttenuto bo) {
        BadgeOttenutoResponse.BadgeOttenutoIdDto idDto = new BadgeOttenutoResponse.BadgeOttenutoIdDto(
                bo.getId().getIdUtenteReg(), bo.getId().getNomeBadge());
        return new BadgeOttenutoResponse(idDto, toBadgeResponse(bo.getBadge()), bo.getDataOttenimento());
    }

    private BadgeResponse toBadgeResponse(Badge b) {
        return new BadgeResponse(b.getNomeBadge(), b.getSogliaPunti(),
                b.getDescrizioneBadge(), b.getIconaBadge(), b.getColore());
    }
}
