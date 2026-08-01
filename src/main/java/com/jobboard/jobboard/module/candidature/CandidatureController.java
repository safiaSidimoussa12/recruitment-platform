package com.jobboard.jobboard.module.candidature;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final CandidatRepository candidatRepository;

    @PostMapping("/candidatures")
    public String postuler(@RequestParam Long offreId,
            @RequestParam(required = false) String lettreMotivation,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        try {
            candidatureService.postuler(candidat.getId(), offreId, lettreMotivation);
            return "redirect:/candidat/candidatures?success";
        } catch (IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            return "redirect:/offres/" + offreId + "?erreur";
        }
    }

    @GetMapping("/candidat/candidatures")
    public String historique(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        model.addAttribute("candidatures", candidatureService.findByCandidat(candidat.getId()));
        return "candidat/historique";
    }

    @GetMapping("/recruteur/offres/{offreId}/candidatures")
    public String candidaturesParOffre(@PathVariable Long offreId, Model model) {
        model.addAttribute("candidatures", candidatureService.findByOffre(offreId));
        model.addAttribute("offreId", offreId);
        return "recruteur/candidatures";
    }

    @PostMapping("/recruteur/candidatures/{id}/traiter")
    public String traiter(@PathVariable Long id,
            @RequestParam StatutCandidature statut,
            @RequestParam Long offreId) {
        candidatureService.traiter(id, statut);
        return "redirect:/recruteur/offres/" + offreId + "/candidatures";
    }
}
