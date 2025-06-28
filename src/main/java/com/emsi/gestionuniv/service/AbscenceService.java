package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.model.user.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.emsi.gestionuniv.service.EtudiantService.closeResources;

public class AbscenceService {
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String ABSCENCES_TABLE = "Abscences";

    public List<Abscence> getAbscencesByCours(int coursId) {
        List<Abscence> Abscences = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT a.*, e.matricule, e.nom, e.prenom " +
                    "FROM %s.%s a " +
                    "JOIN etudiants e ON a.etudiant_id = e.id " +
                    "WHERE a.cours_id = ?", DATABASE_SCHEMA, ABSCENCES_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Abscence Abscence = new Abscence();
                Abscence.setId(rs.getInt("id"));
                Abscence.setEtudiantId(rs.getInt("etudiant_id"));
                Abscence.setCoursId(rs.getInt("cours_id"));
                Abscence.setDate(rs.getDate("date"));
                Abscence.setJustifiee(rs.getBoolean("justifiee"));
                Abscence.setJustification(rs.getString("justification"));

                Student student = new Student();
                student.setMatricule(rs.getString("matricule"));
                student.setNom(rs.getString("nom"));
                student.setPrenom(rs.getString("prenom"));
                Abscence.setEtudiant(student);

                Abscences.add(Abscence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return Abscences;
    }

    public boolean addAbsence(Abscence absence, int enseignantId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        // Vérification de la propriété du cours
        com.emsi.gestionuniv.service.CoursService coursService = new com.emsi.gestionuniv.service.CoursService();
        com.emsi.gestionuniv.model.academic.cours cours = coursService.getCoursById(absence.getCoursId());
        if (cours == null || cours.getEnseignantId() != enseignantId) {
            System.err.println("Tentative d'ajout d'absence sur un cours non autorisé pour cet enseignant.");
            return false;
        }

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("INSERT INTO %s.%s (etudiant_id, cours_id, date, justifiee, justification) " +
                    "VALUES (?, ?, ?, ?, ?)", DATABASE_SCHEMA, ABSCENCES_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, absence.getEtudiantId());
            pstmt.setInt(2, absence.getCoursId());
            pstmt.setDate(3, absence.getDate());
            pstmt.setBoolean(4, absence.isJustifiee());
            pstmt.setString(5, absence.getJustification());

            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }

    public boolean updateAbsence(Abscence absence) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("UPDATE %s.%s SET justifiee = ?, justification = ? WHERE id = ?",
                    DATABASE_SCHEMA, ABSCENCES_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, absence.isJustifiee());
            pstmt.setString(2, absence.getJustification());
            pstmt.setInt(3, absence.getId());

            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }

    public int[] getAbsenceStats(int coursId) {
        int[] stats = new int[3];
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT COUNT(*) as total, " +
                    "SUM(CASE WHEN justifiee = 1 THEN 1 ELSE 0 END) as justifiees, " +
                    "SUM(CASE WHEN justifiee = 0 THEN 1 ELSE 0 END) as non_justifiees " +
                    "FROM %s.%s WHERE cours_id = ?", DATABASE_SCHEMA, ABSCENCES_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("justifiees");
                stats[2] = rs.getInt("non_justifiees");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return stats;
    }

    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Abscence> getAbsencesByCours(int currentCoursId) {
        List<Abscence> absences = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT a.*, e.matricule, e.nom, e.prenom " +
                    "FROM %s.%s a " +
                    "JOIN etudiants e ON a.etudiant_id = e.id " +
                    "WHERE a.cours_id = ?", DATABASE_SCHEMA, ABSCENCES_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentCoursId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Abscence absence = new Abscence();
                absence.setId(rs.getInt("id"));
                absence.setEtudiantId(rs.getInt("etudiant_id"));
                absence.setCoursId(rs.getInt("cours_id"));
                absence.setDate(rs.getDate("date"));
                absence.setJustifiee(rs.getBoolean("justifiee"));
                absence.setJustification(rs.getString("justification"));

                Student student = new Student();
                student.setMatricule(rs.getString("matricule"));
                student.setNom(rs.getString("nom"));
                student.setPrenom(rs.getString("prenom"));
                absence.setEtudiant(student);

                absences.add(absence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return absences;
    }

    public List<Abscence> getAbsencesByEtudiantId(int etudiantId) {
        List<Abscence> absences = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT * FROM %s.%s WHERE etudiant_id = ?", DATABASE_SCHEMA, ABSCENCES_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, etudiantId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Abscence absence = new Abscence();
                absence.setId(rs.getInt("id"));
                absence.setEtudiantId(rs.getInt("etudiant_id"));
                absence.setCoursId(rs.getInt("cours_id"));
                absence.setDate(rs.getDate("date"));
                absence.setJustifiee(rs.getBoolean("justifiee"));
                absence.setJustification(rs.getString("justification"));
                absences.add(absence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return absences;
    }

    public void ajouterAbsence(Abscence absence) {
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            // Vérifier s'il existe déjà une absence pour cet étudiant, ce cours et cette date
            String checkSql = String.format(
                "SELECT COUNT(*) FROM %s.%s WHERE etudiant_id = ? AND cours_id = ? AND date = ?",
                DATABASE_SCHEMA, ABSCENCES_TABLE
            );
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, absence.getEtudiantId());
            checkStmt.setInt(2, absence.getCoursId());
            checkStmt.setDate(3, absence.getDate());
            rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Déjà existant, on n'ajoute pas
                System.out.println("Absence déjà enregistrée pour cet étudiant, ce cours et cette date.");
                return;
            }

            // Ajout de l'absence
            String insertSql = String.format(
                "INSERT INTO %s.%s (etudiant_id, cours_id, date, justifiee, justification) VALUES (?, ?, ?, ?, ?)",
                DATABASE_SCHEMA, ABSCENCES_TABLE
            );
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, absence.getEtudiantId());
            insertStmt.setInt(2, absence.getCoursId());
            insertStmt.setDate(3, absence.getDate());
            insertStmt.setBoolean(4, absence.isJustifiee());
            insertStmt.setString(5, absence.getJustification());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, checkStmt, null);
            closeResources(null, insertStmt, conn);
        }
    }
}