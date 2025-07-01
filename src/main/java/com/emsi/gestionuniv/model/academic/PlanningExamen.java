package com.emsi.gestionuniv.model.academic;

public class PlanningExamen {
    private int id;
    private String classe;
    private String titre;
    private String cheminPdf;
    private String cible;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getCheminPdf() { return cheminPdf; }
    public void setCheminPdf(String cheminPdf) { this.cheminPdf = cheminPdf; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
}