package com.jobboard.jobboard.module.candidat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    Optional<Candidat> findByEmail(String email);
}
