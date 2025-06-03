package com.emsi.gestionuniv.model.academic;

public class cours {
    private int id;
    private String code;
    private String intitule;
    private String filiere;
    private String niveau;
    private int effectif;
    private String volumeHoraire;
    private int enseignantId;

    // Constructeur par défaut
    public cours() {
    }

    // Constructeur avec paramètres
    public cours(int id, String code, String intitule, String filiere, String niveau, int effectif, String volumeHoraire, int enseignantId) {
        this.id = id;
        this.code = code;
        this.intitule = intitule;
        this.filiere = filiere;
        this.niveau = niveau;
        this.effectif = effectif;
        this.volumeHoraire = volumeHoraire;
        this.enseignantId = enseignantId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public int getEffectif() {
        return effectif;
    }

    public void setEffectif(int effectif) {
        this.effectif = effectif;
    }

    public String getVolumeHoraire() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(String volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }

    public int getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(int enseignantId) {
        this.enseignantId = enseignantId;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", intitule='" + intitule + '\'' +
                ", filiere='" + filiere + '\'' +
                ", niveau='" + niveau + '\'' +
                ", effectif=" + effectif +
                ", volumeHoraire='" + volumeHoraire + '\'' +
                ", enseignantId=" + enseignantId +
                '}';
    }

    // Méthode pour obtenir le titre complet du cours
    public String getTitre() {
        return code + " - " + intitule;
    }

    // Méthode pour obtenir le programme complet
    public String getProgramme() {
        return filiere + " - " + niveau;
    }

    // Méthode pour obtenir le nombre d'étudiants
    public int getNombreEtudiants() {
        return effectif;
    }
}
