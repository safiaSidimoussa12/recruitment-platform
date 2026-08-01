package com.jobboard.jobboard.module.admin;

import com.jobboard.jobboard.shared.domain.Utilisateur;
import com.jobboard.jobboard.shared.domain.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdminRepository extends JpaRepository<Utilisateur, Long> {
    List<Utilisateur> findByStatut(StatutCompte statut);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.statut = 'ACTIF'")
    long countUtilisateursActifs();
}
