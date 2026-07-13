package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.AnnuncioIncluso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// interfaccia per l'accesso ai dati degli annunci inclusi: estende JpaRepository per fornire metodi CRUD e query personalizzate
public interface AnnuncioInclusoRepository extends JpaRepository<AnnuncioIncluso, AnnuncioIncluso.AnnuncioInclusoId> {
    List<AnnuncioIncluso> findById_IdProposta(Long idProposta); // restituisce la lista degli annunci inclusi in una proposta specifica
}