package com.jobboard.jobboard.module.entreprise;

import com.jobboard.jobboard.shared.domain.StatutEntreprise;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    public Entreprise findById(Long id) {
        return entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable."));
    }

    public List<Entreprise> findAll() {
        return entrepriseRepository.findAll();
    }

    @Transactional
    public Entreprise creer(Entreprise entreprise) {
        if (entrepriseRepository.existsByNom(entreprise.getNom())) {
            throw new IllegalArgumentException("Une entreprise avec ce nom existe déjà.");
        }
        return entrepriseRepository.save(entreprise);
    }

    @Transactional
    public Entreprise modifier(Long id, Entreprise updated) {
        Entreprise entreprise = findById(id);
        entreprise.setNom(updated.getNom());
        entreprise.setDescription(updated.getDescription());
        entreprise.setSecteur(updated.getSecteur());
        entreprise.setSiteWeb(updated.getSiteWeb());
        entreprise.setLocalisation(updated.getLocalisation());
        return entrepriseRepository.save(entreprise);
    }

    @Transactional
    public void suspendre(Long id) {
        Entreprise entreprise = findById(id);
        entreprise.setStatut(StatutEntreprise.SUSPENDUE);
        entrepriseRepository.save(entreprise);
    }
}