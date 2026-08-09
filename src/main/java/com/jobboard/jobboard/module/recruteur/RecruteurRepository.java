package com.jobboard.jobboard.module.recruteur;

import com.jobboard.jobboard.shared.domain.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Optional<Recruteur> findByEmail(String email);

    List<Recruteur> findByEntrepriseId(Long entrepriseId);

    List<Recruteur> findByStatut(StatutCompte statut);
}