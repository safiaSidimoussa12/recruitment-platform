package com.jobboard.jobboard.module.candidature;

import com.jobboard.jobboard.module.candidat.Candidat;
import com.jobboard.jobboard.module.messagerie.Message;
import com.jobboard.jobboard.module.offre.Offre;
import com.jobboard.jobboard.shared.domain.StatutCandidature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "candidature", uniqueConstraints = @UniqueConstraint(columnNames = { "candidat_id", "offre_id" }))
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String lettreMotivation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut = StatutCandidature.APPLIED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateSoumission = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
}
