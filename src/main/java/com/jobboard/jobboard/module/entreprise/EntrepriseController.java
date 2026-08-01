package com.jobboard.jobboard.module.entreprise;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;

@Controller
@RequestMapping("/recruteur/entreprise")
@RequiredArgsConstructor
public class EntrepriseController {

    private final EntrepriseService entrepriseService;
    private final RecruteurRepository recruteurRepository;

    @GetMapping
    public String profil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        model.addAttribute("entreprise", recruteur.getEntreprise());
        return "recruteur/entreprise";
    }

    @PostMapping
    public String modifier(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Entreprise updated) {
        var recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        entrepriseService.modifier(recruteur.getEntreprise().getId(), updated);
        return "redirect:/recruteur/entreprise?success";
    }
}
