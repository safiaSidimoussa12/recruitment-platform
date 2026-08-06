package com.jobboard.jobboard.module.candidature;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jobboard.jobboard.module.candidature.InterviewDetails;
import com.jobboard.jobboard.module.candidature.InterviewDetailsRepository;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final CandidatRepository candidatRepository;
    private final InterviewDetailsRepository interviewDetailsRepository;
    private final RecruteurRepository recruteurRepository;
    private final com.jobboard.jobboard.module.offre.OffreService offreService;

    @PostMapping("/candidatures")
    public String postuler(@RequestParam Long offreId,
            @RequestParam(required = false) String lettreMotivation,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        try {
            candidatureService.postuler(candidat.getId(), offreId, lettreMotivation);
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
            return "redirect:/candidat/candidatures";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreurOffre", e.getMessage());
            return "redirect:/offres/" + offreId;
        }
    }

    @GetMapping("/recruteur/offres/{offreId}/candidatures")
    public String candidaturesParOffre(@PathVariable Long offreId,
            @RequestParam(defaultValue = "ALL") String statut,
            Model model) {
        List<Candidature> candidatures;

        if ("ALL".equals(statut)) {
            candidatures = candidatureService.findByOffre(offreId);
        } else {
            StatutCandidature s = StatutCandidature.valueOf(statut);
            candidatures = candidatureService.findByOffreAndStatut(offreId, s);
        }

        // Compter par statut pour les tabs
        Map<String, Long> counts = new java.util.LinkedHashMap<>();
        counts.put("ALL", (long) candidatureService.findByOffre(offreId).size());
        counts.put("APPLIED", countByStatut(offreId, StatutCandidature.APPLIED));
        counts.put("SHORTLISTED", countByStatut(offreId, StatutCandidature.SHORTLISTED));
        counts.put("INTERVIEWS", countByStatutList(offreId,
                List.of(StatutCandidature.INTERVIEW_SCHEDULED, StatutCandidature.INTERVIEW_COMPLETED)));
        counts.put("HIRED", countByStatut(offreId, StatutCandidature.HIRED));
        counts.put("REJECTED", countByStatut(offreId, StatutCandidature.REJECTED));

        // Pour INTERVIEWS, combiner scheduled + completed
        if ("INTERVIEWS".equals(statut)) {
            candidatures = new java.util.ArrayList<>();
            candidatures.addAll(candidatureService.findByOffreAndStatut(
                    offreId, StatutCandidature.INTERVIEW_SCHEDULED));
            candidatures.addAll(candidatureService.findByOffreAndStatut(
                    offreId, StatutCandidature.INTERVIEW_COMPLETED));
        }

        model.addAttribute("candidatures", candidatures);
        model.addAttribute("offreId", offreId);
        model.addAttribute("statut", statut);
        model.addAttribute("counts", counts);
        model.addAttribute("offre", offreService.findById(offreId));
        return "recruteur/candidatures";
    }

    private long countByStatut(Long offreId, StatutCandidature statut) {
        return candidatureService.findByOffreAndStatut(offreId, statut).size();
    }

    private long countByStatutList(Long offreId, List<StatutCandidature> statuts) {
        return statuts.stream()
                .mapToLong(s -> candidatureService.findByOffreAndStatut(offreId, s).size())
                .sum();
    }

    @PostMapping("/recruteur/candidatures/{id}/statut")
    public String changerStatut(@PathVariable Long id,
            @RequestParam StatutCandidature statut,
            @RequestParam Long offreId,
            RedirectAttributes redirectAttributes) {
        try {
            candidatureService.changerStatut(id, statut);
            redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/recruteur/offres/" + offreId + "/candidatures";
    }

    @GetMapping("/recruteur/candidatures/{id}/interview")
    public String interviewForm(@PathVariable Long id, Model model) {
        Candidature candidature = candidatureService.findById(id);
        model.addAttribute("candidature", candidature);
        model.addAttribute("request", new InterviewRequest());
        interviewDetailsRepository.findByCandidatureId(id)
                .ifPresent(d -> model.addAttribute("existingDetails", d));
        return "recruteur/interview-form";
    }

    @PostMapping("/recruteur/candidatures/{id}/interview")
    public String scheduleInterview(@PathVariable Long id,
            @ModelAttribute InterviewRequest request,
            RedirectAttributes redirectAttributes) {
        Candidature candidature = candidatureService.scheduleInterview(id, request);
        redirectAttributes.addFlashAttribute("success", "Interview scheduled successfully.");
        return "redirect:/recruteur/offres/" + candidature.getOffre().getId() + "/candidatures";
    }

    @GetMapping("/candidat/candidatures")
    public String historique(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));

        List<Candidature> candidatures = candidatureService.findByCandidat(candidat.getId());

        // Map candidatureId -> InterviewDetails
        Map<Long, InterviewDetails> interviewMap = candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.INTERVIEW_SCHEDULED
                        || c.getStatut() == StatutCandidature.INTERVIEW_COMPLETED)
                .collect(java.util.stream.Collectors.toMap(
                        Candidature::getId,
                        c -> interviewDetailsRepository.findByCandidatureId(c.getId()).orElse(null),
                        (a, b) -> a));

        model.addAttribute("candidatures", candidatures);
        model.addAttribute("interviewMap", interviewMap);
        return "candidat/historique";
    }

    @GetMapping("/recruteur/pipeline/shortlisted")
    public String shortlisted(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = getRecruteur(userDetails);
        model.addAttribute("candidatures",
                candidatureService.findByRecruteurAndStatut(recruteur.getId(), StatutCandidature.SHORTLISTED));
        model.addAttribute("pageTitle", "Shortlisted Candidates");
        model.addAttribute("statut", "SHORTLISTED");
        return "recruteur/pipeline";
    }

    @GetMapping("/recruteur/pipeline/interviews")
    public String interviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = getRecruteur(userDetails);

        List<Candidature> result = new java.util.ArrayList<>();
        result.addAll(candidatureService.findByRecruteurAndStatut(
                recruteur.getId(), StatutCandidature.INTERVIEW_SCHEDULED));
        result.addAll(candidatureService.findByRecruteurAndStatut(
                recruteur.getId(), StatutCandidature.INTERVIEW_COMPLETED));

        model.addAttribute("candidatures", result);
        model.addAttribute("pageTitle", "Interviews");
        model.addAttribute("statut", "INTERVIEWS");
        return "recruteur/pipeline";
    }

    @GetMapping("/recruteur/pipeline/hired")
    public String hired(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = getRecruteur(userDetails);
        model.addAttribute("candidatures",
                candidatureService.findByRecruteurAndStatut(recruteur.getId(), StatutCandidature.HIRED));
        model.addAttribute("pageTitle", "Hired Candidates");
        model.addAttribute("statut", "HIRED");
        return "recruteur/pipeline";
    }

    @GetMapping("/recruteur/pipeline/rejected")
    public String rejected(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = getRecruteur(userDetails);
        model.addAttribute("candidatures",
                candidatureService.findByRecruteurAndStatut(recruteur.getId(), StatutCandidature.REJECTED));
        model.addAttribute("pageTitle", "Rejected Candidates");
        model.addAttribute("statut", "REJECTED");
        return "recruteur/pipeline";
    }

    private Recruteur getRecruteur(UserDetails userDetails) {
        return recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found."));
    }

}
