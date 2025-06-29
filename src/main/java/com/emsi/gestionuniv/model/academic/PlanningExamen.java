package com.emsi.gestionuniv.model.academic;

public class PlanningExamen {
    private int id;
    private String classe;
    private String titre;
    private String cheminPdf;
    private String type;      // "etudiant" ou "enseignant"
    private String cible;     // nom de la classe ou id de l'enseignant

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getCheminPdf() { return cheminPdf; }
    public void setCheminPdf(String cheminPdf) { this.cheminPdf = cheminPdf; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
}