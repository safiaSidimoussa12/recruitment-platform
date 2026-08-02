INSERT IGNORE INTO entreprise (id, nom, description, secteur, localisation, site_web, statut)
VALUES (1, 'TechCorp', 'Entreprise de technologie', 'Informatique', 'Alger', 'https://techcorp.dz', 'ACTIVE');

INSERT IGNORE INTO utilisateur (id, email, mot_de_passe, role, statut, date_creation)
VALUES (1, 'candidat@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RD6771k6v2uqXSymy', 'CANDIDAT', 'ACTIF', NOW());

INSERT IGNORE INTO candidat (id, nom, prenom, telephone, ville)
VALUES (1, 'Benali', 'Sara', '0555123456', 'Alger');

INSERT IGNORE INTO utilisateur (id, email, mot_de_passe, role, statut, date_creation)
VALUES (2, 'recruteur@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RD6771k6v2uqXSymy', 'RECRUTEUR', 'ACTIF', NOW());

INSERT IGNORE INTO recruteur (id, nom, prenom, entreprise_id)
VALUES (2, 'Mansouri', 'Karim', 1);

INSERT IGNORE INTO utilisateur (id, email, mot_de_passe, role, statut, date_creation)
VALUES (3, 'admin@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RD6771k6v2uqXSymy', 'ADMIN', 'ACTIF', NOW());

INSERT IGNORE INTO offre (id, titre, description, ville, domaine, salaire_min, salaire_max, type_contrat, statut, date_publication, entreprise_id, recruteur_id)
VALUES (1, 'Développeur Spring Boot', 'Poste de développeur backend Java/Spring Boot', 'Alger', 'Informatique', 80000, 120000, 'CDI', 'PUBLIEE', NOW(), 1, 2);

INSERT IGNORE INTO offre (id, titre, description, ville, domaine, salaire_min, salaire_max, type_contrat, statut, date_publication, entreprise_id, recruteur_id)
VALUES (2, 'Développeur Android', 'Poste de développeur mobile Android', 'Oran', 'Informatique', 70000, 100000, 'CDI', 'PUBLIEE', NOW(), 1, 2);

INSERT IGNORE INTO offre (id, titre, description, ville, domaine, salaire_min, salaire_max, type_contrat, statut, date_publication, entreprise_id, recruteur_id)
VALUES (3, 'Stage DevOps', 'Stage de 6 mois en DevOps', 'Alger', 'Infrastructure', 30000, 40000, 'STAGE', 'PUBLIEE', NOW(), 1, 2);