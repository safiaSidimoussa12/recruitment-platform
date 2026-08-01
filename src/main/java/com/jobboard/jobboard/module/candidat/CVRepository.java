package com.jobboard.jobboard.module.candidat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CVRepository extends JpaRepository<CV, Long> {
    Optional<CV> findByCandidat(Candidat candidat);

    void deleteByCandidat(Candidat candidat);
}
