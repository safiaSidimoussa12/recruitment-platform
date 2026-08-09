package com.jobboard.jobboard.module.admin;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.entreprise.EntrepriseService;
import com.jobboard.jobboard.module.offre.OffreService;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/recruteurs/{id}/approuver")
    public String approuver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        recruteurRepository.findById(id).ifPresent(r -> {
            r.setStatut(StatutCompte.ACTIF);
            recruteurRepository.save(r);
        });
        redirectAttributes.addFlashAttribute("success", "Recruiter approved successfully.");
        return "redirect:/admin/recruteurs/pending";
    }

    @PostMapping("/recruteurs/{id}/rejeter")
    public String rejeter(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        recruteurRepository.findById(id).ifPresent(r -> {
            r.setStatut(StatutCompte.SUSPENDU);
            recruteurRepository.save(r);
        });
        redirectAttributes.addFlashAttribute("success", "Recruiter rejected.");
        return "redirect:/admin/recruteurs/pending";
    }

    @GetMapping("/recruteurs/pending")
    public String pendingRecruteurs(Model model) {
        model.addAttribute("recruteurs",
                recruteurRepository.findByStatut(StatutCompte.EN_ATTENTE));
        return "admin/pending-recruteurs";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUtilisateurs", adminRepository.count());
        model.addAttribute("totalEntreprises", entrepriseService.findAll().size());
        model.addAttribute("pendingRecruteurs",
                recruteurRepository.findByStatut(StatutCompte.EN_ATTENTE).size());
        return "admin/dashboard";
    }
}
