USE jobboard_db;
SELECT * FROM  utilisateur;

SELECT * FROM  entreprise;
SELECT * FROM recruteur;



ALTER TABLE candidature 
MODIFY COLUMN statut ENUM(
    'APPLIED',
    'REVIEWED',
    'SHORTLISTED',
    'INTERVIEW_SCHEDULED',
    'INTERVIEW_COMPLETED',
    'HIRED',
    'REJECTED'
) NOT NULL DEFAULT 'APPLIED';




USE jobboard_db;
SELECT * FROM  utilisateur;

SELECT * FROM  entreprise;
SELECT * FROM recruteur;



ALTER TABLE candidature 
MODIFY COLUMN statut ENUM(
    'APPLIED',
    'REVIEWED',
    'SHORTLISTED',
    'INTERVIEW_SCHEDULED',
    'INTERVIEW_COMPLETED',
    'HIRED',
    'REJECTED'
) NOT NULL DEFAULT 'APPLIED';

SELECT id, email, role, statut FROM utilisateur WHERE role = 'ADMIN';

SELECT id, email, role, mot_de_passe FROM utilisateur WHERE role = 'ADMIN';




-- Insérer l'utilisateur admin
INSERT INTO utilisateur (email, mot_de_passe, role, statut, date_creation)
VALUES (
    'admin@jobboard.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RD6771k6v2uqXSymy',
    'ADMIN',
    'ACTIF',
    NOW()
);



SET SQL_SAFE_UPDATES = 0;
DELETE FROM utilisateur WHERE role = 'ADMIN';
SET SQL_SAFE_UPDATES = 1;


UPDATE utilisateur 
SET role = 'ADMIN' 
WHERE email = 'admin@jobboard.com';


SELECT u.email, u.role, u.statut, r.nom 
FROM utilisateur u 
JOIN recruteur r ON u.id = r.id 
WHERE u.role = 'RECRUTEUR';




SELECT u.email, u.role, u.statut 
FROM utilisateur u 
WHERE u.email = 'sssss@gmail.com';


SELECT u.email, u.role, u.statut 


SHOW COLUMNS FROM utilisateur LIKE 'statut';
FROM utilisateur u 
WHERE u.role = 'RECRUTEUR';




ALTER TABLE utilisateur 
MODIFY COLUMN statut ENUM('ACTIF', 'SUSPENDU', 'EN_ATTENTE') NOT NULL DEFAULT 'ACTIF';


SELECT u.email, u.role, u.statut 
FROM utilisateur u 
WHERE u.role = 'RECRUTEUR';


SELECT e.nom, e.statut 
FROM entreprise e 
JOIN recruteur r ON r.entreprise_id = e.id
JOIN utilisateur u ON u.id = r.id
WHERE u.statut = 'SUSPENDU';

select * from recruteur




ALTER TABLE recruteur MODIFY COLUMN entreprise_id BIGINT NULL;