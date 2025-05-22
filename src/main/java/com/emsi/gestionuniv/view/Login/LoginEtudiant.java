package com.emsi.gestionuniv.view.Login;

import javax.swing.*;                       // Importe toutes les classes Swing pour l’interface utilisateur
import javax.swing.border.EmptyBorder;     // Pour ajouter des marges internes
import java.awt.*;                         // Pour les classes graphiques générales (Color, Layout, etc.)
import java.awt.event.*;                   // Pour gérer les événements (clic, clavier, etc.)
import java.awt.geom.RoundRectangle2D;     // Pour dessiner des formes arrondies (ici pour les panels)
import java.awt.image.BufferedImage;       // Pour créer une image personnalisée en mémoire


import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.view.etudiant.EtudiantDashboard;

public class LoginEtudiant extends JFrame {
    // Couleurs du thème EMSI
    private static final Color EMSI_GREEN = new Color(0, 130, 65);
    private static final Color EMSI_DARK_GREEN = new Color(0, 100, 50);
    private static final Color EMSI_LIGHT_GREEN = new Color(225, 245, 225);
    private static final Color EMSI_ACCENT = new Color(46, 180, 100);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color BG_COLOR = new Color(245, 250, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    private JTextField matriculeField;
    private JPasswordField passwordField;

    public LoginEtudiant() {
        setTitle("EMSI - Connexion Étudiant");      // Titre de la fenêtre
        setSize(550, 600);                          // Dimensions fixes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ferme l’application à la fermeture
        setLocationRelativeTo(null);                // Centre la fenêtre sur l’écran
        setResizable(false);                        // Empêche le redimensionnement
        setIconImage(createEmsiLogo(32, 32));       // Définit l’icône de la fenêtre avec une image générée


        // Panel principal avec fond dégradé
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {      // Redéfinition pour peindre un fond personnalisé
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Lissage
                g2d.setColor(BG_COLOR);                      // Couleur de fond
                g2d.fillRect(0, 0, getWidth(), getHeight()); // Remplit tout le panel
            }
        };
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));     // Pas de marges internes

        // Création du header
        JPanel headerPanel = createHeaderPanel();  // Appelle la méthode qui retourne un JPanel contenant l’en-tête de l’interface (avec logo et titre)
        mainPanel.add(headerPanel, BorderLayout.NORTH);  // Ajoute le header en haut du panneau principal

        // Création du panel central avec le formulaire
        JPanel formPanel = createFormPanel();  // Appelle la méthode qui retourne le formulaire de connexion
        mainPanel.add(formPanel, BorderLayout.CENTER);  // Ajoute le formulaire au centre

        // Création du footer
        JPanel footerPanel = createFooterPanel();  // Appelle la méthode qui crée le pied de page
        mainPanel.add(footerPanel, BorderLayout.SOUTH);  // Ajoute le footer en bas

        add(mainPanel);  // Ajoute le panneau principal à la fenêtre
        setVisible(true);  // Affiche la fenêtre

    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;  // Conversion en Graphics2D pour des rendus avancés
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Anti-aliasing activé
                GradientPaint gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), 0, EMSI_DARK_GREEN);  // Dégradé de couleur horizontale
                g2d.setPaint(gradient);  // Applique le dégradé
                g2d.fillRect(0, 0, getWidth(), getHeight());  // Dessine le fond
            }
        };

        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Logo EMSI
        JLabel logoLabel = new JLabel(new ImageIcon(createEmsiLogo(40, 40)));  // Crée un logo circulaire avec un E au centre
        logoLabel.setText("EMSI");  // Ajoute le texte à côté du logo
        logoLabel.setForeground(WHITE);  // Couleur du texte
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));  // Police en gras
        headerPanel.add(logoLabel, BorderLayout.WEST);  // Positionne le logo à gauche


        // Titre
        JLabel titleLabel = new JLabel("Système de Gestion Universitaire", SwingConstants.CENTER);  // Titre centré
        titleLabel.setForeground(WHITE);  // Couleur blanche
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Style gras
        headerPanel.add(titleLabel, BorderLayout.CENTER);  // Ajoute le titre au centre


        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                        25, 25, getWidth() - 50, getHeight() - 50, 20, 20);
                g2d.fill(roundedRectangle);

                // Bordure légère
                g2d.setColor(new Color(230, 230, 230));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(roundedRectangle);
            }
        };
        formPanel.setOpaque(false);  // Laisse la transparence visible
        formPanel.setLayout(null);  // Positionnement absolu (par coordonnées)


        // Titre du formulaire
        JLabel formTitle = new JLabel("Connexion à votre espace étudiant", SwingConstants.CENTER);
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(EMSI_GREEN);
        formTitle.setBounds(50, 50, 450, 30);
        formPanel.add(formTitle);

        // Sous-titre
        JLabel subtitle = new JLabel("Entrez vos identifiants pour accéder à votre espace", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBounds(50, 85, 450, 20);
        formPanel.add(subtitle);

        // Label Matricule
        JLabel matriculeLabel = new JLabel("Matricule");
        matriculeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        matriculeLabel.setForeground(TEXT_COLOR);
        matriculeLabel.setBounds(100, 130, 100, 20);
        formPanel.add(matriculeLabel);

        // Champ Matricule
        matriculeField = new JTextField();
        matriculeField.setBounds(100, 155, 350, 50);
        styliserChampTexte(matriculeField, "Entrez votre matricule");
        formPanel.add(matriculeField);

        // Label Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setBounds(100, 220, 150, 20);
        formPanel.add(passwordLabel);

        // Champ Mot de passe
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 245, 350, 50);
        styliserChampTexte(passwordField, "••••••••");

        // Action : Touche "Entrée" déclenche la connexion
        passwordField.addActionListener(e -> handleLogin());
        formPanel.add(passwordField);

        // Bouton de connexion avec style personnalisé
        JButton loginButton = createModernLoginButton("SE CONNECTER");
        loginButton.setBounds(100, 320, 350, 50);
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton);

        // Lien mot de passe oublié
        JLabel forgotPassword = new JLabel("Mot de passe oublié ?", SwingConstants.CENTER);
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPassword.setForeground(EMSI_GREEN);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.setBounds(100, 390, 350, 20);
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginEtudiant.this,
                        "Veuillez contacter l'administration pour réinitialiser votre mot de passe.",
                        "Mot de passe oublié",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPassword.setText("<html><u>Mot de passe oublié ?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPassword.setText("Mot de passe oublié ?");
            }
        });
        formPanel.add(forgotPassword);

        return formPanel;
    }

    private void handleLogin() {
        // Récupération des valeurs des champs
        String matricule = matriculeField.getText().trim();
        String pwd = new String(passwordField.getPassword());

        // Vérification que les champs ne sont pas vides
        if (matricule.isEmpty() || pwd.isEmpty() ||
                matricule.equals("Entrez votre matricule") || pwd.equals("••••••••")) {
            showErrorDialog("Veuillez remplir tous les champs");
            return;
        }

        // Authentification de l'étudiant
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Student student = EtudiantService.findStudentByMatricule(matricule, pwd);

            if (student != null) {
                // Authentification réussie
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    new EtudiantDashboard(student).setVisible(true);
                });
            } else {
                showErrorDialog("Matricule ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showErrorDialog("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel copyrightLabel = new JLabel("© 2025 École Marocaine des Sciences de l'Ingénieur - Tous droits réservés");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(copyrightLabel);

        return footerPanel;
    }

    private void styliserChampTexte(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
                field.setForeground(TEXT_COLOR);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                field.setBackground(WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                field.setBackground(new Color(245, 245, 245));
            }
        });
    }

    private JButton createModernLoginButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(EMSI_DARK_GREEN);
                } else if (getModel().isRollover()) {
                    g2.setColor(EMSI_ACCENT);
                } else {
                    g2.setColor(EMSI_GREEN);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);

        return button;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private Image createEmsiLogo(int width, int height) {
        BufferedImage logo = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(EMSI_GREEN);
        g2d.fillOval(0, 0, width, height);

        g2d.setColor(WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, width / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth("E")) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString("E", x, y);

        g2d.dispose();
        return logo;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginEtudiant());
    }
}