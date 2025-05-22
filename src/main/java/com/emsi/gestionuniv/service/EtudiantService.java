package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.repository.StudentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service gérant les opérations liées aux étudiants :
 * - Authentification
 * - Recherche par matricule, email, ID
 * - Mise à jour de la photo de profil
 */
public class EtudiantService {

    // Référence à un repository en mémoire (peut être utilisé pour tests ou extensions)
    private StudentRepository studentRepository;

    // Nom du schéma et de la table utilisée dans la base de données
    private static final String DATABASE_SCHEMA = "gestion_universitaire";
    private static final String STUDENT_TABLE = "etudiants";

    /**
     * Constructeur du service étudiant
     */
    public EtudiantService() {
        this.studentRepository = new StudentRepository(); // Utilisé si on veut stocker localement
    }

    /**
     * Méthode statique permettant de rechercher un étudiant par matricule et mot de passe.
     * Sert principalement à l'authentification.
     *
     * @param matricule Matricule fourni par l'utilisateur
     * @param password Mot de passe fourni par l'utilisateur
     * @return Un objet Student si les identifiants sont valides, sinon null
     */
    public static Student findStudentByMatricule(String matricule, String password) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        System.out.println("DEBUG - Tentative d'authentification pour matricule: " + matricule);

        try {
            // Connexion à la base de données
            conn = DBConnect.getConnection();

            if (conn == null) {
                System.err.println("ERREUR CRITIQUE - Connexion à la base de données échouée.");
                return null;
            }

            System.out.println("DEBUG - Connexion établie");

            // Préparation de la requête SQL paramétrée
            String sql = String.format("SELECT * FROM %s.%s WHERE matricule = ? AND mot_de_passe = ?",
                    DATABASE_SCHEMA, STUDENT_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, matricule);
            pstmt.setString(2, password);

            System.out.println("DEBUG - Exécution de la requête...");
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Création d'un objet Student à partir du résultat
                student = createStudentFromResultSet(rs);
                System.out.println("DEBUG - Étudiant trouvé: " + student.getNom() + " " + student.getPrenom());
            } else {
                System.out.println("DEBUG - Aucun étudiant trouvé avec ces identifiants.");
            }

        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR GÉNÉRALE: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Libération des ressources
            closeResources(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * Authentifie un étudiant à l'aide de son matricule et mot de passe
     *
     * @param matricule Matricule fourni
     * @param password Mot de passe fourni
     * @return true si les identifiants sont valides, false sinon
     */
    public boolean authenticate(String matricule, String password) {
        return findStudentByMatricule(matricule, password) != null;
    }

    /**
     * Recherche un étudiant par son identifiant unique (ID)
     *
     * @param id Identifiant de l'étudiant
     * @return Objet Student trouvé ou null si aucun étudiant ne correspond
     */
    public Student findStudentById(int id) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("SELECT * FROM %s.%s WHERE id = ?", DATABASE_SCHEMA, STUDENT_TABLE);
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
     * Recherche un étudiant en fonction de son adresse email
     *
     * @param email Adresse email à rechercher
     * @return Objet Student si trouvé, null sinon
     */
    public Student findStudentByEmail(String email) {
        Student student = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("SELECT * FROM %s.%s WHERE email = ?", DATABASE_SCHEMA, STUDENT_TABLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = createStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * Met à jour le chemin de la photo de profil d’un étudiant donné
     *
     * @param id Identifiant de l’étudiant
     * @param photoPath Nouveau chemin de la photo
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateStudentPhoto(int id, String photoPath) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();

            String sql = String.format("UPDATE %s.%s SET photo = ? WHERE id = ?", DATABASE_SCHEMA, STUDENT_TABLE);
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
     * Crée un objet Student à partir d’un ResultSet
     *
     * @param rs Le ResultSet contenant les données de l'étudiant
     * @return Un objet Student entièrement initialisé
     * @throws SQLException en cas d'erreur d'accès à une colonne
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
            System.err.println("ERREUR lors de la construction de Student à partir du ResultSet: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ferme les ressources JDBC pour éviter les fuites mémoire
     *
     * @param rs Le ResultSet à fermer
     * @param pstmt Le PreparedStatement à fermer
     * @param conn La connexion à fermer
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
