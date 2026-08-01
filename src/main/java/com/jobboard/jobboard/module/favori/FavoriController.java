package com.jobboard.jobboard.module.favori;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriService favoriService;
    private final CandidatRepository candidatRepository;

    @PostMapping("/candidat/favoris/toggle")
    public String toggle(@RequestParam Long offreId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        favoriService.toggleFavori(candidat.getId(), offreId);
        return "redirect:/offres/" + offreId;
    }

    @GetMapping("/candidat/favoris")
    public String mesFavoris(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        model.addAttribute("favoris", favoriService.findByCandidat(candidat.getId()));
        return "candidat/favoris";
    }
}
