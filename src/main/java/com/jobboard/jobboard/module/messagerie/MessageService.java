package com.jobboard.jobboard.module.messagerie;

import com.jobboard.jobboard.module.candidature.Candidature;
import com.jobboard.jobboard.module.candidature.CandidatureRepository;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final CandidatureRepository candidatureRepository;

    public List<Message> findByCandidature(Long candidatureId) {
        return messageRepository.findByCandidatureIdOrderByDateEnvoiAsc(candidatureId);
    }

    @Transactional
    public Message envoyer(Long candidatureId, Utilisateur expediteur,
            Utilisateur destinataire, String contenu) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable."));

        Message message = new Message();
        message.setCandidature(candidature);
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setContenu(contenu);
        return messageRepository.save(message);
    }

    @Transactional
    public void marquerLus(Long candidatureId, Long destinataireId) {
        messageRepository.findByCandidatureIdOrderByDateEnvoiAsc(candidatureId)
                .stream()
                .filter(m -> m.getDestinataire().getId().equals(destinataireId) && !m.getLu())
                .forEach(m -> {
                    m.setLu(true);
                    messageRepository.save(m);
                });
    }
}
