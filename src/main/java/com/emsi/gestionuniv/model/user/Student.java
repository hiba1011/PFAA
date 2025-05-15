/**
 * Package contenant les modèles liés aux utilisateurs du système de gestion universitaire
 */
package com.emsi.gestionuniv.model.user;

/**
 * Représente un étudiant dans le système.
 * Cette classe hérite de la classe User et ajoute des attributs spécifiques aux étudiants.
 */
public class Student extends User {
    /** Promotion à laquelle appartient l'étudiant (ex: "2024-2025") */
    private String promotion;

    /** Filière d'études suivie par l'étudiant (ex: "Informatique", "Commerce", etc.) */
    private String filiere;

    //Constructeur
    public Student(int id, String username, String password, String nom, String prenom,
                   String email, String promotion, String filiere) {
        // Appel du constructeur de la classe parente avec le rôle "STUDENT" prédéfini
        super(id, password, nom, prenom, email, "STUDENT");
        this.promotion = promotion;
        this.filiere = filiere;
    }

    // Getters and Setters
    /**
     * Récupère la promotion de l'étudiant
     * @return La promotion de l'étudiant
     */
    public String getPromotion() {
        return promotion;
    }

    /**
     * Définit la promotion de l'étudiant
     * @param promotion Nouvelle promotion
     */
    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    /**
     * Récupère la filière d'études de l'étudiant
     * @return La filière de l'étudiant
     */
    public String getFiliere() {
        return filiere;
    }

    /**
     * Définit la filière d'études de l'étudiant
     * @param filiere Nouvelle filière
     */
    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    /**
     * Redéfinition de la méthode getDashboardTitle de la classe User
     * Génère un titre personnalisé pour le tableau de bord de l'étudiant
     *
     * @return Le titre du tableau de bord avec le prénom et nom de l'étudiant
     */
    @Override
    public String getDashboardTitle() {
        return "Tableau de bord Étudiant - " + getPrenom() + " " + getNom();
    }
}