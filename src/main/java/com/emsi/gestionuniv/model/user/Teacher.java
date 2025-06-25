/**
 * Package contenant les modèles liés aux utilisateurs du système de gestion universitaire
 */
package com.emsi.gestionuniv.model.user;

/**
 * Représente un enseignant dans le système.
 * Cette classe hérite de la classe User et ajoute des attributs spécifiques aux enseignants.
 */
public class Teacher extends User {
    /** Département auquel appartient l'enseignant (ex: "Informatique", "Mathématiques", etc.) */
    private String departement;

    /** Spécialité ou domaine d'expertise de l'enseignant */
    private String specialite;

    /** Numéro de téléphone de l'enseignant */
    private String telephone;

    /**
     * Constructeur complet pour créer un nouvel enseignant
     *
     * @param id Identifiant unique de l'enseignant
     * @param nom Nom de famille de l'enseignant
     * @param prenom Prénom de l'enseignant
     * @param email Adresse email de l'enseignant
     * @param password Mot de passe de l'enseignant
     * @param departement Département auquel appartient l'enseignant
     * @param specialite Spécialité ou domaine d'expertise de l'enseignant
     * @param telephone Numéro de téléphone de l'enseignant
     */
    public Teacher(int id, String nom, String prenom, String email, String password,
                   String departement, String specialite, String telephone) {
        // Appel du constructeur de la classe parente avec le rôle "TEACHER" prédéfini
        super(id, password, nom, prenom, email, "TEACHER");
        this.departement = departement;
        this.specialite = specialite;
        this.telephone = telephone;
    }

    /**
     * Constructeur par défaut
     */
    public Teacher() {
        super(0, "", "", "", "", "TEACHER");
    }

    // Getters and Setters

    /**
     * Récupère le numéro de téléphone de l'enseignant
     * @return Le numéro de téléphone de l'enseignant
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Définit le numéro de téléphone de l'enseignant
     * @param telephone Nouveau numéro de téléphone
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Récupère le département de l'enseignant
     * @return Le département de l'enseignant
     */
    public String getDepartement() {
        return departement;
    }

    /**
     * Définit le département de l'enseignant
     * @param departement Nouveau département
     */
    public void setDepartement(String departement) {
        this.departement = departement;
    }

    /**
     * Récupère la spécialité de l'enseignant
     * @return La spécialité de l'enseignant
     */
    public String getSpecialite() {
        return specialite;
    }

    /**
     * Définit la spécialité de l'enseignant
     * @param specialite Nouvelle spécialité
     */
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    /**
     * Redéfinition de la méthode getDashboardTitle de la classe User
     * Génère un titre personnalisé pour le tableau de bord de l'enseignant
     *
     * @return Le titre du tableau de bord avec le prénom et nom de l'enseignant
     */
    @Override
    public String getDashboardTitle() {
        return "Tableau de bord Enseignant - " + getPrenom() + " " + getNom();
    }

    /**
     * Retourne le nom complet de l'enseignant (prénom suivi du nom)
     * Cette méthode facilite l'affichage du nom complet dans l'interface utilisateur
     *
     * @return Le nom complet de l'enseignant au format "Prénom Nom"
     */
    public String getFullName() {
        return getPrenom() + " " + getNom();
    }
}