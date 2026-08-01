package com.jobboard.jobboard.module.messagerie;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByCandidatureIdOrderByDateEnvoiAsc(Long candidatureId);

    long countByCandidatureIdAndLuFalseAndDestinataireId(Long candidatureId, Long destinataireId);
}
