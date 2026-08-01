package com.jobboard.jobboard.module.favori;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.offre.Offre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "favori", uniqueConstraints = @UniqueConstraint(columnNames = { "candidat_id", "offre_id" }))
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateAjout = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;
}
