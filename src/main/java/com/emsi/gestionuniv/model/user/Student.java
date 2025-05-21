package com.emsi.gestionuniv.model.user;

public class Student extends User {
    private String matricule;
    private String filiere;
    private String promotion;
    private String photo;

    public Student(int id, String matricule, String prenom, String nom, String email, String filiere, String promotion, String photo) {
        super(id, "", nom, prenom, email, "STUDENT"); // On passe un mot de passe vide et le rôle "STUDENT"
        this.matricule = matricule;
        this.filiere = filiere;
        this.promotion = promotion;
        this.photo = photo;
    }

    public Student() {
        super(0, "", "", "", "", "STUDENT"); // Appel au constructeur de User avec des valeurs par défaut
        this.matricule = "";
        this.filiere = "";
        this.promotion = "";
        this.photo = "";
    }

    // Getters et Setters
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String getDashboardTitle() {
        return "Bienvenue sur le tableau de bord étudiant, " + getPrenom() + " !";
    }
}
