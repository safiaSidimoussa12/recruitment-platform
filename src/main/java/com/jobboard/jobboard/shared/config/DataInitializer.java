package com.jobboard.jobboard.shared.config;

import com.jobboard.jobboard.shared.domain.Role;
import com.jobboard.jobboard.shared.domain.StatutCompte;
import com.jobboard.jobboard.shared.domain.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final CandidatRepository candidatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crée le compte admin s'il n'existe pas
        if (utilisateurRepository.findByEmail("admin@careerbridge.com").isEmpty()) {
            Candidat admin = new Candidat();
            admin.setEmail("admin@careerbridge.com");
            admin.setMotDePasse(passwordEncoder.encode("Admin@2026"));
            admin.setRole(Role.ADMIN);
            admin.setStatut(StatutCompte.ACTIF);
            admin.setNom("Admin");
            admin.setPrenom("CareerBridge");
            candidatRepository.save(admin);
            System.out.println("=== Admin account created ===");
        }
    }
}
