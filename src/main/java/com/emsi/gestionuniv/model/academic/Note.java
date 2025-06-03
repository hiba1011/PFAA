package com.emsi.gestionuniv.model.academic;

import com.emsi.gestionuniv.model.user.Student;

public class Note {
    private int id;
    private int etudiantId;
    private int coursId;
    private double noteCC;
    private double noteTP;
    private double noteExamen;
    private double moyenne;
    private Student etudiant;
    private cours cours;

    // Constructeur par défaut
    public Note() {
    }

    // Constructeur avec paramètres
    public Note(int id, int etudiantId, int coursId, double noteCC, double noteTP, double noteExamen) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.coursId = coursId;
        this.noteCC = noteCC;
        this.noteTP = noteTP;
        this.noteExamen = noteExamen;
        calculerMoyenne();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    public int getCoursId() {
        return coursId;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }

    public double getNoteCC() {
        return noteCC;
    }

    public void setNoteCC(double noteCC) {
        this.noteCC = noteCC;
        calculerMoyenne();
    }

    public double getNoteTP() {
        return noteTP;
    }

    public void setNoteTP(double noteTP) {
        this.noteTP = noteTP;
        calculerMoyenne();
    }

    public double getNoteExamen() {
        return noteExamen;
    }

    public void setNoteExamen(double noteExamen) {
        this.noteExamen = noteExamen;
        calculerMoyenne();
    }

    public double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }

    public Student getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Student etudiant) {
        this.etudiant = etudiant;
    }

    public cours getCours() {
        return cours;
    }

    public void setCours(cours cours) {
        this.cours = cours;
    }

    // Méthode pour calculer la moyenne
    private void calculerMoyenne() {
        // Calcul de la moyenne pondérée
        // CC : 30%, TP : 20%, Examen : 50%
        this.moyenne = (noteCC * 0.3) + (noteTP * 0.2) + (noteExamen * 0.5);
    }

    // Méthode pour vérifier si l'étudiant a réussi
    public boolean isReussi() {
        return moyenne >= 10.0;
    }

    // Méthode pour obtenir la mention
    public String getMention() {
        if (moyenne >= 16) return "Très Bien";
        if (moyenne >= 14) return "Bien";
        if (moyenne >= 12) return "Assez Bien";
        if (moyenne >= 10) return "Passable";
        return "Insuffisant";
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", etudiantId=" + etudiantId +
                ", coursId=" + coursId +
                ", noteCC=" + noteCC +
                ", noteTP=" + noteTP +
                ", noteExamen=" + noteExamen +
                ", moyenne=" + moyenne +
                ", etudiant=" + (etudiant != null ? etudiant.getMatricule() : "null") +
                ", cours=" + (cours != null ? cours.getCode() : "null") +
                '}';
    }
}
