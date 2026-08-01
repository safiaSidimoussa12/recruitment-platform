package com.jobboard.jobboard.module.entreprise;

import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.shared.domain.StatutEntreprise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "entreprise")
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String secteur;
    private String logoUrl;
    private String siteWeb;
    private String localisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEntreprise statut = StatutEntreprise.ACTIVE;

    @OneToMany(mappedBy = "entreprise")
    private List<Recruteur> recruteurs = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<Offre> offres = new ArrayList<>();
}