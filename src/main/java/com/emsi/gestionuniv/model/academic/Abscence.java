package com.emsi.gestionuniv.model.academic;

import com.emsi.gestionuniv.model.user.Student;

import java.sql.Date;

public class Abscence {
    private int id;
    private int etudiantId;
    private int coursId;
    private Date date;
    private boolean justifiee;
    private byte[] justification;
    private Student etudiant;

    // Constructeur par défaut
    public Abscence() {
    }

    // Constructeur avec paramètres
    public Abscence(int id, int etudiantId, int coursId, Date date, boolean justifiee, byte[] justification) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.coursId = coursId;
        this.date = date;
        this.justifiee = justifiee;
        this.justification = justification;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isJustifiee() {
        return justifiee;
    }

    public void setJustifiee(boolean justifiee) {
        this.justifiee = justifiee;
    }

    public byte[] getJustification() {
        return justification;
    }

    public void setJustification(byte[] justification) {
        this.justification = justification;
    }

    public Student getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Student etudiant) {
        this.etudiant = etudiant;
    }

    @Override
    public String toString() {
        return "Abscence{" +
                "id=" + id +
                ", etudiantId=" + etudiantId +
                ", coursId=" + coursId +
                ", date=" + date +
                ", justifiee=" + justifiee +
                ", justification='" + justification + '\'' +
                ", etudiant=" + (etudiant != null ? etudiant.getMatricule() : "null") +
                '}';
    }
}