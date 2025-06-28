package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.model.academic.Note;

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
     * 
     * @param enseignantId ID de l'enseignant
     * @return Liste des cours
     */
    public List<cours> getCoursByEnseignant(int enseignantId) {
        List<cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cours WHERE enseignant_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cours c = new cours();
                c.setId(rs.getInt("id"));
                c.setTitre(rs.getString("titre"));
                c.setCode(rs.getString("code"));
                c.setIntitule(rs.getString("intitule"));
                c.setFiliere(rs.getString("filiere"));
                c.setNiveau(rs.getString("niveau"));
                c.setEffectif(rs.getInt("effectif"));
                c.setVolumeHoraire(rs.getString("volume_horaire"));
                c.setCredits(rs.getInt("credits"));
                c.setEnseignantId(rs.getInt("enseignant_id"));
                coursList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DEBUG: Cours récupérés : " + coursList.size());
        return coursList;
    }

    /**
     * Récupère un cours par son ID
     * 
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
     * 
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

    /**
     * Récupère les étudiants par le nom du groupe
     * 
     * @param groupName Nom du groupe
     * @return Liste des étudiants
     */
    public List<Student> getStudentsByGroupName(String groupName) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE groupe = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, groupName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setNom(rs.getString("nom"));
                student.setPrenom(rs.getString("prenom"));
                student.setMatricule(rs.getString("matricule"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Récupère les notes par l'ID du cours
     * 
     * @param coursId ID du cours
     * @return Liste des notes
     */
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

    /**
     * Récupère les classes par l'ID de l'enseignant
     * 
     * @param teacherId ID de l'enseignant
     * @return Liste des classes
     */
    public List<String> getClassesByTeacherId(int teacherId) {
        List<String> classes = new ArrayList<>();
        String sql = "SELECT DISTINCT groupe FROM etudiants e " +
                "JOIN cours c ON e.cours_id = c.id " +
                "WHERE c.enseignant_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                classes.add(rs.getString("groupe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * Récupère les matières enseignées par un enseignant
     * 
     * @param teacherId ID de l'enseignant
     * @return Liste des matières
     */
    public List<String> getMatieresByTeacherId(int teacherId) {
        List<String> matieres = new ArrayList<>();
        String sql = "SELECT intitule FROM cours WHERE enseignant_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matieres.add(rs.getString("intitule")); // Utilise la colonne `intitule`
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    /**
     * Récupère l'ID d'un cours par son intitule
     * 
     * @param intitule Intitule du cours
     * @return ID du cours ou -1 si non trouvé
     */
    public int getCoursIdByIntitule(String intitule) {
        String sql = "SELECT id FROM cours WHERE intitule = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, intitule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si aucun cours n'est trouvé
    }

    /**
     * Récupère tous les cours de la base de données
     * @return Liste de tous les cours
     */
    public List<cours> getAllCours() {
        List<cours> coursList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = String.format("SELECT * FROM %s.%s", DATABASE_SCHEMA, COURS_TABLE);
            pstmt = conn.prepareStatement(sql);
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
            System.err.println("Erreur lors de la récupération de tous les cours: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return coursList;
    }
    
    /**
     * Ajoute un nouveau cours dans la base de données
     * @param c L'objet cours à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean addCours(cours c) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("INSERT INTO %s.%s (code, intitule, credits, enseignant_id, filiere, niveau, effectif, volume_horaire, titre) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    DATABASE_SCHEMA, COURS_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getCode());
            pstmt.setString(2, c.getIntitule());
            pstmt.setInt(3, c.getCredits());
            pstmt.setInt(4, c.getEnseignantId());
            pstmt.setString(5, c.getFiliere());
            pstmt.setString(6, c.getNiveau());
            pstmt.setInt(7, c.getEffectif());
            pstmt.setString(8, c.getVolumeHoraire());
            pstmt.setString(9, c.getTitre());

            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du cours: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }
    
    /**
     * Supprime un cours de la base de données
     * @param coursId ID du cours à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteCours(int coursId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            String sql = String.format("DELETE FROM %s.%s WHERE id = ?",
                    DATABASE_SCHEMA, COURS_TABLE);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);

            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du cours: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }
    
    public int countCours() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM cours";
        try (java.sql.Connection conn = com.emsi.gestionuniv.config.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public java.util.List<cours> getLastCours(int n) {
        java.util.List<cours> list = new java.util.ArrayList<>();
        String sql = String.format("SELECT * FROM %s.%s ORDER BY id DESC LIMIT ?", DATABASE_SCHEMA, COURS_TABLE);
        try (java.sql.Connection conn = com.emsi.gestionuniv.config.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cours c = new cours();
                    c.setId(rs.getInt("id"));
                    c.setCode(rs.getString("code"));
                    c.setIntitule(rs.getString("intitule"));
                    c.setCredits(rs.getInt("credits"));
                    c.setEnseignantId(rs.getInt("enseignant_id"));
                    c.setFiliere(rs.getString("filiere"));
                    c.setNiveau(rs.getString("niveau"));
                    c.setEffectif(rs.getInt("effectif"));
                    c.setVolumeHoraire(rs.getString("volume_horaire"));
                    c.setTitre(rs.getString("titre"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class CoursWithEnseignant extends cours {
        private String enseignantNom;
        private String enseignantPrenom;
        public String getEnseignantNom() { return enseignantNom; }
        public void setEnseignantNom(String nom) { this.enseignantNom = nom; }
        public String getEnseignantPrenom() { return enseignantPrenom; }
        public void setEnseignantPrenom(String prenom) { this.enseignantPrenom = prenom; }
    }

    public List<CoursWithEnseignant> getAllCoursWithEnseignant() {
        List<CoursWithEnseignant> coursList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = "SELECT c.*, e.nom AS enseignant_nom, e.prenom AS enseignant_prenom " +
                         "FROM cours c LEFT JOIN enseignants e ON c.enseignant_id = e.id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CoursWithEnseignant c = new CoursWithEnseignant();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setIntitule(rs.getString("intitule"));
                c.setFiliere(rs.getString("filiere"));
                c.setNiveau(rs.getString("niveau"));
                c.setEffectif(rs.getInt("effectif"));
                c.setVolumeHoraire(rs.getString("volume_horaire"));
                c.setTitre(rs.getString("titre"));
                c.setCredits(rs.getInt("credits"));
                c.setEnseignantId(rs.getInt("enseignant_id"));
                c.setEnseignantNom(rs.getString("enseignant_nom"));
                c.setEnseignantPrenom(rs.getString("enseignant_prenom"));
                coursList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return coursList;
    }

    public CoursWithEnseignant getCoursByIdWithEnseignant(int coursId) {
        CoursWithEnseignant c = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = "SELECT c.*, e.nom AS enseignant_nom, e.prenom AS enseignant_prenom " +
                         "FROM cours c LEFT JOIN enseignants e ON c.enseignant_id = e.id WHERE c.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coursId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                c = new CoursWithEnseignant();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setIntitule(rs.getString("intitule"));
                c.setFiliere(rs.getString("filiere"));
                c.setNiveau(rs.getString("niveau"));
                c.setEffectif(rs.getInt("effectif"));
                c.setVolumeHoraire(rs.getString("volume_horaire"));
                c.setTitre(rs.getString("titre"));
                c.setCredits(rs.getInt("credits"));
                c.setEnseignantId(rs.getInt("enseignant_id"));
                c.setEnseignantNom(rs.getString("enseignant_nom"));
                c.setEnseignantPrenom(rs.getString("enseignant_prenom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return c;
    }
}