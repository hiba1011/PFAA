package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.repository.StudentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service gérant les opérations liées aux étudiants
 */
public class EtudiantService {
    private StudentRepository studentRepository;
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String STUDENT_TABLE = "etudiants";

    public EtudiantService() {
        this.studentRepository = new StudentRepository();
    }

    /**
     * Retourne l'objet Student correspondant au matricule et mot de passe,
     * ou null si aucun étudiant n'est trouvé.
     *
     * @param matricule Le matricule de l'étudiant
     * @param password Le mot de passe de l'étudiant
     * @return L'objet Student si trouvé, null sinon
     */
    public static Student findStudentByMatricule(String matricule, String password) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        System.out.println("DEBUG - Tentative d'authentification pour matricule: " + matricule);

        try {
            conn = DBConnect.getConnection();

            if (conn == null) {
                System.err.println("ERREUR CRITIQUE - La connexion à la base de données est null!");
                return null;
            }

            System.out.println("DEBUG - Connexion à la base de données établie");

            String sql = String.format("SELECT * FROM %s.%s WHERE matricule = ? AND mot_de_passe = ?",
                    DATABASE_SCHEMA, STUDENT_TABLE);
            System.out.println("DEBUG - Requête SQL: " + sql.replace("?", "***"));

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, matricule);
            pstmt.setString(2, password);

            System.out.println("DEBUG - Exécution de la requête...");
            rs = pstmt.executeQuery();

            boolean found = rs.next();
            System.out.println("DEBUG - Résultat trouvé: " + (found ? "OUI" : "NON"));

            if (found) {
                System.out.println("DEBUG - Construction de l'objet Student...");
                student = createStudentFromResultSet(rs);
                System.out.println("DEBUG - Étudiant trouvé: " + student.getNom() + " " + student.getPrenom());
            } else {
                System.out.println("DEBUG - Aucun étudiant trouvé avec ces identifiants");
            }

        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de la recherche de l'étudiant: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR GÉNÉRALE lors de la recherche de l'étudiant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * Authentifie un étudiant en vérifiant si le matricule et le mot de passe correspondent
     * à un enregistrement dans la base de données.
     *
     * @param matricule Le matricule fourni par l'utilisateur
     * @param password Le mot de passe fourni par l'utilisateur
     * @return true si l'authentification réussit, false sinon
     */
    public boolean authenticate(String matricule, String password) {
        return findStudentByMatricule(matricule, password) != null;
    }

    /**
     * Récupère un étudiant par son ID
     *
     * @param id L'identifiant de l'étudiant
     * @return l'objet Student correspondant ou null si non trouvé
     */
    public Student findStudentById(int id) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("SELECT * FROM %s.%s WHERE id = ?",
                    DATABASE_SCHEMA, STUDENT_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = createStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'étudiant par ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * Recherche un étudiant par son email
     *
     * @param email L'email de l'étudiant
     * @return l'objet Student correspondant ou null si non trouvé
     */
    public Student findStudentByEmail(String email) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("SELECT * FROM %s.%s WHERE email = ?",
                    DATABASE_SCHEMA, STUDENT_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = createStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'étudiant par email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * Met à jour la photo de profil d'un étudiant
     *
     * @param id L'identifiant de l'étudiant
     * @param photoPath Le chemin de la nouvelle photo
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateStudentPhoto(int id, String photoPath) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("UPDATE %s.%s SET photo = ? WHERE id = ?",
                    DATABASE_SCHEMA, STUDENT_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, photoPath);
            pstmt.setInt(2, id);

            success = pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la photo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }

        return success;
    }

    /**
     * Crée un objet Student à partir d'un ResultSet
     */
    private static Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        try {
            Student student = new Student();
            student.setId(rs.getInt("id"));
            student.setMatricule(rs.getString("matricule"));
            student.setNom(rs.getString("nom"));
            student.setPrenom(rs.getString("prenom"));
            student.setEmail(rs.getString("email"));
            student.setPassword(rs.getString("mot_de_passe"));
            student.setPromotion(rs.getString("promotion"));
            student.setFiliere(rs.getString("filiere"));
            student.setPhoto(rs.getString("photo"));
            return student;
        } catch (SQLException e) {
            System.err.println("ERREUR lors de la création de l'étudiant depuis ResultSet:");
            System.err.println("Colonne manquante ou erreur de conversion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Méthode utilitaire pour fermer les ressources de base de données
     */
    private static void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}