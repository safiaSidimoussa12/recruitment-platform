package com.jobboard.jobboard.module.messagerie;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.candidature.Candidature;
import com.jobboard.jobboard.module.candidature.CandidatureRepository;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;
    private final CandidatureRepository candidatureRepository;

    @GetMapping("/messages/{candidatureId}")
    public String conversation(@PathVariable Long candidatureId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature not found."));

        Utilisateur currentUser = getUtilisateurByEmail(userDetails.getUsername());

        Long destinataireId;
        if (currentUser instanceof Candidat) {
            destinataireId = candidature.getOffre().getRecruteur().getId();
        } else {
            destinataireId = candidature.getCandidat().getId();
        }

        model.addAttribute("messages", messageService.findByCandidature(candidatureId));
        model.addAttribute("candidatureId", candidatureId);
        model.addAttribute("destinataireId", destinataireId);
        model.addAttribute("currentUserEmail", userDetails.getUsername());
        return "messagerie/conversation";
    }

    @PostMapping("/messages/{candidatureId}")
    public String envoyer(@PathVariable Long candidatureId,
            @RequestParam String contenu,
            @RequestParam Long destinataireId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur expediteur = getUtilisateurByEmail(userDetails.getUsername());
        Utilisateur destinataire = getUtilisateurById(destinataireId);
        messageService.envoyer(candidatureId, expediteur, destinataire, contenu);
        return "redirect:/messages/" + candidatureId;
    }

    private Utilisateur getUtilisateurByEmail(String email) {
        return candidatRepository.findByEmail(email)
                .map(c -> (Utilisateur) c)
                .orElseGet(() -> recruteurRepository.findByEmail(email)
                        .map(r -> (Utilisateur) r)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }

    private Utilisateur getUtilisateurById(Long id) {
        return candidatRepository.findById(id)
                .map(c -> (Utilisateur) c)
                .orElseGet(() -> recruteurRepository.findById(id)
                        .map(r -> (Utilisateur) r)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }
}