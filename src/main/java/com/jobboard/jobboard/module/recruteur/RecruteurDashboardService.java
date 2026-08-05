package com.jobboard.jobboard.module.recruteur;

import com.jobboard.jobboard.module.candidature.CandidatureRepository;
import com.jobboard.jobboard.module.messagerie.MessageRepository;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.module.offre.OffreRepository;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import com.jobboard.jobboard.shared.domain.StatutOffre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruteurDashboardService {

    private final OffreRepository offreRepository;
    private final CandidatureRepository candidatureRepository;
    private final MessageRepository messageRepository;

    public long countOffresActives(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId).stream()
                .filter(o -> o.getStatut() == StatutOffre.PUBLIEE)
                .count();
    }

    public long countCandidaturesRecues(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId).stream()
                .mapToLong(o -> candidatureRepository.findByOffreId(o.getId()).size())
                .sum();
    }

    public long countMessagesNonLus(Long recruteurId) {
        return candidatureRepository.findAll().stream()
                .filter(c -> c.getOffre().getRecruteur().getId().equals(recruteurId))
                .mapToLong(c -> messageRepository
                        .countByCandidatureIdAndLuFalseAndDestinataireId(c.getId(), recruteurId))
                .sum();
    }

    public List<Offre> getRecentOffres(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId).stream()
                .sorted((a, b) -> b.getDatePublication().compareTo(a.getDatePublication()))
                .limit(5)
                .toList();
    }

    public long countShortlisted(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId).stream()
                .mapToLong(o -> candidatureRepository
                        .findByOffreIdAndStatut(o.getId(), StatutCandidature.SHORTLISTED).size())
                .sum();
    }

    public long countHired(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId).stream()
                .mapToLong(o -> candidatureRepository
                        .findByOffreIdAndStatut(o.getId(), StatutCandidature.HIRED).size())
                .sum();
    }
}
