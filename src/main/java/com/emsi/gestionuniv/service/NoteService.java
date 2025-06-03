package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.Note;
import com.emsi.gestionuniv.model.user.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteService {
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String NOTES_TABLE = "notes";

    /**
     * Récupère toutes les notes des étudiants pour un cours donné
     * @param coursId ID du cours
     * @return Liste des notes
     */
    public List<Note> getNotesByCours(int coursId) {
        List<Note> notes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT n.*, e.matricule, e.nom, e.prenom " +
                    "FROM %s.%s n " +
                    "JOIN etudiants e ON n.etudiant_id = e.id " +
                    "WHERE n.cours_id = ?", DATABASE_SCHEMA, NOTES_TABLE);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setEtudiantId(rs.getInt("etudiant_id"));
                note.setCoursId(rs.getInt("cours_id"));
                note.setNoteCC(rs.getDouble("note_cc"));
                note.setNoteTP(rs.getDouble("note_tp"));
                note.setNoteExamen(rs.getDouble("note_examen"));
                note.setMoyenne(rs.getDouble("moyenne"));
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return notes;
    }

    /**
     * Met à jour les notes d'un étudiant
     * @param note L'objet Note contenant les nouvelles notes
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateNote(Note note) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("UPDATE %s.%s SET note_cc = ?, note_tp = ?, note_examen = ?, moyenne = ? " +
                    "WHERE id = ?", DATABASE_SCHEMA, NOTES_TABLE);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, note.getNoteCC());
            pstmt.setDouble(2, note.getNoteTP());
            pstmt.setDouble(3, note.getNoteExamen());
            pstmt.setDouble(4, note.getMoyenne());
            pstmt.setInt(5, note.getId());

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