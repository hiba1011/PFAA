package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.academic.CoursPdf;
import com.emsi.gestionuniv.config.DBConnect;
import java.sql.*;
import java.util.*;

public class CoursPdfService {
    public void ajouterCoursPdf(String titre, String chemin, int enseignantId, String classe) {
        String sql = "INSERT INTO cours_pdf (titre, chemin_pdf, enseignant_id, classe) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setString(2, chemin);
            ps.setInt(3, enseignantId);
            ps.setString(4, classe);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CoursPdf> getCoursPdfByClasse(String classe) {
        List<CoursPdf> pdfs = new ArrayList<>();
        String sql = "SELECT * FROM cours_pdf WHERE classe = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CoursPdf pdf = new CoursPdf();
                pdf.setId(rs.getInt("id"));
                pdf.setTitre(rs.getString("titre"));
                pdf.setCheminPdf(rs.getString("chemin_pdf"));
                pdf.setClasse(rs.getString("classe"));
                pdf.setEnseignantId(rs.getInt("enseignant_id"));
                pdfs.add(pdf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pdfs;
    }

    public List<CoursPdf> getCoursPdfByEnseignant(int enseignantId) {
        List<CoursPdf> pdfs = new ArrayList<>();
        String sql = "SELECT * FROM cours_pdf WHERE enseignant_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CoursPdf pdf = new CoursPdf();
                pdf.setId(rs.getInt("id"));
                pdf.setTitre(rs.getString("titre"));
                pdf.setCheminPdf(rs.getString("chemin_pdf"));
                pdf.setClasse(rs.getString("classe"));
                pdf.setEnseignantId(rs.getInt("enseignant_id"));
                pdfs.add(pdf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pdfs;
    }

    public void deleteCoursPdf(int id) {
        String sql = "DELETE FROM cours_pdf WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}