package com.jobboard.jobboard.module.messagerie;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageCountService {

    private final MessageRepository messageRepository;
    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;

    public long countUnreadForUser(String email) {
        // Vérifie candidat
        var candidat = candidatRepository.findByEmail(email);
        if (candidat.isPresent()) {
            return messageRepository.findAll().stream()
                    .filter(m -> m.getDestinataire().getId().equals(candidat.get().getId())
                            && !m.getLu())
                    .count();
        }

        // Vérifie recruteur
        var recruteur = recruteurRepository.findByEmail(email);
        if (recruteur.isPresent()) {
            return messageRepository.findAll().stream()
                    .filter(m -> m.getDestinataire().getId().equals(recruteur.get().getId())
                            && !m.getLu())
                    .count();
        }

        return 0;
    }
}