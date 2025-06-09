package com.emsi.gestionuniv.model.academic;

public class Note {
    private int id;
    private int etudiantId;
    private int coursId;
    private double noteCC;
    private double noteTP;
    private double noteExamen;
    private double moyenne;
    private String matiere;
    private double controleContinu;
    private double examen;
    private double tp;
    private double noteFinale;
    private String validation;
    private String nomEtudiant; // Ajoute cet attribut
    private String prenomEtudiant; // Ajoute cet attribut

    // Constructeur par défaut
    public Note() {
    }

    // Constructeur avec paramètres
    public Note(String matiere, double controleContinu, double examen, double tp) {
        this.matiere = matiere;
        this.controleContinu = controleContinu;
        this.examen = examen;
        this.tp = tp;
        calculerNoteFinale();
        valider();
    }

    // Getters et setters
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
    }

    public double getNoteTP() {
        return noteTP;
    }

    public void setNoteTP(double noteTP) {
        this.noteTP = noteTP;
    }

    public double getNoteExamen() {
        return noteExamen;
    }

    public void setNoteExamen(double noteExamen) {
        this.noteExamen = noteExamen;
    }

    public double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }

    // Pour affichage des notes dans l'onglet étudiant
    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public double getControleContinu() {
        return controleContinu;
    }

    public void setControleContinu(double controleContinu) {
        this.controleContinu = controleContinu;
        calculerNoteFinale();
    }

    public double getExamen() {
        return examen;
    }

    public void setExamen(double examen) {
        this.examen = examen;
        calculerNoteFinale();
    }

    public double getTp() {
        return tp;
    }

    public void setTp(double tp) {
        this.tp = tp;
        calculerNoteFinale();
    }

    public double getNoteFinale() {
        return noteFinale;
    }

    public void setNoteFinale(double noteFinale) {
        this.noteFinale = noteFinale;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public void setNomEtudiant(String nomEtudiant) {
        this.nomEtudiant = nomEtudiant;
    }

    public String getPrenomEtudiant() {
        return prenomEtudiant;
    }

    public void setPrenomEtudiant(String prenomEtudiant) {
        this.prenomEtudiant = prenomEtudiant;
    }

    // Méthode pour calculer la note finale
    private void calculerNoteFinale() {
        // Calcul de la note finale
        // CC : 30%, TP : 20%, Examen : 50%
        this.noteFinale = (controleContinu * 0.3) + (tp * 0.2) + (examen * 0.5);
    }

    // Méthode pour valider la note
    private void valider() {
        this.validation = noteFinale >= 10.0 ? "Validé" : "Non Validé";
    }

    @Override
    public String toString() {
        return "Note{" +
                "matiere='" + matiere + '\'' +
                ", controleContinu=" + controleContinu +
                ", examen=" + examen +
                ", tp=" + tp +
                ", noteFinale=" + noteFinale +
                ", validation='" + validation + '\'' +
                '}';
    }
}
