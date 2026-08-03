package com.jobboard.jobboard.module.auth;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.entreprise.Entreprise;
import com.jobboard.jobboard.module.entreprise.EntrepriseRepository;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import com.jobboard.jobboard.module.auth.dto.RegisterRequest;
import com.jobboard.jobboard.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerCandidat(RegisterRequest request) {
        if (candidatRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé.");
        }
        Candidat candidat = new Candidat();
        candidat.setEmail(request.getEmail());
        candidat.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        candidat.setRole(Role.CANDIDAT);
        candidat.setNom(request.getNom());
        candidat.setPrenom(request.getPrenom());
        candidatRepository.save(candidat);
    }

    @Transactional
    public void registerRecruteur(RegisterRequest request) {
        if (recruteurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Créer une entreprise par défaut si pas fournie
        Entreprise entreprise;
        if (request.getEntrepriseId() != null) {
            entreprise = entrepriseRepository.findById(request.getEntrepriseId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found."));
        } else {
            entreprise = new Entreprise();
            entreprise.setNom(request.getNomEntreprise() != null
                    ? request.getNomEntreprise()
                    : "My Company");
            entreprise.setStatut(com.jobboard.jobboard.shared.domain.StatutEntreprise.ACTIVE);
            entreprise = entrepriseRepository.save(entreprise);
        }

        Recruteur recruteur = new Recruteur();
        recruteur.setEmail(request.getEmail());
        recruteur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        recruteur.setRole(Role.RECRUTEUR);
        recruteur.setNom(request.getNom());
        recruteur.setPrenom(request.getPrenom());
        recruteur.setEntreprise(entreprise);
        recruteurRepository.save(recruteur);
    }
}
