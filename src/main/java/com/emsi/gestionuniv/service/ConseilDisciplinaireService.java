package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.academic.ConseilDisciplinaire;
import com.emsi.gestionuniv.config.DBConnect;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConseilDisciplinaireService {
    private static final String TABLE = "ConseilsDisciplinaires";
    private static final String SCHEMA = "gestion_universitaire";

    public void ajouterConseil(ConseilDisciplinaire conseil) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = String.format(
                    "INSERT INTO %s.%s (etudiant_id, cours_id, type, date, commentaire) VALUES (?, ?, ?, ?, ?)", SCHEMA,
                    TABLE);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, conseil.getEtudiantId());
            ps.setInt(2, conseil.getCoursId());
            ps.setString(3, conseil.getType());
            ps.setTimestamp(4, Timestamp.valueOf(conseil.getDate()));
            ps.setString(5, conseil.getCommentaire());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ConseilDisciplinaire> getConseilsByEtudiant(int etudiantId) {
        List<ConseilDisciplinaire> conseils = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = String.format("SELECT * FROM %s.%s WHERE etudiant_id = ? ORDER BY date DESC", SCHEMA, TABLE);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ConseilDisciplinaire c = new ConseilDisciplinaire();
                c.setId(rs.getInt("id"));
                c.setEtudiantId(rs.getInt("etudiant_id"));
                c.setCoursId(rs.getInt("cours_id"));
                c.setType(rs.getString("type"));
                c.setDate(rs.getTimestamp("date").toLocalDateTime());
                c.setCommentaire(rs.getString("commentaire"));
                conseils.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conseils;
    }

    public List<ConseilDisciplinaire> getAllConseils() {
        List<ConseilDisciplinaire> conseils = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM gestion_universitaire.ConseilsDisciplinaires ORDER BY date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ConseilDisciplinaire c = new ConseilDisciplinaire();
                c.setId(rs.getInt("id"));
                c.setEtudiantId(rs.getInt("etudiant_id"));
                c.setCoursId(rs.getInt("cours_id"));
                c.setType(rs.getString("type"));
                c.setDate(rs.getTimestamp("date").toLocalDateTime());
                c.setCommentaire(rs.getString("commentaire"));
                conseils.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conseils;
    }
}