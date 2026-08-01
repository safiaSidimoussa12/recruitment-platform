package com.jobboard.jobboard.module.entreprise;

import com.jobboard.jobboard.shared.domain.StatutEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findByStatut(StatutEntreprise statut);

    boolean existsByNom(String nom);
}
