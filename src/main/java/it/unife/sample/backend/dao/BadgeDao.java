package it.unife.sample.backend.dao;

import it.unife.sample.backend.dto.response.BadgeOttenutoResponse;
import it.unife.sample.backend.dto.response.BadgeResponse;
import it.unife.sample.backend.model.Badge;

import java.util.List;

public interface BadgeDao {

    List<BadgeOttenutoResponse> findByUtente(Long idUtente);
    List<BadgeResponse> findAll();
    List<Badge> findAllEntity();
    boolean giaOttenuto(Long idUtente, String nomeBadge);
    void assegna(Long idUtente, String nomeBadge);
}
