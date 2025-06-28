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
import java.util.stream.Collectors;

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
                teacher.setTelephone(rs.getString("telephone"));
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
        // S'assurer que la table existe
        ensureEnseignantGroupesTableExists();
        
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

    /**
     * Met à jour le profil de l'enseignant (téléphone et photo)
     */
    public void updateTeacherProfile(Teacher teacher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnect.getConnection();
            String sql = "UPDATE gestion_universitaire.enseignants SET telephone = ?, photo = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacher.getTelephone());
            pstmt.setString(2, teacher.getPhoto());
            pstmt.setInt(3, teacher.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du profil enseignant : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du profil enseignant", e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * Recherche des enseignants par nom, prénom, email, département, spécialité ou téléphone
     * @param searchTerm le terme de recherche
     * @return liste des enseignants correspondants
     */
    public List<Teacher> searchTeachers(String searchTerm) {
        List<Teacher> teachers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = "SELECT * FROM gestion_universitaire.enseignants WHERE " +
                    "nom LIKE ? OR prenom LIKE ? OR email LIKE ? OR departement LIKE ? OR specialite LIKE ? OR telephone LIKE ?";
            pstmt = conn.prepareStatement(sql);
            String pattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, pattern);
            }
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
            System.err.println("Erreur lors de la recherche d'enseignants: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return teachers;
    }

    /**
     * Recherche un enseignant par son identifiant unique (ID)
     * @param id identifiant de l'enseignant
     * @return l'objet Teacher trouvé ou null si aucun enseignant ne correspond
     */
    public Teacher findTeacherById(int id) {
        Teacher teacher = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnect.getConnection();
            String sql = "SELECT * FROM gestion_universitaire.enseignants WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setNom(rs.getString("nom"));
                teacher.setPrenom(rs.getString("prenom"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPassword(rs.getString("mot_de_passe"));
                teacher.setDepartement(rs.getString("departement"));
                teacher.setSpecialite(rs.getString("specialite"));
                teacher.setTelephone(rs.getString("telephone"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'enseignant par ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return teacher;
    }

    /**
     * Ajoute un nouvel enseignant dans la base de données
     * @param teacher l'enseignant à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean addTeacher(Teacher teacher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBConnect.getConnection();
            String sql = "INSERT INTO gestion_universitaire.enseignants (prenom, nom, email, mot_de_passe, departement, specialite, telephone) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacher.getPrenom());
            pstmt.setString(2, teacher.getNom());
            pstmt.setString(3, teacher.getEmail());
            pstmt.setString(4, teacher.getPassword());
            pstmt.setString(5, teacher.getDepartement());
            pstmt.setString(6, teacher.getSpecialite());
            pstmt.setString(7, teacher.getTelephone());
            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'enseignant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Met à jour les informations d'un enseignant
     * @param teacher l'enseignant avec les nouvelles informations (l'id doit être renseigné)
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateTeacher(Teacher teacher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBConnect.getConnection();
            String sql = "UPDATE gestion_universitaire.enseignants SET prenom=?, nom=?, email=?, mot_de_passe=?, departement=?, specialite=?, telephone=? WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacher.getPrenom());
            pstmt.setString(2, teacher.getNom());
            pstmt.setString(3, teacher.getEmail());
            pstmt.setString(4, teacher.getPassword());
            pstmt.setString(5, teacher.getDepartement());
            pstmt.setString(6, teacher.getSpecialite());
            pstmt.setString(7, teacher.getTelephone());
            pstmt.setInt(8, teacher.getId());
            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'enseignant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }


    public boolean deleteTeacher(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBConnect.getConnection();
            String sql = "DELETE FROM gestion_universitaire.enseignants WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            success = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'enseignant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        return success;
    }

    public int countTeachers() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM enseignants";
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

    public java.util.List<Teacher> getLastTeachers(int n) {
        java.util.List<Teacher> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM enseignants ORDER BY id DESC LIMIT ?";
        try (java.sql.Connection conn = com.emsi.gestionuniv.config.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Teacher t = new Teacher();
                    t.setId(rs.getInt("id"));
                    t.setNom(rs.getString("nom"));
                    t.setPrenom(rs.getString("prenom"));
                    t.setEmail(rs.getString("email"));
                    t.setPassword(rs.getString("mot_de_passe"));
                    t.setDepartement(rs.getString("departement"));
                    t.setSpecialite(rs.getString("specialite"));
                    t.setTelephone(rs.getString("telephone"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Vérifie et crée la table enseignant_groupes si elle n'existe pas
     */
    private void ensureEnseignantGroupesTableExists() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnect.getConnection();
            
            // Vérifier si la table existe
            String checkTableSql = "SHOW TABLES LIKE 'enseignant_groupes'";
            pstmt = conn.prepareStatement(checkTableSql);
            if (!pstmt.executeQuery().next()) {
                // La table n'existe pas, la créer
                String createTableSql = "CREATE TABLE gestion_universitaire.enseignant_groupes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "enseignant_id INT NOT NULL," +
                    "groupe VARCHAR(50) NOT NULL," +
                    "FOREIGN KEY (enseignant_id) REFERENCES enseignants(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_enseignant_groupe (enseignant_id, groupe)" +
                    ")";
                
                pstmt = conn.prepareStatement(createTableSql);
                pstmt.executeUpdate();
                System.out.println("Table enseignant_groupes créée avec succès");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table enseignant_groupes : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Ajoute des groupes à un enseignant
     * @param teacherId ID de l'enseignant
     * @param groupes Liste des noms de groupes à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean addGroupesToTeacher(int teacherId, List<String> groupes) {
        if (groupes == null || groupes.isEmpty()) {
            return true; // Rien à ajouter
        }
        
        // S'assurer que la table existe
        ensureEnseignantGroupesTableExists();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DBConnect.getConnection();
            // Ne pas spécifier l'ID s'il est auto-incrémenté
            String sql = "INSERT INTO gestion_universitaire.enseignant_groupes (enseignant_id, groupe) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            for (String groupe : groupes) {
                pstmt.setInt(1, teacherId);
                pstmt.setString(2, groupe);
                pstmt.executeUpdate();
            }
            success = true;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des groupes à l'enseignant : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        
        return success;
    }
    
    /**
     * Supprime tous les groupes d'un enseignant
     * @param teacherId ID de l'enseignant
     * @return true si la suppression a réussi, false sinon
     */
    public boolean removeAllGroupesFromTeacher(int teacherId) {
        // S'assurer que la table existe
        ensureEnseignantGroupesTableExists();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DBConnect.getConnection();
            String sql = "DELETE FROM gestion_universitaire.enseignant_groupes WHERE enseignant_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            pstmt.executeUpdate();
            success = true;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des groupes de l'enseignant : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt, conn);
        }
        
        return success;
    }
    
    /**
     * Met à jour les groupes d'un enseignant (supprime les anciens et ajoute les nouveaux)
     * @param teacherId ID de l'enseignant
     * @param nouveauxGroupes Liste des nouveaux groupes
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateTeacherGroupes(int teacherId, List<String> nouveauxGroupes) {
        // Supprimer tous les groupes existants
        if (!removeAllGroupesFromTeacher(teacherId)) {
            return false;
        }
        
        // Ajouter les nouveaux groupes
        return addGroupesToTeacher(teacherId, nouveauxGroupes);
    }
}
