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
    private final InterviewDetailsRepository interviewDetailsRepository;

    @Transactional
    public Candidature postuler(Long candidatId, Long offreId, String lettreMotivation) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable."));

        if (candidatureRepository.existsByCandidatAndOffre(candidat, offre)) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        Candidature candidature = new Candidature();
        candidature.setCandidat(candidat);
        candidature.setOffre(offre);
        candidature.setLettreMotivation(lettreMotivation);
        candidature.setStatut(StatutCandidature.APPLIED);
        return candidatureRepository.save(candidature);
    }

    public List<Candidature> findByCandidat(Long candidatId) {
        return candidatureRepository.findByCandidatId(candidatId);
    }

    public List<Candidature> findByOffre(Long offreId) {
        return candidatureRepository.findByOffreId(offreId);
    }

    @Transactional
    public Candidature changerStatut(Long candidatureId, StatutCandidature statut) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable."));
        if (candidature.getStatut() == StatutCandidature.HIRED ||
                candidature.getStatut() == StatutCandidature.REJECTED) {
            throw new IllegalStateException("Cannot change status of a finalized application.");
        }
        candidature.setStatut(statut);
        return candidatureRepository.save(candidature);
    }

    @Transactional
    public Candidature scheduleInterview(Long candidatureId, InterviewRequest request) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable."));

        InterviewDetails details = interviewDetailsRepository
                .findByCandidatureId(candidatureId)
                .orElse(new InterviewDetails());

        details.setCandidature(candidature);
        details.setInterviewDate(request.getInterviewDate());
        details.setInterviewTime(request.getInterviewTime());
        details.setLocation(request.getLocation());
        details.setInterviewer(request.getInterviewer());
        details.setNotes(request.getNotes());
        interviewDetailsRepository.save(details);

        candidature.setStatut(StatutCandidature.INTERVIEW_SCHEDULED);
        return candidatureRepository.save(candidature);
    }

    public Candidature findById(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable."));
    }

    public List<Candidature> findByOffreAndStatut(Long offreId, StatutCandidature statut) {
        return candidatureRepository.findByOffreIdAndStatut(offreId, statut);
    }

    public List<Candidature> findByRecruteurAndStatut(Long recruteurId, StatutCandidature statut) {
        return candidatureRepository.findAll().stream()
                .filter(c -> c.getOffre().getRecruteur().getId().equals(recruteurId)
                        && c.getStatut() == statut)
                .toList();
    }

    public List<Candidature> findByRecruteur(Long recruteurId) {
        return candidatureRepository.findAll().stream()
                .filter(c -> c.getOffre().getRecruteur().getId().equals(recruteurId))
                .toList();
    }

}