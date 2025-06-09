package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.planning.Emploi_de_temps;
import com.emsi.gestionuniv.config.DBConnect;
import java.sql.*;
import java.util.*;

public class EmploiDuTempsService {

    public List<Emploi_de_temps> getEmploiDuTempsParClasse(String classe) {
        List<Emploi_de_temps> liste = new ArrayList<>();
        String sql = "SELECT id, jour_semaine, heure_debut, heure_fin, matiere, salle, enseignant, groupe " +
                "FROM emploi_etu WHERE groupe = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Emploi_de_temps edt = new Emploi_de_temps();
                edt.setId(rs.getInt("id"));
                edt.setGroupe(rs.getString("groupe"));
                edt.setJourSemaine(rs.getString("jour_semaine"));
                edt.setHeureDebut(rs.getString("heure_debut"));
                edt.setHeureFin(rs.getString("heure_fin"));
                edt.setMatiere(rs.getString("matiere"));
                edt.setSalle(rs.getString("salle"));
                edt.setEnseignant(rs.getString("enseignant"));
                liste.add(edt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<Emploi_de_temps> getEmploiDuTempsParEnseignant(int enseignantId) {
        List<Emploi_de_temps> liste = new ArrayList<>();
        String sql = "SELECT s.id, CONCAT(c.filiere, ' - ', c.niveau) AS groupe, " +
                "DAYNAME(s.date_debut) AS jour_semaine, TIME(s.date_debut) AS heure_debut, TIME(s.date_fin) AS heure_fin, "
                +
                "c.intitule AS matiere, sa.numero AS salle, CONCAT(e.prenom, ' ', e.nom) AS enseignant " +
                "FROM sessions s " +
                "JOIN cours c ON s.cours_id = c.id " +
                "JOIN enseignants e ON c.enseignant_id = e.id " +
                "JOIN salles sa ON s.salle_id = sa.id " +
                "WHERE e.id = ?";

        System.out.println("DEBUG: Executing SQL query: " + sql);
        System.out.println("DEBUG: With enseignantId: " + enseignantId);

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Emploi_de_temps edt = new Emploi_de_temps();
                edt.setId(rs.getInt("id"));
                edt.setGroupe(rs.getString("groupe"));
                edt.setJourSemaine(rs.getString("jour_semaine"));
                edt.setHeureDebut(rs.getString("heure_debut"));
                edt.setHeureFin(rs.getString("heure_fin"));
                edt.setMatiere(rs.getString("matiere"));
                edt.setSalle(rs.getString("salle"));
                edt.setEnseignant(rs.getString("enseignant"));
                liste.add(edt);

                System.out.println("DEBUG: Fetched Emploi_de_temps entry: " + edt);
            }
            System.out.println("DEBUG: SQL query returned " + rowCount + " rows.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}
