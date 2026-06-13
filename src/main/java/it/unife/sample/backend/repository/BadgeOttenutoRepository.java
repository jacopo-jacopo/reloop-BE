package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.BadgeOttenuto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BadgeOttenutoRepository extends JpaRepository<BadgeOttenuto, BadgeOttenuto.BadgeOttenutoId> {
    List<BadgeOttenuto> findById_IdUtenteReg(Long idUtente);
}