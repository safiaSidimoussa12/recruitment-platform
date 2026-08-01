package com.jobboard.jobboard.module.recruteur;

import com.jobboard.jobboard.module.entreprise.Entreprise;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recruteur")
@PrimaryKeyJoinColumn(name = "id")
public class Recruteur extends Utilisateur {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String telephone;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @OneToMany(mappedBy = "recruteur")
    private List<Offre> offres = new ArrayList<>();
}
