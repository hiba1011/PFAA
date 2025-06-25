package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.Note;
import java.sql.*;
import java.util.*;

public class NoteService {
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String NOTES_TABLE = "notes";

    /**
     * Récupère toutes les notes des étudiants pour un cours donné
     * 
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
     * 
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

    public List<Note> getNotesByEtudiantId(int etudiantId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, c.intitule AS matiere " +
                "FROM note n " +
                "JOIN cours c ON n.cours_id = c.id " +
                "WHERE n.etudiant_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setEtudiantId(rs.getInt("etudiant_id"));
                note.setCoursId(rs.getInt("cours_id"));
                note.setControleContinu(rs.getDouble("controle_continu"));
                note.setExamen(rs.getDouble("examen"));
                note.setTp(rs.getDouble("tp"));
                note.setNoteFinale(rs.getDouble("note_finale"));
                note.setValidation(rs.getString("validation"));
                note.setMatiere(rs.getString("matiere"));
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    /**
     * Ferme les ressources JDBC
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveOrUpdateNote(Note note) {
        String sql = "UPDATE note SET controle_continu = ?, examen = ?, tp = ?, note_finale = ?, validation = ? "
                + "WHERE etudiant_id = ? AND cours_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            // Paramètres pour l'UPDATE
            ps.setDouble(1, note.getControleContinu());
            ps.setDouble(2, note.getExamen());
            ps.setDouble(3, note.getTp());
            ps.setDouble(4, note.getNoteFinale());
            ps.setString(5, note.getValidation());
            ps.setInt(6, note.getEtudiantId());
            ps.setInt(7, note.getCoursId());

            return ps.executeUpdate() > 0; // Retourne true si la mise à jour a réussi
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Note> getNotesByCoursId(int coursId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, c.intitule AS matiere FROM note n JOIN cours c ON n.cours_id = c.id WHERE n.cours_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coursId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setEtudiantId(rs.getInt("etudiant_id"));
                note.setCoursId(rs.getInt("cours_id"));
                note.setControleContinu(rs.getDouble("controle_continu"));
                note.setExamen(rs.getDouble("examen"));
                note.setTp(rs.getDouble("tp"));
                note.setNoteFinale(rs.getDouble("note_finale"));
                note.setValidation(rs.getString("validation"));
                note.setMatiere(rs.getString("matiere"));
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public List<Note> getNotesByClassAndCourse(String groupName, int courseId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT etudiant_id, nom, prenom, controle_continu, examen, tp, note_finale, validation "
                + "FROM note "
                + "JOIN etudiants ON note.etudiant_id = etudiants.id "
                + "WHERE cours_id = ? AND etudiants.groupe = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, groupName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setEtudiantId(rs.getInt("etudiant_id"));
                note.setNomEtudiant(rs.getString("nom")); // Récupère le nom de l'étudiant
                note.setPrenomEtudiant(rs.getString("prenom")); // Récupère le prénom de l'étudiant
                note.setControleContinu(rs.getDouble("controle_continu"));
                note.setExamen(rs.getDouble("examen"));
                note.setTp(rs.getDouble("tp"));
                note.setNoteFinale(rs.getDouble("note_finale"));
                note.setValidation(rs.getString("validation"));
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public Note getNoteByStudentAndCourse(int etudiantId, int coursId) {
        Note note = null;
        String sql = "SELECT n.*, e.nom, e.prenom, c.intitule AS matiere " +
                "FROM note n " +
                "JOIN etudiants e ON n.etudiant_id = e.id " +
                "JOIN cours c ON n.cours_id = c.id " +
                "WHERE n.etudiant_id = ? AND n.cours_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, etudiantId);
            ps.setInt(2, coursId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                note = new Note();
                note.setEtudiantId(rs.getInt("etudiant_id"));
                note.setCoursId(rs.getInt("cours_id"));
                note.setControleContinu(rs.getDouble("controle_continu"));
                note.setExamen(rs.getDouble("examen"));
                note.setTp(rs.getDouble("tp"));
                note.setNoteFinale(rs.getDouble("note_finale"));
                note.setValidation(rs.getString("validation"));
                note.setNomEtudiant(rs.getString("nom"));
                note.setPrenomEtudiant(rs.getString("prenom"));
                note.setMatiere(rs.getString("matiere"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return note;
    }

}