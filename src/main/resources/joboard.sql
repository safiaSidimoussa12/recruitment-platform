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