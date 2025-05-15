/**
 * Package contenant les modèles liés aux utilisateurs du système de gestion universitaire
 */
package com.emsi.gestionuniv.model.user;

/**
 * Classe abstraite représentant un utilisateur générique dans le système.
 * Cette classe sert de base pour tous les types d'utilisateurs spécifiques (Student, Teacher, etc.)
 * et définit les attributs et comportements communs à tous les utilisateurs.
 */
public abstract class User {
    /** Identifiant unique de l'utilisateur dans le système */
    private int id;

    /** Mot de passe de l'utilisateur pour l'authentification */
    private String password;

    /** Nom de famille de l'utilisateur */
    private String nom;

    /** Prénom de l'utilisateur */
    private String prenom;

    /** Adresse email de l'utilisateur, utilisée comme identifiant de connexion */
    private String email;

    /** Rôle de l'utilisateur dans le système (ex: "STUDENT", "TEACHER", "ADMIN") */
    private String role;

    /**
     * Constructeur pour créer un nouvel utilisateur avec tous les attributs nécessaires
     *
     * @param id Identifiant unique de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param nom Nom de famille de l'utilisateur
     * @param prenom Prénom de l'utilisateur
     * @param email Adresse email de l'utilisateur
     * @param role Rôle de l'utilisateur dans le système
     */
    public User(int id, String password, String nom, String prenom, String email, String role) {
        this.id = id;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    /**
     * Récupère l'identifiant unique de l'utilisateur
     * @return L'identifiant de l'utilisateur
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur
     * @param id Nouvel identifiant
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Récupère le mot de passe de l'utilisateur
     * @return Le mot de passe de l'utilisateur
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe de l'utilisateur
     * @param password Nouveau mot de passe
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Récupère le nom de famille de l'utilisateur
     * @return Le nom de famille de l'utilisateur
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de famille de l'utilisateur
     * @param nom Nouveau nom de famille
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Récupère le prénom de l'utilisateur
     * @return Le prénom de l'utilisateur
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom de l'utilisateur
     * @param prenom Nouveau prénom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Récupère l'adresse email de l'utilisateur
     * @return L'adresse email de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'adresse email de l'utilisateur
     * @param email Nouvelle adresse email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Récupère le rôle de l'utilisateur dans le système
     * @return Le rôle de l'utilisateur
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur dans le système
     * @param role Nouveau rôle
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Méthode abstraite qui doit être implémentée par toutes les classes dérivées.
     * Retourne le titre du tableau de bord spécifique à chaque type d'utilisateur.
     *
     * @return Le titre personnalisé du tableau de bord en fonction du type d'utilisateur
     */
    public abstract String getDashboardTitle();
}