package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.planning.Emploi_de_temps;
import com.emsi.gestionuniv.config.DBConnect;
import java.sql.*;
import java.util.*;

public class EmploiDuTempsService {
    public List<Emploi_de_temps> getEmploiDuTempsParClasse(String classe) {
        List<Emploi_de_temps> liste = new ArrayList<>();
        String sql = "SELECT * FROM emploi_etu WHERE groupe = ?";
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
}