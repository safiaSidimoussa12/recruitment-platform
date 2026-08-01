package com.jobboard.jobboard.module.candidat;

import com.jobboard.jobboard.shared.domain.Utilisateur;
import com.jobboard.jobboard.module.candidature.Candidature;
import com.jobboard.jobboard.module.favori.Favori;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "candidat")
@PrimaryKeyJoinColumn(name = "id")
public class Candidat extends Utilisateur {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String telephone;
    private String ville;
    private String photoUrl;

    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL, orphanRemoval = true)
    private CV cv;

    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Candidature> candidatures = new ArrayList<>();

    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Favori> favoris = new ArrayList<>();
}