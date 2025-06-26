package com.emsi.gestionuniv.model.academic;

public class CoursPdf {
    private int id;
    private String titre;
    private String cheminPdf;
    private int enseignantId;
    private String classe;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getCheminPdf() { return cheminPdf; }
    public void setCheminPdf(String cheminPdf) { this.cheminPdf = cheminPdf; }

    public int getEnseignantId() { return enseignantId; }
    public void setEnseignantId(int enseignantId) { this.enseignantId = enseignantId; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
}