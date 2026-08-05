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
    private final RecruteurDashboardService dashboardService;

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

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found."));

        model.addAttribute("recruteur", recruteur);
        model.addAttribute("totalOffres", dashboardService.countOffresActives(recruteur.getId()));
        model.addAttribute("totalCandidatures", dashboardService.countCandidaturesRecues(recruteur.getId()));
        model.addAttribute("messagesNonLus", dashboardService.countMessagesNonLus(recruteur.getId()));
        model.addAttribute("recentOffres", dashboardService.getRecentOffres(recruteur.getId()));

        model.addAttribute("totalShortlisted", dashboardService.countShortlisted(recruteur.getId()));
        model.addAttribute("totalHired", dashboardService.countHired(recruteur.getId()));
        return "recruteur/dashboard";
    }

}
