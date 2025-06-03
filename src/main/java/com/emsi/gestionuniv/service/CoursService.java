package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.cours;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoursService {
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String COURS_TABLE = "cours";

    /**
     * Récupère tous les cours d'un enseignant
     * @param enseignantId ID de l'enseignant
     * @return Liste des cours
     */
    public List<cours> getCoursByEnseignant(int enseignantId) {
        List<cours> coursList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT * FROM %s.%s WHERE enseignant_id = ?", 
                    DATABASE_SCHEMA, COURS_TABLE);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enseignantId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cours c = new cours();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setIntitule(rs.getString("intitule"));
                c.setFiliere(rs.getString("filiere"));
                c.setNiveau(rs.getString("niveau"));
                c.setEffectif(rs.getInt("effectif"));
                c.setVolumeHoraire(rs.getString("volume_horaire"));
                coursList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return coursList;
    }

    /**
     * Récupère un cours par son ID
     * @param coursId ID du cours
     * @return L'objet cours ou null si non trouvé
     */
    public cours getCoursById(int coursId) {
        cours c = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT * FROM %s.%s WHERE id = ?", 
                    DATABASE_SCHEMA, COURS_TABLE);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                c = new cours();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setIntitule(rs.getString("intitule"));
                c.setFiliere(rs.getString("filiere"));
                c.setNiveau(rs.getString("niveau"));
                c.setEffectif(rs.getInt("effectif"));
                c.setVolumeHoraire(rs.getString("volume_horaire"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return c;
    }

    /**
     * Met à jour un cours
     * @param c L'objet cours à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateCours(cours c) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("UPDATE %s.%s SET code = ?, intitule = ?, filiere = ?, " +
                    "niveau = ?, effectif = ?, volume_horaire = ? WHERE id = ?", 
                    DATABASE_SCHEMA, COURS_TABLE);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getCode());
            pstmt.setString(2, c.getIntitule());
            pstmt.setString(3, c.getFiliere());
            pstmt.setString(4, c.getNiveau());
            pstmt.setInt(5, c.getEffectif());
            pstmt.setString(6, c.getVolumeHoraire());
            pstmt.setInt(7, c.getId());

            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Ferme les ressources JDBC
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 