package com.jobboard.jobboard.module.candidat;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class CandidatController {

    private final CandidatService candidatService;
    private final CandidatRepository candidatRepository;

    @GetMapping("/candidat/profil")
    public String profil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        model.addAttribute("candidat", candidat);
        return "candidat/profil";
    }

    @PostMapping("/candidat/profil")
    public String updateProfil(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Candidat updated) {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        candidatService.updateProfil(candidat.getId(), updated);
        return "redirect:/candidat/profil?success";
    }

    @PostMapping("/candidat/cv")
    public String uploadCV(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {
        Candidat candidat = candidatRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        candidatService.uploadCV(candidat.getId(), file);
        return "redirect:/candidat/profil?cvSuccess";
    }
}
