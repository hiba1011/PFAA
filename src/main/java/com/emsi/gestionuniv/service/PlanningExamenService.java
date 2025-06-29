package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.academic.PlanningExamen;
import java.sql.*;
import java.util.*;
import com.emsi.gestionuniv.config.DBConnect;

public class PlanningExamenService {
    public void ajouterPlanning(PlanningExamen planning) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO planning_examens (classe, titre, chemin_pdf, type, cible) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, planning.getClasse());
            ps.setString(2, planning.getTitre());
            ps.setString(3, planning.getCheminPdf());
            ps.setString(4, planning.getType());
            ps.setString(5, planning.getCible());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlanningExamen> getPlanningsByClasse(String classe) {
        List<PlanningExamen> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM planning_examens WHERE type = 'etudiant' AND cible = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlanningExamen p = new PlanningExamen();
                p.setId(rs.getInt("id"));
                p.setClasse(rs.getString("classe"));
                p.setTitre(rs.getString("titre"));
                p.setCheminPdf(rs.getString("chemin_pdf"));
                p.setType(rs.getString("type"));
                p.setCible(rs.getString("cible"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PlanningExamen> getPlanningsByEnseignant(int enseignantId) {
        List<PlanningExamen> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM planning_examens WHERE type = 'enseignant' AND cible = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, String.valueOf(enseignantId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlanningExamen p = new PlanningExamen();
                p.setId(rs.getInt("id"));
                p.setClasse(rs.getString("classe"));
                p.setTitre(rs.getString("titre"));
                p.setCheminPdf(rs.getString("chemin_pdf"));
                p.setType(rs.getString("type"));
                p.setCible(rs.getString("cible"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PlanningExamen> getAllPlannings() {
        List<PlanningExamen> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM planning_examens";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlanningExamen p = new PlanningExamen();
                p.setId(rs.getInt("id"));
                p.setClasse(rs.getString("classe"));
                p.setTitre(rs.getString("titre"));
                p.setCheminPdf(rs.getString("chemin_pdf"));
                p.setType(rs.getString("type"));
                p.setCible(rs.getString("cible"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}