package com.jobboard.jobboard.module.admin;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.entreprise.Entreprise;
import com.jobboard.jobboard.module.entreprise.EntrepriseService;
import com.jobboard.jobboard.module.offre.OffreService;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.jobboard.jobboard.shared.domain.StatutOffre;

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
    private final com.jobboard.jobboard.shared.domain.UtilisateurRepository utilisateurRepository;
    private final com.jobboard.jobboard.module.entreprise.EntrepriseRepository entrepriseRepository;
    private final com.jobboard.jobboard.module.offre.OffreRepository offreRepository;

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAllAsView());
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
            Entreprise entreprise = r.getEntreprise();

            // Vérifie si l'entreprise a d'autres recruteurs actifs
            boolean hasActiveRecruiters = entreprise.getRecruteurs().stream()
                    .anyMatch(rec -> !rec.getId().equals(r.getId())
                            && rec.getStatut() == StatutCompte.ACTIF);

            // Suspend le recruteur
            r.setStatut(StatutCompte.SUSPENDU);
            if (!hasActiveRecruiters) {
                r.setEntreprise(null); // dissocier avant suppression
            }
            recruteurRepository.save(r);

            // Supprime l'entreprise si pas d'autres recruteurs actifs
            if (!hasActiveRecruiters) {
                entrepriseRepository.delete(entreprise);
            }
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
        model.addAttribute("totalOffres", offreRepository.countByStatut(StatutOffre.PUBLIEE));
        model.addAttribute("pendingRecruteurs",
                recruteurRepository.findByStatut(StatutCompte.EN_ATTENTE).size());
        return "admin/dashboard";
    }
}
