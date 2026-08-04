package com.jobboard.jobboard.module.candidature;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterviewDetailsRepository extends JpaRepository<InterviewDetails, Long> {
    Optional<InterviewDetails> findByCandidatureId(Long candidatureId);
}