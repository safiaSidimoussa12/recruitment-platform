package com.jobboard.jobboard.module.candidat;

import com.jobboard.jobboard.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

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

        // Cherche le CV existant
        Optional<CV> existingCV = cvRepository.findByCandidat(candidat);

        // Sauvegarde le nouveau fichier
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        if (existingCV.isPresent()) {
            // Supprimer l'ancien fichier physique
            try {
                Files.deleteIfExists(Paths.get(existingCV.get().getFichierUrl()));
            } catch (IOException ignored) {
            }

            // Mettre à jour l'enregistrement existant
            CV cv = existingCV.get();
            cv.setFichierUrl(filePath.toString());
            cv.setDateUpload(LocalDateTime.now());
            cvRepository.save(cv);
        } else {
            // Créer un nouvel enregistrement
            CV cv = new CV();
            cv.setFichierUrl(filePath.toString());
            cv.setCandidat(candidat);
            cvRepository.save(cv);
        }
    }

    public CV getCVByCandidat(Long candidatId) {
        Candidat candidat = findById(candidatId);
        return cvRepository.findByCandidat(candidat)
                .orElseThrow(() -> new ResourceNotFoundException("CV introuvable."));
    }
}
