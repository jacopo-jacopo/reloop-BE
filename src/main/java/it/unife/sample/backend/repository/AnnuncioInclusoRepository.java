package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.AnnuncioIncluso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnuncioInclusoRepository extends JpaRepository<AnnuncioIncluso, AnnuncioIncluso.AnnuncioInclusoId> {
    List<AnnuncioIncluso> findById_IdProposta(Long idProposta);
}