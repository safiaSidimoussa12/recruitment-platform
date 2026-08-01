package com.jobboard.jobboard.module.candidat;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidatService {

    private final CandidatRepository candidatRepository;
    private final CVRepository cvRepository;

    private static final String UPLOAD_DIR = "uploads/cvs/";

    public Candidat findById(Long id) {
        return candidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable."));
    }

    @Transactional
    public void updateProfil(Long id, Candidat updated) {
        Candidat candidat = findById(id);
        candidat.setNom(updated.getNom());
        candidat.setPrenom(updated.getPrenom());
        candidat.setTelephone(updated.getTelephone());
        candidat.setVille(updated.getVille());
        candidatRepository.save(candidat);
    }

    @Transactional
    public void uploadCV(Long candidatId, MultipartFile file) throws IOException {
        Candidat candidat = findById(candidatId);

        // Supprimer l'ancien CV si existant
        cvRepository.findByCandidat(candidat).ifPresent(cv -> {
            try {
                Files.deleteIfExists(Paths.get(cv.getFichierUrl()));
            } catch (IOException ignored) {
            }
            cvRepository.delete(cv);
        });

        // Sauvegarder le nouveau fichier
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        CV cv = new CV();
        cv.setFichierUrl(path.toString());
        cv.setCandidat(candidat);
        cvRepository.save(cv);
    }

    public CV getCVByCandidat(Long candidatId) {
        Candidat candidat = findById(candidatId);
        return cvRepository.findByCandidat(candidat)
                .orElseThrow(() -> new ResourceNotFoundException("CV introuvable."));
    }
}
