package com.jobboard.jobboard.module.offre;

import com.jobboard.jobboard.shared.domain.StatutOffre;
import com.jobboard.jobboard.shared.domain.TypeContrat;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository offreRepository;

    public Page<Offre> search(String keyword, String ville, String domaine,
            TypeContrat typeContrat, Double salaireMin,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePublication").descending());
        return offreRepository.search(keyword, ville, domaine, typeContrat, salaireMin, pageable);
    }

    public Offre findById(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable."));
    }

    public List<Offre> findByRecruteur(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId);
    }

    @Transactional
    public Offre publier(Offre offre) {
        offre.setStatut(StatutOffre.PUBLIEE);
        return offreRepository.save(offre);
    }

    @Transactional
    public Offre modifier(Long id, Offre updated) {
        Offre offre = findById(id);
        offre.setTitre(updated.getTitre());
        offre.setDescription(updated.getDescription());
        offre.setVille(updated.getVille());
        offre.setDomaine(updated.getDomaine());
        offre.setSalaireMin(updated.getSalaireMin());
        offre.setSalaireMax(updated.getSalaireMax());
        offre.setTypeContrat(updated.getTypeContrat());
        offre.setDateCloture(updated.getDateCloture());
        return offreRepository.save(offre);
    }

    @Transactional
    public void supprimer(Long id) {
        Offre offre = findById(id);
        offre.setStatut(StatutOffre.SUPPRIMEE);
        offreRepository.save(offre);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cloturerOffresExpirees() {
        offreRepository.findAll().stream()
                .filter(o -> o.getStatut() == StatutOffre.PUBLIEE
                        && o.getDateCloture() != null
                        && o.getDateCloture().isBefore(LocalDate.now()))
                .forEach(o -> {
                    o.setStatut(StatutOffre.CLOTUREE);
                    offreRepository.save(o);
                });
    }


    @Transactional
public void archiver(Long id) {
    Offre offre = findById(id);
    offre.setStatut(StatutOffre.SUPPRIMEE);
    offreRepository.save(offre);
}

@Transactional
public void restaurer(Long id) {
    Offre offre = findById(id);
    offre.setStatut(StatutOffre.PUBLIEE);
    offreRepository.save(offre);
}

@Transactional
public void supprimerDefinitivement(Long id) {
    offreRepository.deleteById(id);
}

public List<Offre> findArchivedByRecruteur(Long recruteurId) {
    return offreRepository.findByRecruteurId(recruteurId).stream()
        .filter(o -> o.getStatut() == StatutOffre.SUPPRIMEE)
        .toList();
}

public List<Offre> findActiveByRecruteur(Long recruteurId) {
    return offreRepository.findByRecruteurId(recruteurId).stream()
        .filter(o -> o.getStatut() != StatutOffre.SUPPRIMEE)
        .toList();
}
}