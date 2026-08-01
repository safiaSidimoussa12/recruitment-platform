package com.jobboard.jobboard.module.admin;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.entreprise.EntrepriseService;
import com.jobboard.jobboard.module.offre.OffreService;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.jobboard.jobboard.shared.domain.StatutCompte;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository adminRepository;
    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;
    private final EntrepriseService entrepriseService;
    private final OffreService offreService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUtilisateurs", adminRepository.count());
        model.addAttribute("totalOffres", offreService.findByRecruteur(null) != null
                ? offreService.findByRecruteur(null).size()
                : 0);
        model.addAttribute("totalEntreprises", entrepriseService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        model.addAttribute("utilisateurs", adminRepository.findAll());
        return "admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/suspendre")
    public String suspendre(@PathVariable Long id) {
        adminRepository.findById(id).ifPresent(u -> {
            u.setStatut(StatutCompte.SUSPENDU);
            adminRepository.save(u);
        });
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/activer")
    public String activer(@PathVariable Long id) {
        adminRepository.findById(id).ifPresent(u -> {
            u.setStatut(StatutCompte.ACTIF);
            adminRepository.save(u);
        });
        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/entreprises")
    public String entreprises(Model model) {
        model.addAttribute("entreprises", entrepriseService.findAll());
        return "admin/entreprises";
    }

    @PostMapping("/entreprises/{id}/suspendre")
    public String suspendrEntreprise(@PathVariable Long id) {
        entrepriseService.suspendre(id);
        return "redirect:/admin/entreprises";
    }
}
