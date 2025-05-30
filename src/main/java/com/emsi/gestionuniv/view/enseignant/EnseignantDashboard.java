package com.emsi.gestionuniv.view.enseignant;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import com.emsi.gestionuniv.model.user.Teacher;

/**
 * Tableau de bord pour les enseignants de l'application de gestion universitaire.
 * Cette classe crée une interface graphique moderne pour les enseignants
 * avec une barre latérale de navigation, un en-tête et un espace principal de contenu.
 */
public class EnseignantDashboard extends JFrame {
    // L'objet enseignant connecté
    private Teacher teacher;

    // Définition des couleurs de la charte graphique EMSI
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 10);

    // Définition des polices utilisées dans l'interface
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font STAT_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font STAT_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Panel principal de contenu qui peut être modifié selon la navigation
    private JPanel contentPanel;

    /**
     * Constructeur qui initialise le tableau de bord avec les informations de l'enseignant
     * @param teacher L'objet Teacher représentant l'enseignant connecté
     */
    public EnseignantDashboard(Teacher teacher) {
        this.teacher = teacher;

        // Vérifier que l'enseignant est valide
        if (teacher == null) {
            JOptionPane.showMessageDialog(this, "Enseignant non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Configuration de la fenêtre principale
        setTitle("Tableau de bord - " + teacher.getFullName());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setBackground(BACKGROUND_WHITE);

        // Appliquer le look and feel moderne
        setUIProperties();

        // Initialiser et assembler les composants de l'interface
        createDashboardContent();
    }

    /**
     * Configure le look and feel de l'application pour une apparence moderne
     */
    private void setUIProperties() {
        try {
            // Utiliser le look and feel du système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Personnaliser certains éléments UI
            UIManager.put("Panel.background", BACKGROUND_WHITE);
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", MENU_FONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée et assemble tous les éléments du tableau de bord
     */
    private void createDashboardContent() {
        // Panel principal avec layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Créer les composants principaux de l'interface
        JPanel headerPanel = createHeaderPanel();    // En-tête (haut)
        JPanel sidebarPanel = createSidebarPanel();  // Barre latérale (gauche)
        contentPanel = createContentPanel();         // Contenu principal (centre)

        // Assemblage des composants dans le panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Crée l'en-tête du tableau de bord avec un dégradé de couleur,
     * le nom de l'enseignant et des boutons de notifications
     * @return Le panel d'en-tête configuré
     */
    private JPanel createHeaderPanel() {
        // Panel avec un rendu graphique personnalisé pour le dégradé
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé de fond vert EMSI
                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_GREEN,
                        getWidth(), 0, EMSI_DARK_GREEN
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Motifs graphiques décoratifs (cercles semi-transparents)
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(-20, -20, 150, 150);
                g2d.fillOval(getWidth() - 100, getHeight() - 40, 80, 80);

                g2d.dispose();
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Informations de l'enseignant (nom et département)
        JLabel welcomeLabel = new JLabel("Bienvenue, " + teacher.getFullName() + "!");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);

        JLabel detailsLabel = new JLabel("Département: " + teacher.getDepartement() + " | Spécialité: " + teacher.getSpecialite());
        detailsLabel.setFont(SUBTITLE_FONT);
        detailsLabel.setForeground(new Color(255, 255, 255, 220));

        // Panel pour les boutons de notifications à droite
        JPanel notifPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notifPanel.setOpaque(false);

        // Création des boutons avec des icônes Unicode
        JButton notifButton = createIconButton("\uD83D\uDD14", "Notifications"); // Cloche Unicode
        JButton profileButton = createIconButton("\uD83D\uDC64", "Profil"); // Icône utilisateur Unicode

        notifPanel.add(notifButton);
        notifPanel.add(profileButton);

        // Organisation des informations de l'enseignant
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(welcomeLabel);
        infoPanel.add(detailsLabel);

        // Ajout des composants à l'en-tête
        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(notifPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Crée un bouton d'icône avec un effet de survol pour l'en-tête
     * @param unicode Caractère Unicode représentant l'icône
     * @param tooltip Texte d'info-bulle
     * @return Le bouton configuré
     */
    private JButton createIconButton(String unicode, String tooltip) {
        // Bouton personnalisé avec effet de survol
        JButton button = new JButton(unicode) {
            // État du bouton (survolé ou non)
            private boolean hovering = false;

            {
                // Écouteur pour détecter le survol de la souris
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Rendu personnalisé du bouton
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond du bouton (transparent ou semi-transparent si survolé)
                if (hovering) {
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Dessiner le texte (l'icône unicode)
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        // Configuration du bouton
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(40, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Crée la barre latérale avec le menu de navigation
     * @return Le panel de la barre latérale
     */
    private JPanel createSidebarPanel() {
        // Panel avec ombre à droite
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond du panneau latéral
                g2d.setColor(BACKGROUND_WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effet d'ombre à droite du panneau
                for (int i = 0; i < 6; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawLine(getWidth() - i, 0, getWidth() - i, getHeight());
                }

                g2d.dispose();
            }
        };
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        // Logo EMSI en haut de la barre latérale
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel logoLabel = new JLabel("EMSI");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(EMSI_GREEN);
        logoPanel.add(logoLabel);

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Espace vertical

        // Options du menu avec icônes Unicode
        String[] menuOptions = {
                "Tableau de bord",
                "Mes cours",
                "Mes listes",
                "Gestion des notes",
                "Planning",
                "Communications",
                "Mon profil",

        };

        // Création des boutons du menu
        for (String option : menuOptions) {
            JButton menuButton = createMenuButton(option);
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 12))); // Espacement entre boutons
        }

        sidebarPanel.add(Box.createVerticalGlue()); // Pousse le bouton déconnexion vers le bas

        // Bouton déconnexion en bas de la barre latérale
        JButton logoutButton = createMenuButton(" Déconnexion");
        logoutButton.addActionListener(e -> {
            dispose(); // Ferme la fenêtre actuelle
            // SwingUtilities.invokeLater(() -> new login().setVisible(true)); // Ouvre la fenêtre de connexion
        });

        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }

    /**
     * Crée un bouton stylisé pour le menu de navigation
     * @param text Texte du bouton (avec icône Unicode)
     * @return Le bouton configuré
     */
    private JButton createMenuButton(String text) {
        // Bouton avec rendu personnalisé
        JButton button = new JButton(text) {
            // Le bouton "Tableau de bord" est sélectionné par défaut
            private boolean selected = text.equals("Tableau de bord");
            private boolean hovering = false;

            {
                // Détection du survol
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Rendu personnalisé du bouton
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond du bouton selon l'état (sélectionné, survolé ou normal)
                if (selected) {
                    g2d.setColor(EMSI_GREEN);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (hovering) {
                    g2d.setColor(EMSI_LIGHT_GRAY);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Texte du bouton (aligné à gauche)
                g2d.setFont(MENU_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int x = 12; // Padding à gauche
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2d.setColor(selected ? Color.WHITE : EMSI_GRAY);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };

        // Configuration du bouton
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        // Gestion du clic sur le bouton
        button.addActionListener(e -> handleMenuAction(text));

        return button;
    }

    /**
     * Crée le panel principal de contenu du tableau de bord
     * @return Le panel de contenu configuré
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre du contenu
        JLabel contentTitle = new JLabel("Tableau de bord");
        contentTitle.setFont(HEADING_FONT);
        contentTitle.setForeground(EMSI_GRAY);

        // Zone de recherche avec style moderne
        final JTextField searchField = new JTextField(20) {
            {
                setOpaque(false);
                setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Rendu personnalisé pour un champ arrondi
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        searchField.setText("Rechercher...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Rechercher...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Panel pour le titre et la recherche
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(contentTitle, BorderLayout.WEST);
        titlePanel.add(searchField, BorderLayout.EAST);

        // Panneaux de statistiques (4 cartes)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Création des cartes de statistiques avec icônes Unicode
        statsPanel.add(createModernStatPanel("Cours", "5", "\uD83D\uDCDA")); // 📚
        statsPanel.add(createModernStatPanel("Étudiants", "120", "\uD83D\uDC64")); // 👤
        statsPanel.add(createModernStatPanel("Notes en attente", "45", "\uD83D\uDCCB")); // 📋
        statsPanel.add(createModernStatPanel("Messages", "12", "\uD83D\uDCE8")); // 📨

        // Panneau pour les dernières activités
        JPanel activitiesPanel = createActivitiesPanel();

        // Panneau pour le calendrier et les tâches
        JPanel calendarPanel = createCalendarPanel();

        // Organisation des panneaux d'information (2 colonnes)
        JPanel dashboardContent = new JPanel(new GridLayout(1, 2, 15, 0));
        dashboardContent.setOpaque(false);
        dashboardContent.add(activitiesPanel);
        dashboardContent.add(calendarPanel);

        // Assemblage final du contenu
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(dashboardContent, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crée une carte de statistique avec un style moderne
     * @param title Titre de la statistique
     * @param value Valeur numérique à afficher
     * @param icon Icône Unicode
     * @return Panel de la carte configurée
     */
    private JPanel createModernStatPanel(String title, String value, String icon) {
        // Panel avec coins arrondis et ombre
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Effet d'ombre légère
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 12, 12);
                }

                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);

        // En-tête avec icône et titre
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLabel.setForeground(EMSI_GREEN);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(STAT_TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Valeur de la statistique (grand nombre)
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(STAT_VALUE_FONT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(new Color(50, 50, 50));

        // Assemblage de la carte
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée le panneau des dernières activités
     * @return Panel configuré
     */
    private JPanel createActivitiesPanel() {
        // Panel avec coins arrondis et ombre
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Légère ombre
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 12, 12);
                }

                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 300));

        // Titre du panneau
        JLabel titleLabel = new JLabel("Dernières activités");
        titleLabel.setFont(STAT_TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        // Liste verticale des activités
        JPanel activitiesList = new JPanel();
        activitiesList.setLayout(new BoxLayout(activitiesList, BoxLayout.Y_AXIS));
        activitiesList.setOpaque(false);

        // Données d'exemple pour les activités
        String[][] activities = {
                {"Aujourd'hui", "Notes du module Java mises à jour"},
                {"Aujourd'hui", "Nouveau message de l'administration"},
                {"Hier", "Planning du semestre ajouté"},
                {"29/04/2025", "Réunion département planifiée"}
        };

        // Création des éléments d'activité
        for (String[] activity : activities) {
            activitiesList.add(createActivityItem(activity[0], activity[1]));
            activitiesList.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement vertical
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(activitiesList, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un élément d'activité pour la liste des activités récentes
     * @param date Date de l'activité
     * @param description Description de l'activité
     * @return Panel configuré pour l'activité
     */
    private JPanel createActivityItem(String date, String description) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Étiquette de date (à gauche)
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(EMSI_GREEN);
        dateLabel.setPreferredSize(new Dimension(80, 20));

        // Description de l'activité (au centre)
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(dateLabel, BorderLayout.WEST);
        panel.add(descLabel, BorderLayout.CENTER);

        // Séparateur horizontal sous chaque activité
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        panel.add(separator, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crée le panneau du planning de la semaine
     * @return Panel configuré
     */
    private JPanel createCalendarPanel() {
        // Panel avec coins arrondis et ombre
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Légère ombre
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 12, 12);
                }

                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 300));

        // Titre du panneau
        JLabel titleLabel = new JLabel("Planning de la semaine");
        titleLabel.setFont(STAT_TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        // Contenu du planning
        JPanel planningContent = new JPanel();
        planningContent.setLayout(new BoxLayout(planningContent, BoxLayout.Y_AXIS));
        planningContent.setOpaque(false);

        // Données d'exemple pour les cours
        String[][] courses = {
                {"Lundi 25/04", "08:30 - 12:30", "Algorithmes et Structures de Données"},
                {"Mardi 26/04", "14:00 - 17:30", "Programmation Java"},
                {"Mercredi 27/04", "09:00 - 12:00", "Bases de données avancées"},
                {"Jeudi 28/04", "14:00 - 18:00", "Intelligence Artificielle"}
        };

        // Création des éléments du planning
        for (String[] course : courses) {
            planningContent.add(createPlanningItem(course[0], course[1], course[2]));
            planningContent.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement vertical
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(planningContent, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un élément de cours pour le planning
     * @param day Jour du cours
     * @param hours Heures du cours
     * @param course Nom du cours
     * @return Panel configuré pour le cours
     */
    private JPanel createPlanningItem(String day, String hours, String course) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Panel pour le jour et l'heure (à gauche)
        JPanel timePanel = new JPanel(new GridLayout(2, 1));
        timePanel.setOpaque(false);

        JLabel dayLabel = new JLabel(day);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dayLabel.setForeground(EMSI_GRAY);

        JLabel hoursLabel = new JLabel(hours);
        hoursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hoursLabel.setForeground(EMSI_GREEN);

        timePanel.add(dayLabel);
        timePanel.add(hoursLabel);

        // Intitulé du cours (au centre)
        JLabel courseLabel = new JLabel(course);
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(timePanel, BorderLayout.WEST);
        panel.add(courseLabel, BorderLayout.CENTER);

        // Séparateur horizontal sous chaque cours
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        panel.add(separator, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Gère les actions du menu de navigation
     * @param option Texte de l'option de menu sélectionnée
     */
    private void handleMenuAction(String option) {
        // Extrait le titre de l'option (supprime l'icône)
        String title = option.substring(option.indexOf(" ") + 1);

        // Gestion spéciale pour la déconnexion
        if (option.equals("🚪 Déconnexion")) {
            dispose(); // Ferme la fenêtre actuelle
            // SwingUtilities.invokeLater(() -> new login().setVisible(true)); // Ouvre l'écran de connexion
        } else {
            // Pour les autres options, affiche un message temporaire
            JOptionPane.showMessageDialog(this,
                    "Fonctionnalité '" + title + "' en cours de développement.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}