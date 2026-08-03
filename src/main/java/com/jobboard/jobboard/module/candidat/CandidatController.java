package com.jobboard.jobboard.module.candidat;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;

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

    @GetMapping("/candidat/cv/download/{candidatId}")
    public ResponseEntity<Resource> downloadCV(@PathVariable Long candidatId) throws IOException {
        Candidat candidat = candidatService.findById(candidatId);
        CV cv = candidatService.getCVByCandidat(candidatId);

        Path path = Paths.get(cv.getFichierUrl());
        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
