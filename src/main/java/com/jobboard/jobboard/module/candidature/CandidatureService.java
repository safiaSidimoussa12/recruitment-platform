package com.jobboard.jobboard.module.candidature;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.module.offre.OffreRepository;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final CandidatRepository candidatRepository;
    private final OffreRepository offreRepository;

    @Transactional
    public Candidature postuler(Long candidatId, Long offreId, String lettreMotivation) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable."));

        if (candidatureRepository.existsByCandidatAndOffre(candidat, offre)) {
            throw new IllegalStateException("Vous avez déjà postulé à cette offre.");
        }

        Candidature candidature = new Candidature();
        candidature.setCandidat(candidat);
        candidature.setOffre(offre);
        candidature.setLettreMotivation(lettreMotivation);
        candidature.setStatut(StatutCandidature.EN_ATTENTE);
        return candidatureRepository.save(candidature);
    }

    public List<Candidature> findByCandidat(Long candidatId) {
        return candidatureRepository.findByCandidatId(candidatId);
    }

    public List<Candidature> findByOffre(Long offreId) {
        return candidatureRepository.findByOffreId(offreId);
    }

    @Transactional
    public Candidature traiter(Long candidatureId, StatutCandidature statut) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable."));
        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new IllegalStateException("Candidature déjà traitée.");
        }
        candidature.setStatut(statut);
        return candidatureRepository.save(candidature);
    }
}
