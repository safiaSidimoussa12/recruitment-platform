package com.jobboard.jobboard.module.recruteur;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Optional<Recruteur> findByEmail(String email);

    List<Recruteur> findByEntrepriseId(Long entrepriseId);
}
