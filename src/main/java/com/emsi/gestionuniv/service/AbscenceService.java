package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.model.academic.ConseilDisciplinaire;
import com.emsi.gestionuniv.model.user.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                Abscence.setJustification(rs.getBytes("justification"));

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
            pstmt.setBytes(5, absence.getJustification());
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
            pstmt.setBytes(5, absence.getJustification());
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
                absence.setJustification(rs.getBytes("justification"));

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
                absence.setJustification(rs.getBytes("justification"));
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
            // Vérifier s'il existe déjà une absence pour cet étudiant, ce cours et cette
            // date
            String checkSql = String.format(
                    "SELECT COUNT(*) FROM %s.%s WHERE etudiant_id = ? AND cours_id = ? AND date = ?",
                    DATABASE_SCHEMA, ABSCENCES_TABLE);
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
                    DATABASE_SCHEMA, ABSCENCES_TABLE);
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, absence.getEtudiantId());
            insertStmt.setInt(2, absence.getCoursId());
            insertStmt.setDate(3, absence.getDate());
            insertStmt.setBoolean(4, absence.isJustifiee());
            insertStmt.setBytes(5, absence.getJustification());
            insertStmt.executeUpdate();

            int absCount = getAbsenceCountForEtudiantCours(absence.getEtudiantId(), absence.getCoursId());
            ConseilDisciplinaireService conseilService = new ConseilDisciplinaireService();
            if (absCount == 5) {
                ConseilDisciplinaire c = new ConseilDisciplinaire();
                c.setEtudiantId(absence.getEtudiantId());
                c.setCoursId(absence.getCoursId());
                c.setType("Blâme");
                c.setDate(java.time.LocalDateTime.now());
                c.setCommentaire("5 absences non justifiées dans ce cours.");
                conseilService.ajouterConseil(c);
            } else if (absCount == 10) {
                ConseilDisciplinaire c = new ConseilDisciplinaire();
                c.setEtudiantId(absence.getEtudiantId());
                c.setCoursId(absence.getCoursId());
                c.setType("Conseil disciplinaire");
                c.setDate(java.time.LocalDateTime.now());
                c.setCommentaire("10 absences non justifiées dans ce cours.");
                conseilService.ajouterConseil(c);
            } else if (absCount > 10 && absCount % 1 == 0) { // chaque absence au-delà de 10
                ConseilDisciplinaire c = new ConseilDisciplinaire();
                c.setEtudiantId(absence.getEtudiantId());
                c.setCoursId(absence.getCoursId());
                c.setType("Exclusion 7 jours");
                c.setDate(java.time.LocalDateTime.now());
                c.setCommentaire("Plus de 10 absences non justifiées dans ce cours. Exclusion temporaire.");
                conseilService.ajouterConseil(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, checkStmt, null);
            closeResources(null, insertStmt, conn);
        }
    }

    public int getAbsenceCountForEtudiantCours(int etudiantId, int coursId) {
        int count = 0;
        try (Connection conn = DBConnect.getConnection()) {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s.%s WHERE etudiant_id = ? AND cours_id = ? AND justifiee = 0",
                    DATABASE_SCHEMA, ABSCENCES_TABLE);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, etudiantId);
            ps.setInt(2, coursId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                count = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void updateJustificatif(int abscenceId, File justificatifImage) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE abscences SET justification = ?, justifiee = false WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            FileInputStream fis = new FileInputStream(justificatifImage);
            ps.setBinaryStream(1, fis, (int) justificatifImage.length());
            ps.setInt(2, abscenceId);
            ps.executeUpdate();
            fis.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void validerJustificatif(int abscenceId, boolean approuve) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE abscences SET justifiee = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, approuve);
            ps.setInt(2, abscenceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Abscence> getAbsencesByClasse(String classe) {
        List<Abscence> absences = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT a.* FROM abscences a JOIN etudiants e ON a.etudiant_id = e.id WHERE e.groupe = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Abscence a = new Abscence();
                a.setId(rs.getInt("id"));
                a.setEtudiantId(rs.getInt("etudiant_id"));
                a.setCoursId(rs.getInt("cours_id"));
                a.setDate(rs.getDate("date"));
                a.setJustifiee(rs.getBoolean("justifiee"));
                a.setJustification(rs.getBytes("justification"));
                absences.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return absences;
    }

    public void updateJustificationTexte(int abscenceId, String justificationTexte) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE abscences SET justification_texte = ?, justifiee = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, justificationTexte);
            ps.setBoolean(2, justificationTexte != null && !justificationTexte.isEmpty());
            ps.setInt(3, abscenceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAction(int abscenceId, String action) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE abscences SET action = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, action);
            ps.setInt(2, abscenceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}