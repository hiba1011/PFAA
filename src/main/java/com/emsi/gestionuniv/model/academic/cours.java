package com.emsi.gestionuniv.model.academic;

import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.service.TeacherService;

public class cours {
    private int id;
    private String titre;
    private String specialite;
    private String code;
    private String intitule;
    private String filiere;
    private String niveau;
    private int effectif;
    private String volumeHoraire;
    private int credits;
    private int enseignantId;
    private String description;

    // Constructeur par défaut
    public cours() {
    }

    // Constructeur avec paramètres
    public cours(int id, String titre, String specialite, String code, String intitule, String filiere, String niveau,
            int effectif,
            String volumeHoraire) {
        this.id = id;
        this.titre = titre;
        this.specialite = specialite;
        this.code = code;
        this.intitule = intitule;
        this.filiere = filiere;
        this.niveau = niveau;
        this.effectif = effectif;
        this.volumeHoraire = volumeHoraire;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(int enseignantId) {
        this.enseignantId = enseignantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", specialite='" + specialite + '\'' +
                ", code='" + code + '\'' +
                ", intitule='" + intitule + '\'' +
                ", filiere='" + filiere + '\'' +
                ", niveau='" + niveau + '\'' +
                ", effectif=" + effectif +
                ", volumeHoraire='" + volumeHoraire + '\'' +
                '}';
    }

    public String getEnseignantFullName() {
        TeacherService teacherService = new TeacherService();
        Teacher teacher = teacherService.findTeacherById(this.enseignantId);
        if (teacher != null) {
            return teacher.getFullName();
        }
        return "";
    }
}
