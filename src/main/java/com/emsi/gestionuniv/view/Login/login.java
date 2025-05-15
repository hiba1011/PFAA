/**
 * Classe représentant l'interface de connexion pour les enseignants
 * Cette classe gère l'authentification des enseignants au système de gestion universitaire
 */
package com.emsi.gestionuniv.view.Login;

import javax.swing.*;
import java.awt.*;

import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.view.enseignant.EnseignantDashboard;

public class login extends JFrame {
    // Composants de l'interface utilisateur
    private JTextField emailField;        // Champ pour saisir l'email
    private JPasswordField passwordField; // Champ sécurisé pour le mot de passe
    private JButton loginButton;          // Bouton de connexion
    private JLabel statusLabel;           // Affichage des messages d'erreur/statut

    // Service pour l'authentification des enseignants
    private TeacherService teacherService;

    /**
     * Constructeur qui initialise et configure l'interface de connexion
     */
    public login() {
        // Initialisation du service d'authentification
        teacherService = new TeacherService();

        // Configuration de la fenêtre principale
        setTitle("Login Enseignant - Gestion Universitaire");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centre la fenêtre sur l'écran

        // Panneau principal avec une bordure vide pour l'espacement
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Section d'en-tête avec le titre de l'application
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("SYSTÈME DE GESTION UNIVERSITAIRE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        // Formulaire de connexion avec disposition en grille
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        formPanel.add(emailField);
        formPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField);
        formPanel.add(new JLabel(""));  // Cellule vide pour l'alignement

        // Configuration du bouton de connexion
        loginButton = new JButton("Se connecter");
        loginButton.setBackground(new Color(70, 130, 180));  // Couleur bleu acier
        loginButton.setForeground(Color.WHITE);              // Texte blanc
        loginButton.setFocusPainted(false);                  // Désactive l'effet de focus
        loginButton.addActionListener(e -> authenticateTeacher());  // Gestionnaire d'événement
        formPanel.add(loginButton);

        // Panneau pour afficher les messages d'erreur/statut
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);  // Messages d'erreur en rouge
        statusPanel.add(statusLabel);

        // Assemblage des panneaux dans le panneau principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    /**
     * Méthode pour authentifier l'enseignant avec les informations saisies
     * Vérifie si les champs sont remplis, si l'utilisateur existe et si les identifiants sont corrects
     */
    private void authenticateTeacher() {
        // Récupération des valeurs saisies
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Vérification que les champs sont remplis
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        // Recherche de l'enseignant dans la base de données
        Teacher teacher = teacherService.findByEmail(email);
        if (teacher == null) {
            statusLabel.setText("Enseignant non trouvé");
            return;
        }

        // Vérification du mot de passe
        if (!teacherService.authenticate(email, password)) {
            statusLabel.setText("Email ou mot de passe incorrect");
            return;
        }

        // Authentification réussie
        statusLabel.setText("");
        JOptionPane.showMessageDialog(this, "Connexion réussie !");

        // Ouverture du tableau de bord enseignant et fermeture de la fenêtre de connexion
        SwingUtilities.invokeLater(() -> new EnseignantDashboard(teacher).setVisible(true));
        dispose();  // Ferme la fenêtre de connexion
    }

    /**
     * Point d'entrée principal de l'application
     * Configure le look and feel du système et lance l'interface de connexion
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Utilisation du look and feel du système d'exploitation
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Création de l'interface de connexion
            new login();
        });
    }
}