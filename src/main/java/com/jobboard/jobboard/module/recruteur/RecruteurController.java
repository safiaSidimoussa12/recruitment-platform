package com.jobboard.jobboard.module.recruteur;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruteur")
@RequiredArgsConstructor
public class RecruteurController {

    private final RecruteurRepository recruteurRepository;

    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        model.addAttribute("recruteur", recruteur);
        return "recruteur/profil";
    }

    @PostMapping("/profil")
    public String updateProfil(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Recruteur updated) {
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        recruteur.setNom(updated.getNom());
        recruteur.setPrenom(updated.getPrenom());
        recruteur.setTelephone(updated.getTelephone());
        recruteurRepository.save(recruteur);
        return "redirect:/recruteur/profil?success";
    }
}
