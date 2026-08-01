package com.jobboard.jobboard.module.offre;

import com.jobboard.jobboard.shared.domain.StatutOffre;
import com.jobboard.jobboard.shared.domain.TypeContrat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {

    Page<Offre> findByStatut(StatutOffre statut, Pageable pageable);

    @Query("""
                SELECT o FROM Offre o
                WHERE o.statut = 'PUBLIEE'
                AND (:keyword IS NULL OR LOWER(o.titre) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:ville IS NULL OR LOWER(o.ville) LIKE LOWER(CONCAT('%', :ville, '%')))
                AND (:domaine IS NULL OR LOWER(o.domaine) LIKE LOWER(CONCAT('%', :domaine, '%')))
                AND (:typeContrat IS NULL OR o.typeContrat = :typeContrat)
                AND (:salaireMin IS NULL OR o.salaireMin >= :salaireMin)
            """)
    Page<Offre> search(
            @Param("keyword") String keyword,
            @Param("ville") String ville,
            @Param("domaine") String domaine,
            @Param("typeContrat") TypeContrat typeContrat,
            @Param("salaireMin") Double salaireMin,
            Pageable pageable);

    List<Offre> findByEntrepriseId(Long entrepriseId);

    List<Offre> findByRecruteurId(Long recruteurId);
}
