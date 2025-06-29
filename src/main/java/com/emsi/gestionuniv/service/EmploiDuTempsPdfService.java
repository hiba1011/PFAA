package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf;
import java.sql.*;
import java.util.*;
import com.emsi.gestionuniv.config.DBConnect;

public class EmploiDuTempsPdfService {
// Ajoute ces méthodes dans EmploiDuTempsPdfService

public void ajouterEmploi(EmploiDuTempsPdf emploi) {
    try (Connection conn = DBConnect.getConnection()) {
        String sql = "INSERT INTO emploi_du_temps_pdf (type, cible, titre, chemin_pdf) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, emploi.getType());
        ps.setString(2, emploi.getCible());
        ps.setString(3, emploi.getTitre());
        ps.setString(4, emploi.getCheminPdf());
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public List<EmploiDuTempsPdf> getEmploisEtudiantByClasse(String classe) {
    List<EmploiDuTempsPdf> list = new ArrayList<>();
    try (Connection conn = DBConnect.getConnection()) {
        String sql = "SELECT * FROM emploi_du_temps_pdf WHERE type = 'etudiant' AND cible = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, classe);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            EmploiDuTempsPdf e = new EmploiDuTempsPdf();
            e.setId(rs.getInt("id"));
            e.setType(rs.getString("type"));
            e.setCible(rs.getString("cible"));
            e.setTitre(rs.getString("titre"));
            e.setCheminPdf(rs.getString("chemin_pdf"));
            list.add(e);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public List<EmploiDuTempsPdf> getEmploisEnseignantById(int enseignantId) {
    List<EmploiDuTempsPdf> list = new ArrayList<>();
    try (Connection conn = DBConnect.getConnection()) {
        String sql = "SELECT * FROM emploi_du_temps_pdf WHERE type = 'enseignant' AND cible = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.valueOf(enseignantId));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            EmploiDuTempsPdf e = new EmploiDuTempsPdf();
            e.setId(rs.getInt("id"));
            e.setType(rs.getString("type"));
            e.setCible(rs.getString("cible"));
            e.setTitre(rs.getString("titre"));
            e.setCheminPdf(rs.getString("chemin_pdf"));
            list.add(e);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
}