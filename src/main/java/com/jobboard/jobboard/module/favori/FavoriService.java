package com.jobboard.jobboard.module.favori;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.module.offre.OffreRepository;
import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriService {

    private final FavoriRepository favoriRepository;
    private final CandidatRepository candidatRepository;
    private final OffreRepository offreRepository;

    @Transactional
    public void toggleFavori(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable."));

        if (favoriRepository.existsByCandidatAndOffre(candidat, offre)) {
            favoriRepository.deleteByCandidatAndOffre(candidat, offre);
        } else {
            Favori favori = new Favori();
            favori.setCandidat(candidat);
            favori.setOffre(offre);
            favoriRepository.save(favori);
        }
    }

    public List<Favori> findByCandidat(Long candidatId) {
        return favoriRepository.findByCandidatId(candidatId);
    }
}
