package com.jobboard.jobboard.module.offre;

import com.jobboard.jobboard.module.entreprise.Entreprise;
import com.jobboard.jobboard.module.recruteur.Recruteur;
import com.jobboard.jobboard.module.candidature.Candidature;
import com.jobboard.jobboard.shared.domain.StatutOffre;
import com.jobboard.jobboard.shared.domain.TypeContrat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "offre")
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String ville;
    private String domaine;
    private Double salaireMin;
    private Double salaireMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeContrat typeContrat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutOffre statut = StatutOffre.PUBLIEE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime datePublication = LocalDateTime.now();

    private LocalDate dateCloture;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteur;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)
    private List<Candidature> candidatures = new ArrayList<>();
}
