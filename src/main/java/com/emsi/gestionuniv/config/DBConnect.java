package com.emsi.gestionuniv.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    // URL de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_universitaire?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Méthode pour tester la connexion
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("✅ Connected to the database!");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Établit et retourne une connexion à la base de données
     * @return Connection objet de connexion à la base de données
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            // Chargement explicite du pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found!");
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }

        return connection;
    }
}