package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.repository.TeacherRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherService {
    private TeacherRepository teacherRepository;

    public TeacherService() {
        this.teacherRepository = new TeacherRepository();
    }
    /**
     * Retourne l'objet Teacher correspondant à l'email,
     * ou null si aucun enseignant n'est trouvé.
     */
    public Teacher findTeacherByEmail(String email, String password) {
        Teacher teacher = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Obtenir une connexion à la base de données
            conn = com.emsi.gestionuniv.config.DBConnect.getConnection();

            // Requête SQL pour rechercher un enseignant par email et mot de passe
            String sql = "SELECT * FROM gestion_universitaire.enseignants WHERE email = ? AND mot_de_passe = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // Idéalement, utilisez un hachage pour le mot de passe

            rs = pstmt.executeQuery();

            // Si un enseignant est trouvé, créer un objet Teacher
            if (rs.next()) {
                teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setNom(rs.getString("nom"));
                teacher.setPrenom(rs.getString("prenom"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPassword(rs.getString("mot_de_passe"));
                teacher.setDepartement(rs.getString("departement"));
                teacher.setSpecialite(rs.getString("specialite"));
                // Pas besoin de définir le rôle car il est défini par défaut à "TEACHER" dans le constructeur
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return teacher;
    }

    /**
     * Authentifie un enseignant en vérifiant si l'email et le mot de passe correspondent
     * à un enregistrement dans la base de données.
     *
     * @param email L'email fourni par l'utilisateur
     * @param password Le mot de passe fourni par l'utilisateur
     * @return true si l'authentification réussit, false sinon
     */
    public boolean authenticate(String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean authenticated = false;

        try {
            conn = DBConnect.getConnection();

            String sql = "SELECT COUNT(*) FROM gestion_universitaire.enseignants WHERE email = ? AND mot_de_passe = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                authenticated = true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return authenticated;
    }
}
