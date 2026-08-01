package com.jobboard.jobboard.module.favori;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.offre.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByCandidatId(Long candidatId);

    Optional<Favori> findByCandidatAndOffre(Candidat candidat, Offre offre);

    boolean existsByCandidatAndOffre(Candidat candidat, Offre offre);

    void deleteByCandidatAndOffre(Candidat candidat, Offre offre);
}
