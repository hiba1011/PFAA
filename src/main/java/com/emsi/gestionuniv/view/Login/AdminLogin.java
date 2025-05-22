// Déclaration du package
package com.emsi.gestionuniv.view.Login;

// Importation du tableau de bord administrateur
import com.emsi.gestionuniv.view.admin.AdminDashboard;

// Importation des composants Swing
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Classe principale pour la fenêtre de connexion administrateur
public class AdminLogin extends JFrame {
    // Champ de saisie du mot de passe
    private JPasswordField passwordField;

    // Boutons pour la connexion et le retour
    private JButton loginButton;
    private JButton backButton;

    // Mot de passe administrateur codé en dur (à sécuriser dans une vraie app)
    private static final String ADMIN_PASSWORD = "admin123";

    // Constructeur de la fenêtre de connexion
    public AdminLogin() {
        // Paramètres de base de la fenêtre
        setTitle("EMSI - Connexion Administrateur");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre
        setResizable(false);

        // Utilise un layout en grille avec contraintes
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Couleur de fond blanche
        getContentPane().setBackground(Color.WHITE);

        // ----------- En-tête avec logo et titre -----------

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0); // Marges externes

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Vertical

        // Logo en emoji
        JLabel logoLabel = new JLabel("🏫");
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre "EMSI"
        JLabel titleLabel = new JLabel("EMSI");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80)); // Vert EMSI
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sous-titre "Connexion Administrateur"
        JLabel subtitleLabel = new JLabel("Connexion Administrateur");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout des labels au panel
        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Ajout de l'en-tête à la fenêtre
        add(headerPanel, gbc);

        // ----------- Label du mot de passe -----------

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel passwordLabel = new JLabel("Mot de passe administrateur:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setForeground(Color.BLACK);
        add(passwordLabel, gbc);

        // ----------- Champ mot de passe -----------

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        add(passwordField, gbc);

        // ----------- Bouton Se connecter -----------

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 0, 5);
        gbc.fill = GridBagConstraints.NONE;

        loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(76, 175, 80)); // Vert EMSI
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setFocusPainted(false);

        // Action : déclenche la tentative de connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        add(loginButton, gbc);

        // ----------- Bouton Retour -----------

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 5, 0, 0);

        backButton = new JButton("Retour");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);
        backButton.setPreferredSize(new Dimension(80, 35));
        backButton.setFocusPainted(false);

        // Action : fermer la fenêtre
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Ferme la fenêtre
            }
        });

        add(backButton, gbc);

        // ----------- Focus automatique sur le champ -----------

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                passwordField.requestFocusInWindow();
            }
        });

        // Affiche la fenêtre
        setVisible(true);
    }

    // ----------------- Méthode de connexion -----------------

    private void attemptLogin() {
        String enteredPassword = new String(passwordField.getPassword());

        System.out.println("Mot de passe saisi: '" + enteredPassword + "'");

        // Vérifie si le champ est vide
        if (enteredPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez saisir votre mot de passe",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifie si le mot de passe est correct
        if (ADMIN_PASSWORD.equals(enteredPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Connexion réussie!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // Ferme la fenêtre de login et ouvre le dashboard admin
            dispose();
            new AdminDashboard("admin");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Mot de passe incorrect",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);

            // Réinitialise le champ
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    // ----------------- Méthode main pour lancer l'UI -----------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Applique le style du système
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new AdminLogin(); // Lance la fenêtre
            }
        });
    }
}
