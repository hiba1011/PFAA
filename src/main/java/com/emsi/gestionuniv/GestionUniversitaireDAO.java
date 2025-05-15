package com.emsi.gestionuniv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionUniversitaireDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestion_universitaire";
    private static final String USER = "root"; // À adapter
    private static final String PASS = ""; // À adapter

    public static void main(String[] args) {
        try {
            // Test de connexion
            testConnection();

            // Exemples d'utilisation
            // System.out.println(getAllEnseignants());
            // System.out.println(getEtudiant(1));
            // addCours("INFO306", "Base de données", "Fondements des bases de données relationnelles", 4, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== CONNEXION ====================
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");
        }
    }

    // ==================== ENSEIGNANTS ====================
    public static List<String> getAllEnseignants() throws SQLException {
        List<String> enseignants = new ArrayList<>();
        String query = "SELECT id, nom, prenom, email, departement FROM enseignants";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                enseignants.add(String.format("%d: %s %s (%s) - %s",
                        rs.getInt("id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("departement")));
            }
        }
        return enseignants;
    }

    public static boolean addEnseignant(String nom, String prenom, String email, String password,
                                        String departement, String specialite, String telephone) throws SQLException {
        String query = "INSERT INTO enseignants (nom, prenom, email, mot_de_passe, departement, specialite, telephone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, departement);
            ps.setString(6, specialite);
            ps.setString(7, telephone);

            return ps.executeUpdate() > 0;
        }
    }

    // ==================== ÉTUDIANTS ====================
    public static String getEtudiant(int id) throws SQLException {
        String query = "SELECT * FROM etudiants WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format("Étudiant %d: %s %s\nEmail: %s\nFilière: %s\nDate inscription: %s",
                            rs.getInt("id"),
                            rs.getString("prenom"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getString("filiere"),
                            rs.getDate("date_inscription"));
                }
            }
        }
        return "Étudiant non trouvé";
    }

    public static boolean updateEtudiantFiliere(int id, String nouvelleFiliere) throws SQLException {
        String query = "UPDATE etudiants SET filiere = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nouvelleFiliere);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    // ==================== COURS ====================
    public static boolean addCours(String code, String titre, String description, int credits, int enseignantId) throws SQLException {
        String query = "INSERT INTO cours (code, titre, description, credits, enseignant_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, code);
            ps.setString(2, titre);
            ps.setString(3, description);
            ps.setInt(4, credits);
            ps.setInt(5, enseignantId);

            return ps.executeUpdate() > 0;
        }
    }

    public static List<String> getCoursByEnseignant(int enseignantId) throws SQLException {
        List<String> cours = new ArrayList<>();
        String query = "SELECT id, code, titre FROM cours WHERE enseignant_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, enseignantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cours.add(String.format("%s (%s) - ID: %d",
                            rs.getString("titre"),
                            rs.getString("code"),
                            rs.getInt("id")));
                }
            }
        }
        return cours;
    }

    // ==================== INSCRIPTIONS ====================
    public static boolean inscrireEtudiant(int etudiantId, int coursId) throws SQLException {
        String query = "INSERT INTO inscriptions (etudiant_id, cours_id, date_inscription) VALUES (?, ?, CURDATE())";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, etudiantId);
            ps.setInt(2, coursId);

            return ps.executeUpdate() > 0;
        }
    }

    public static boolean updateNote(int inscriptionId, double note) throws SQLException {
        String query = "UPDATE inscriptions SET note = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDouble(1, note);
            ps.setInt(2, inscriptionId);

            return ps.executeUpdate() > 0;
        }
    }

    // ==================== PRÉSENCES ====================
    public static boolean marquerPresence(int etudiantId, int coursId, String date, String statut, String commentaires) throws SQLException {
        String query = "INSERT INTO presences (etudiant_id, cours_id, date, statut, commentaires) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, etudiantId);
            ps.setInt(2, coursId);
            ps.setString(3, date);
            ps.setString(4, statut);
            ps.setString(5, commentaires);

            return ps.executeUpdate() > 0;
        }
    }

    // ==================== SALLES ====================
    public static List<String> getSallesDisponibles(String date, String heureDebut, String heureFin) throws SQLException {
        List<String> salles = new ArrayList<>();
        String query = "SELECT s.id, s.numero, s.capacite, s.type, s.batiment " +
                "FROM salles s " +
                "WHERE s.id NOT IN (" +
                "    SELECT se.salle_id FROM sessions se " +
                "    WHERE se.date_debut <= ? AND se.date_fin >= ?" +
                ")";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            String dateTimeDebut = date + " " + heureDebut;
            String dateTimeFin = date + " " + heureFin;

            ps.setString(1, dateTimeFin);
            ps.setString(2, dateTimeDebut);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    salles.add(String.format("%s - %s (%s, capacité: %d)",
                            rs.getString("numero"),
                            rs.getString("batiment"),
                            rs.getString("type"),
                            rs.getInt("capacite")));
                }
            }
        }
        return salles;
    }

    // ==================== SESSIONS ====================
    public static boolean planifierSession(int coursId, int salleId, String dateDebut, String dateFin, String type) throws SQLException {
        String query = "INSERT INTO sessions (cours_id, salle_id, date_debut, date_fin, type_session) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, coursId);
            ps.setInt(2, salleId);
            ps.setString(3, dateDebut);
            ps.setString(4, dateFin);
            ps.setString(5, type);

            return ps.executeUpdate() > 0;
        }
    }
}