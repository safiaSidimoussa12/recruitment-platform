package com.jobboard.jobboard.module.messagerie;

import com.jobboard.jobboard.module.candidature.Candidature;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean lu = false;

    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;
}
