package com.emsi.gestionuniv.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import com.emsi.gestionuniv.view.enseignant.EnseignantDashboard;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.model.user.Teacher;

/**
 * Classe principale de l'application qui implémente l'interface de connexion
 * pour le système de gestion universitaire de l'EMSI.
 * Cette classe crée une interface utilisateur moderne avec des composants personnalisés
 * et gère l'authentification des enseignants.
 */
public class MainApplication {
    // Couleurs officielles EMSI avec palette modernisée
    private static final Color EMSI_GREEN = new Color(0, 148, 68);       // Vert principal de la marque EMSI
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);  // Variante foncée pour les effets et dégradés
    private static final Color EMSI_GRAY = new Color(88, 88, 90);        // Gris secondaire pour les textes
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245); // Gris clair pour les fonds de champs
    private static final Color ACCENT_COLOR = new Color(183, 123, 91);   // Couleur d'accent pour les éléments à mettre en valeur

    // Nouvelles couleurs pour design moderne
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252); // Blanc cassé pour le fond général
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 10);     // Noir transparent pour les ombres
    private static final Color FOCUS_COLOR = new Color(0, 148, 68, 30);   // Vert transparent pour l'effet de focus

    // Polices standardisées pour l'interface
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font INPUT_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Point d'entrée de l'application.
     * Utilise SwingUtilities.invokeLater pour assurer que la création de l'interface
     * est exécutée dans le thread d'événements Swing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setCustomUIProperties();  // Configure le look and feel personnalisé
            createAndShowGUI();       // Crée et affiche l'interface principale
        });
    }

    /**
     * Définit des propriétés UI personnalisées pour un thème cohérent et moderne.
     * Cette méthode configure le look and feel global de l'application.
     */
    private static void setCustomUIProperties() {
        try {
            // Utilise le look and feel natif du système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configuration des propriétés communes des composants
            UIManager.put("Panel.background", BACKGROUND_WHITE);
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));

            // Style des champs de texte
            UIManager.put("TextField.background", EMSI_LIGHT_GRAY);
            UIManager.put("TextField.border", BorderFactory.createEmptyBorder());
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));

            // Style des champs de mot de passe
            UIManager.put("PasswordField.background", EMSI_LIGHT_GRAY);
            UIManager.put("PasswordField.border", BorderFactory.createEmptyBorder());
            UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));

            // Style des boutons
            UIManager.put("Button.focusPainted", Boolean.FALSE);  // Supprime le contour de focus par défaut
            UIManager.put("Button.borderPainted", Boolean.FALSE); // Supprime la bordure par défaut
            UIManager.put("Button.background", EMSI_GREEN);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée et affiche l'interface graphique principale.
     * Initialise la fenêtre et configure le panneau de login.
     */
    private static void createAndShowGUI() {
        // Initialisation du service d'authentification des enseignants
        TeacherService teacherService = new TeacherService();

        // Création de la fenêtre principale
        JFrame frame = new JFrame("EMSI - Système de Gestion Universitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(createEMSILogoImage());  // Définit l'icône personnalisée

        // Création du panneau de login et ajout à la fenêtre
        JPanel loginPanel = createLoginPanel(frame, teacherService);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));  // Marge autour du contenu
        contentPane.setBackground(BACKGROUND_WHITE);
        contentPane.add(loginPanel, BorderLayout.CENTER);

        // Configuration finale de la fenêtre
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setSize(new Dimension(1000, 650));     // Taille fixe pour l'écran de login
        frame.setLocationRelativeTo(null);           // Centre la fenêtre sur l'écran
        frame.setVisible(true);                      // Affiche la fenêtre
    }

    /**
     * Crée une image de logo EMSI modernisée.
     * Cette méthode génère dynamiquement un logo simple pour l'icône de l'application.
     *
     * @return Image du logo EMSI
     */
    private static Image createEMSILogoImage() {
        // Création d'une image vide avec canal alpha
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cercle extérieur vert
        g2d.setColor(EMSI_GREEN);
        g2d.fillOval(4, 4, 56, 56);

        // Initiale "E" stylisée en blanc
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("E", 22, 40);
        g2d.dispose();  // Libère les ressources graphiques

        return img;
    }

    /**
     * Crée le panneau de login avec design moderne.
     * Ce panneau principal contient les panneaux gauche et droit.
     *
     * @param frame La fenêtre parente
     * @param service Le service d'authentification
     * @return JPanel configuré pour le login
     */
    private static JPanel createLoginPanel(JFrame frame, TeacherService service) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_WHITE);

        // Redéfinition du panneau avec effet d'ombre pour un design moderne
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond arrondi avec légère ombre
                g2d.setColor(BACKGROUND_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Effet d'ombre subtil en augmentant progressivement l'opacité
                for (int i = 0; i < 8; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));  // Noir avec opacité croissante
                    g2d.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 20, 20);
                }
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);  // Rend le panneau transparent pour voir l'effet personnalisé

        // Ajout des deux panneaux principaux (gauche et droit)
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);        // Panneau décoratif à gauche
        mainPanel.add(createRightPanel(frame, service), BorderLayout.CENTER);  // Formulaire à droite

        return mainPanel;
    }

    /**
     * Panneau gauche avec design moderne.
     * Contient le logo et les informations EMSI avec un fond dégradé vert.
     *
     * @return JPanel configuré pour la partie gauche
     */
    private static JPanel createLeftPanel() {
        // Panneau avec rendu graphique personnalisé
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé de fond vert
                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_GREEN,         // Commence par le vert clair en haut
                        0, getHeight(), EMSI_DARK_GREEN  // Termine par le vert foncé en bas
                );
                g2d.setPaint(gradient);

                // Forme arrondie uniquement du côté gauche (bord droit droit)
                g2d.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(),
                        20, 20  // Rayon de l'arrondi
                ));

                // Éléments décoratifs (cercles semi-transparents)
                g2d.setColor(new Color(255, 255, 255, 30));  // Blanc semi-transparent
                int size = 300;
                g2d.fillOval(-50, getHeight()/2 - size/2, size, size);  // Grand cercle à gauche
                g2d.fillOval(getWidth()-100, getHeight()/3 - size/3, size/2, size/2);  // Petit cercle en haut à droite

                // Logo EMSI texte
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 38));
                g2d.drawString("EMSI", getWidth()/2 - 50, getHeight()/2 - 80);

                // Ligne séparatrice
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine(getWidth()/2 - 70, getHeight()/2 - 60, getWidth()/2 + 70, getHeight()/2 - 60);

                // Nom complet de l'école
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String text = "École Marocaine des Sciences de l'Ingénieur";
                g2d.drawString(text, getWidth()/2 - g2d.getFontMetrics().stringWidth(text)/2, getHeight()/2 - 30);

                // Message d'accueil
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String welcome = "Système de Gestion Universitaire";
                g2d.drawString(welcome, getWidth()/2 - g2d.getFontMetrics().stringWidth(welcome)/2, getHeight()/2 + 30);

                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(400, 600));  // Taille fixe pour le panneau gauche
        panel.setOpaque(false);  // Transparent pour voir le rendu personnalisé

        return panel;
    }

    /**
     * Panneau droit avec formulaire et logique d'authentification.
     * Contient les champs de saisie et le bouton de connexion.
     *
     * @param frame La fenêtre parente
     * @param service Le service d'authentification des enseignants
     * @return JPanel configuré pour la partie formulaire
     */
    private static JPanel createRightPanel(JFrame frame, TeacherService service) {
        // Panneau vertical avec disposition BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(80, 50, 80, 50));  // Marges importantes pour l'espace
        panel.setOpaque(false);

        // Titre de connexion
        JLabel titleLabel = new JLabel("Bienvenue");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centré horizontalement
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));  // Espace vertical fixe

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Connectez-vous à votre compte");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(EMSI_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);

        panel.add(Box.createVerticalStrut(60));  // Grand espace avant le formulaire

        // ----- Champ Email -----
        // Label email
        JLabel emailLabel = new JLabel("Adresse e-mail");
        emailLabel.setFont(INPUT_LABEL_FONT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);  // Aligné à gauche
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(10));

        // Panneau conteneur pour le champ email (gère la taille)
        JPanel emailFieldPanel = new JPanel(new BorderLayout());
        emailFieldPanel.setOpaque(false);
        emailFieldPanel.setMaximumSize(new Dimension(400, 50));  // Limite la hauteur maximale
        emailFieldPanel.setPreferredSize(new Dimension(350, 50));
        emailFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Création du champ email personnalisé avec placeholder
        JTextField emailField = createModernTextField("email@emsi.ma");
        emailFieldPanel.add(emailField);
        panel.add(emailFieldPanel);

        panel.add(Box.createVerticalStrut(25));  // Espace entre les champs

        // ----- Champ Mot de passe -----
        // Label mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(INPUT_LABEL_FONT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(10));

        // Panneau conteneur pour le champ mot de passe
        JPanel passwordFieldPanel = new JPanel(new BorderLayout());
        passwordFieldPanel.setOpaque(false);
        passwordFieldPanel.setMaximumSize(new Dimension(400, 50));
        passwordFieldPanel.setPreferredSize(new Dimension(350, 50));
        passwordFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Création du champ mot de passe personnalisé
        JPasswordField passwordField = createModernPasswordField();
        passwordFieldPanel.add(passwordField);
        panel.add(passwordFieldPanel);

        panel.add(Box.createVerticalStrut(20));

        // Option "Se souvenir de moi"
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rememberPanel.setOpaque(false);
        rememberPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox rememberCheckbox = new JCheckBox("Se souvenir de moi");
        rememberCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheckbox.setOpaque(false);
        rememberPanel.add(rememberCheckbox);

        panel.add(rememberPanel);
        panel.add(Box.createVerticalStrut(40));  // Espace avant le bouton

        // Bouton de connexion avec style personnalisé
        JButton loginButton = createModernLoginButton("SE CONNECTER");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(400, 50));
        loginButton.setPreferredSize(new Dimension(350, 50));

        // Événement de clic sur le bouton de connexion
        loginButton.addActionListener(e -> {
            // Récupération des valeurs des champs
            String email = emailField.getText();
            String pwd = new String(passwordField.getPassword());

            // Vérification que les champs ne sont pas vides
            if (email.isEmpty() || pwd.isEmpty()) {
                showErrorDialog(frame, "Veuillez remplir tous les champs");
                return;
            }

            // Authentification de l'enseignant
            Teacher teacher = service.findByEmail(email);
            if (teacher == null || !service.authenticate(email, pwd)) {
                showErrorDialog(frame, "Email ou mot de passe incorrect");
                return;
            }

            // Authentification réussie : fermeture et ouverture du tableau de bord
            frame.dispose();  // Ferme la fenêtre de login
            SwingUtilities.invokeLater(() -> new EnseignantDashboard(teacher).setVisible(true));
        });
        panel.add(loginButton);

        // Lien "Mot de passe oublié"
        panel.add(Box.createVerticalStrut(15));
        JLabel forgotPassword = new JLabel("Mot de passe oublié ?");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPassword.setForeground(EMSI_GREEN);  // Couleur EMSI pour le lien
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Curseur main pour indiquer que c'est cliquable
        forgotPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(forgotPassword);

        // Espace flexible en bas du formulaire
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Affiche une boîte de dialogue d'erreur moderne.
     *
     * @param parent La fenêtre parente
     * @param message Le message d'erreur à afficher
     */
    private static void showErrorDialog(JFrame parent, String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},  // Pas de boutons personnalisés
                null
        );

        JDialog dialog = optionPane.createDialog(parent, "Erreur");
        dialog.setVisible(true);
    }

    /**
     * Crée un champ de texte moderne avec placeholder.
     * Le placeholder disparaît quand le champ prend le focus et réapparaît s'il est vide.
     *
     * @param placeholder Texte indicatif à afficher quand le champ est vide
     * @return JTextField personnalisé
     */
    private static JTextField createModernTextField(String placeholder) {
        // Extension anonyme de JTextField avec fonctionnalités personnalisées
        JTextField field = new JTextField(20) {
            // Variable pour suivre l'état du placeholder
            private boolean showingPlaceholder = true;

            // Bloc d'initialisation d'instance
            {
                setText(placeholder);  // Définit le placeholder initial
                setForeground(Color.GRAY);  // Texte gris pour indiquer que c'est un placeholder

                // Gestion des événements de focus pour le placeholder
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (showingPlaceholder) {
                            setText("");  // Efface le placeholder
                            setForeground(Color.BLACK);  // Couleur normale pour le texte saisi
                            showingPlaceholder = false;
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) {
                            setText(placeholder);  // Restaure le placeholder si vide
                            setForeground(Color.GRAY);
                            showingPlaceholder = true;
                        }
                    }
                });
            }

            // Personnalisation du rendu graphique
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Fond arrondi gris clair
                    g2d.setColor(EMSI_LIGHT_GRAY);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                    // Effet de focus : contour vert quand le champ a le focus
                    if (hasFocus()) {
                        g2d.setColor(FOCUS_COLOR);
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    }

                    g2d.dispose();
                }
                super.paintComponent(g);  // Dessine le texte par-dessus
            }
        };

        // Configuration commune
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setOpaque(false);  // Transparent pour voir notre rendu personnalisé
        field.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(),  // Pas de bordure externe
                BorderFactory.createEmptyBorder(12, 15, 12, 15)  // Marge interne pour le texte
        ));

        return field;
    }

    /**
     * Crée un champ de mot de passe moderne.
     * Similaire au champ texte mais adapté pour les mots de passe.
     *
     * @return JPasswordField personnalisé
     */
    private static JPasswordField createModernPasswordField() {
        // Extension anonyme de JPasswordField avec style personnalisé
        JPasswordField field = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Fond arrondi gris clair
                    g2d.setColor(EMSI_LIGHT_GRAY);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                    // Effet de focus
                    if (hasFocus()) {
                        g2d.setColor(FOCUS_COLOR);
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    }

                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };

        // Configuration commune
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setOpaque(false);
        field.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        return field;
    }

    /**
     * Crée un bouton de connexion moderne avec effet de survol.
     * Le bouton a un dégradé de couleur qui change selon son état.
     *
     * @param text Texte à afficher sur le bouton
     * @return JButton personnalisé
     */
    private static JButton createModernLoginButton(String text) {
        // Extension anonyme de JButton avec style personnalisé
        JButton button = new JButton(text) {
            // État du bouton pour l'effet de survol
            private boolean hovering = false;

            // Bloc d'initialisation d'instance
            {
                // Ajout de l'écouteur pour suivre le survol de la souris
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;  // La souris est sur le bouton
                        repaint();  // Redessine le bouton avec l'effet de survol
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovering = false;  // La souris quitte le bouton
                        repaint();  // Redessine le bouton à l'état normal
                    }
                });
            }

            // Personnalisation du rendu graphique
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé différent selon l'état du bouton (pressé, survolé, normal)
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    // Bouton pressé : vert foncé uniforme
                    gradient = new GradientPaint(0, 0, EMSI_DARK_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                } else if (hovering) {
                    // Bouton survolé : dégradé de vert clair à vert foncé
                    gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                } else {
                    // État normal : dégradé vert clair à vert moyen
                    gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), new Color(0, 130, 60));
                }

                // Application du dégradé et dessin du fond arrondi
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Effet de brillance sur le dessus pour un effet 3D subtil (seulement à l'état normal)
                if (!getModel().isPressed() && !hovering) {
                    g2d.setPaint(new GradientPaint(
                            0, 0, new Color(255, 255, 255, 50),  // Blanc semi-transparent en haut
                            0, getHeight()/2, new Color(255, 255, 255, 0)  // Transparent au milieu
                    ));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                }

                // Calcul de la position du texte pour le centrer parfaitement
                g2d.setFont(BUTTON_FONT);
                FontMetrics m = g2d.getFontMetrics();
                int x = (getWidth() - m.stringWidth(getText())) / 2;
                int y = ((getHeight() - m.getHeight()) / 2) + m.getAscent();

                // Ombre du texte très légère pour effet 3D
                g2d.setColor(new Color(0, 0, 0, 30));  // Noir très transparent
                g2d.drawString(getText(), x+1, y+1);  // Légèrement décalé

                // Texte principal en blanc
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);

                g2d.dispose();  // Libération des ressources graphiques
            }
        };

        // Configuration commune du bouton
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);  // Désactive le remplissage standard
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Curseur main pour indiquer que c'est cliquable

        return button;
    }
}