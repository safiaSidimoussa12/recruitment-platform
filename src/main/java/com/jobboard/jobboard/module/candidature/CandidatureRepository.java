package com.jobboard.jobboard.module.candidature;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    boolean existsByCandidatAndOffre(Candidat candidat, Offre offre);

    List<Candidature> findByCandidatId(Long candidatId);

    List<Candidature> findByOffreId(Long offreId);

    List<Candidature> findByOffreIdAndStatut(Long offreId, StatutCandidature statut);

    Optional<Candidature> findByCandidatAndOffre(Candidat candidat, Offre offre);
}
