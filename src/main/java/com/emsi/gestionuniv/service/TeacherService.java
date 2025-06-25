package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.repository.TeacherRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email et le mot de passe ne peuvent pas être vides");
        }

        Teacher teacher = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Obtenir une connexion à la base de données
            conn = DBConnect.getConnection();
            if (conn == null) {
                throw new SQLException("Impossible d'établir une connexion à la base de données");
            }

            // Hacher le mot de passe
           // String hashedPassword = hashPassword(password);

            // Requête SQL pour rechercher un enseignant par email et mot de passe
            String sql = "SELECT * FROM gestion_universitaire.enseignants WHERE email = ? AND mot_de_passe = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim());
            pstmt.setString(2, password);

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
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'enseignant : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'authentification", e);
        } finally {
            // Fermeture des ressources
            closeResources(rs, pstmt, conn);
        }

        return teacher;
    }

    /**
     * Hache un mot de passe en utilisant SHA-256
     */
    /*private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }*/

    /**
     * Ferme proprement les ressources de base de données
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
        }
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

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            if (conn == null) {
                throw new SQLException("Impossible d'établir une connexion à la base de données");
            }

            String sql = "SELECT * FROM gestion_universitaire.enseignants";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setNom(rs.getString("nom"));
                teacher.setPrenom(rs.getString("prenom"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPassword(rs.getString("mot_de_passe"));
                teacher.setDepartement(rs.getString("departement"));
                teacher.setSpecialite(rs.getString("specialite"));
                teacher.setTelephone(rs.getString("telephone"));
                teachers.add(teacher);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des enseignants : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'accès aux données des enseignants", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return teachers;
    }

    /**
     * Représente une classe (groupe d'étudiants) enseignée par un professeur.
     * Ceci est une classe interne simple pour transporter les données nécessaires.
     */
    public static class Classe {
        private int id;
        private String nom;

        public Classe(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        @Override
        public String toString() {
            return nom; // Useful for JList
        }
    }

    /**
     * Récupère la liste des classes (groupes) associées à un enseignant.
     * @param teacherId L'ID de l'enseignant.
     * @return Une liste d'objets Classe.
     */
    public List<Classe> getClassesByTeacherId(int teacherId) {
        List<Classe> classes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            if (conn == null) {
                throw new SQLException("Impossible d'établir une connexion à la base de données");
            }

            // SQL query to select group id and name directly from enseignant_groupes
            String sql = "SELECT eg.id, eg.groupe FROM gestion_universitaire.enseignant_groupes eg WHERE eg.enseignant_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("groupe");
                Classe classe = new Classe(id, nom);
                classes.add(classe);
                System.out.println("Fetched class: " + classe.getNom() + " for teacher ID: " + teacherId);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des classes de l'enseignant : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return classes;
    }
}
