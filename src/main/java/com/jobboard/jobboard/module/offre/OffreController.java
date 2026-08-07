package com.jobboard.jobboard.module.offre;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.candidature.CandidatureRepository;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import com.jobboard.jobboard.shared.domain.StatutOffre;

import com.jobboard.jobboard.shared.domain.TypeContrat;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;
    private final RecruteurRepository recruteurRepository;
    private final CandidatRepository candidatRepository;
    private final CandidatureRepository candidatureRepository;

    // ── Public ──────────────────────────────────────────

    @GetMapping("/offres")
    public String liste(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) TypeContrat typeContrat,
            @RequestParam(required = false) Double salaireMin,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<Offre> offres = offreService.search(keyword, ville, domaine,
                typeContrat, salaireMin, page, 10);
        model.addAttribute("offres", offres);
        model.addAttribute("keyword", keyword);
        model.addAttribute("ville", ville);
        model.addAttribute("domaine", domaine);
        model.addAttribute("typeContrat", typeContrat);
        model.addAttribute("salaireMin", salaireMin);
        model.addAttribute("typesContrat", TypeContrat.values());
        return "offre/liste";
    }

    @GetMapping("/offres/{id}")
    public String detail(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Offre offre = offreService.findById(id);
        model.addAttribute("offre", offre);

        // Vérifie si le candidat a déjà postulé
        if (userDetails != null) {
            candidatRepository.findByEmail(userDetails.getUsername()).ifPresent(candidat -> {
                boolean dejaPostule = candidatureRepository
                        .existsByCandidatAndOffre(candidat, offre);
                model.addAttribute("dejaPostule", dejaPostule);
            });
        }

        return "offre/detail";
    }
    // ── Recruteur ────────────────────────────────────────

    @GetMapping("/recruteur/offres/new")
    public String newOffreForm(Model model) {
        model.addAttribute("offre", new Offre());
        model.addAttribute("typesContrat", TypeContrat.values());
        return "recruteur/offre-form";
    }

    @PostMapping("/recruteur/offres")
    public String publier(@Valid @ModelAttribute("offre") Offre offre,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("typesContrat", TypeContrat.values());
            return "recruteur/offre-form";
        }
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        offre.setRecruteur(recruteur);
        offre.setEntreprise(recruteur.getEntreprise());
        offreService.publier(offre);
        return "redirect:/recruteur/offres";
    }

    @GetMapping("/recruteur/offres/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("offre", offreService.findById(id));
        model.addAttribute("typesContrat", TypeContrat.values());
        return "recruteur/offre-form";
    }

    @PostMapping("/recruteur/offres/{id}")
    public String modifier(@PathVariable Long id,
            @Valid @ModelAttribute("offre") Offre offre,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("typesContrat", TypeContrat.values());
            return "recruteur/offre-form";
        }
        offreService.modifier(id, offre);
        return "redirect:/recruteur/offres";
    }

    //
    @GetMapping("/recruteur/offres")
    public String mesOffres(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        model.addAttribute("offres", offreService.findActiveByRecruteur(recruteur.getId()));
        return "recruteur/mes-offres";
    }

    @GetMapping("/recruteur/offres/archived")
    public String archivedOffres(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Recruteur recruteur = recruteurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Recruteur introuvable."));
        model.addAttribute("offres", offreService.findArchivedByRecruteur(recruteur.getId()));
        return "recruteur/archived-offres";
    }

    @PostMapping("/recruteur/offres/{id}/supprimer")
    public String archiver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        offreService.archiver(id);
        redirectAttributes.addFlashAttribute("success", "Job archived successfully.");
        return "redirect:/recruteur/offres";
    }

    @PostMapping("/recruteur/offres/{id}/restaurer")
    public String restaurer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        offreService.restaurer(id);
        redirectAttributes.addFlashAttribute("success", "Job restored successfully.");
        return "redirect:/recruteur/offres/archived";
    }

    @PostMapping("/recruteur/offres/{id}/delete-permanent")
    public String supprimerDefinitivement(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        offreService.supprimerDefinitivement(id);
        redirectAttributes.addFlashAttribute("success", "Job permanently deleted.");
        return "redirect:/recruteur/offres/archived";
    }

}
