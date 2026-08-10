package com.jobboard.jobboard.shared.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    // @Query(value = "SELECT id, email, role, statut, date_creation FROM
    // utilisateur", nativeQuery = true)
    // List<UtilisateurView> findAllAsView();

    @Query(value = "SELECT id, email, role, statut, date_creation FROM utilisateur WHERE role != 'ADMIN'", nativeQuery = true)
    List<UtilisateurView> findAllAsView();
}