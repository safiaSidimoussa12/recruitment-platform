package com.jobboard.jobboard.module.candidat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cv")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fichierUrl;

    @Column(nullable = false)
    private LocalDateTime dateUpload = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "candidat_id", nullable = false, unique = true)
    private Candidat candidat;
}
