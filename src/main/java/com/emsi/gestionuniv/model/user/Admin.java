/**
 * Classe Admin représentant un utilisateur administrateur dans le système de gestion universitaire.
 * Cette classe hérite de la classe User et ajoute des attributs spécifiques aux administrateurs.
 *
 * @see User
 */
package com.emsi.gestionuniv.model.user;

public class Admin extends User {
    // Le service auquel l'administrateur est rattaché (ex: Scolarité, Ressources Humaines, etc.)
    private String service;

    /** Le niveau de privilège/responsabilité de l'administrateur dans le système */
    private String niveau;


     // Constructeur complet pour créer un objet Admin avec toutes ses propriétés.

    public Admin(int id, String username, String password, String nom, String prenom,
                 String email, String service, String niveau) {
        // Appel du constructeur parent avec le rôle "ADMIN" fixe
        super(id, password, nom, prenom, email, "ADMIN");
        // Initialisation des attributs spécifiques à l'administrateur
        this.service = service;
        this.niveau = niveau;
    }

    // Accesseurs et mutateurs (Getters and Setters)
    public String getService() {
        return service;
    }


    public void setService(String service) {
        this.service = service;
    }


    public String getNiveau() {
        return niveau;
    }


    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    /**
     * Redéfinition de la méthode de la classe parente pour obtenir le titre du tableau de bord.
     * Cette méthode est utilisée pour personnaliser l'interface utilisateur en fonction du type d'utilisateur.
     *
     * @return Le titre formaté pour le tableau de bord de l'administrateur
     * @see User#getDashboardTitle()
     */
    @Override
    public String getDashboardTitle() {
        // Retourne un titre personnalisé incluant le prénom et le nom de l'administrateur
        return "Tableau de bord Administrateur - " + getPrenom() + " " + getNom();
    }
}