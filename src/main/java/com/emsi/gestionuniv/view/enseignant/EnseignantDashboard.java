package com.emsi.gestionuniv.view.enseignant;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.model.academic.Note;
import com.emsi.gestionuniv.service.AbscenceService;
import com.emsi.gestionuniv.service.NoteService;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.config.DBConnect;
import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.view.Login.EnseignantLogin;
import com.emsi.gestionuniv.service.TeacherService.Classe;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.model.user.Student;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import com.emsi.gestionuniv.model.planning.Emploi_de_temps;
import com.emsi.gestionuniv.service.EmploiDuTempsPdfService;
import java.text.DecimalFormat; // Ajoute cet import en haut du fichier
import java.io.File;
import com.emsi.gestionuniv.model.academic.Message;
import com.emsi.gestionuniv.service.MessageService;

import static com.emsi.gestionuniv.view.etudiant.EtudiantDashboard.EMSI_LIGHT_GREEN;

/**
 * Tableau de bord pour les enseignants de l'application de gestion
 * universitaire.
 * Cette classe crée une interface graphique moderne pour les enseignants
 * avec une barre latérale de navigation, un en-tête et un espace principal de
 * contenu.
 */
public class EnseignantDashboard extends JFrame {
    CoursService coursService = new CoursService();
    private static final long serialVersionUID = 1L;
    private Teacher currentTeacher;
    private int currentTeacherId;
    private int currentCoursId = -1; // Initialiser avec une valeur par défaut

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
    private int lastSeenMessageCount = -1;
    private JPanel messagesMenuItem;

    /**
     * Constructeur qui initialise le tableau de bord avec les informations de
     * l'enseignant
     * 
     * @param teacher L'objet Teacher représentant l'enseignant connecté
     */
    public EnseignantDashboard(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("L'enseignant ne peut pas être null");
        }

        this.teacher = teacher;
        this.currentTeacher = teacher;
        this.currentTeacherId = teacher.getId();

        setTitle("Tableau de bord enseignant - " + teacher.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Créer le panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Ajouter la barre latérale
        mainPanel.add(createSidebarPanel(), BorderLayout.WEST);

        // Créer et afficher le panel par défaut (Profil de l'enseignant)
        contentPanel = new JPanel(new BorderLayout()); // Initialize contentPanel
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Afficher le profil par défaut
        showProfilePanel();

        add(mainPanel);
        // Initialiser le panneau de contenu pour qu'il soit prêt à être mis à jour
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private Component createSidebar() {
        // Supprimer cette méthode car elle n'est pas utilisée
        return null;
    }

    /**
     * Displays the teacher's profile panel.
     */
    private void showProfilePanel() {
        contentPanel.removeAll();
        contentPanel.add(createProfilePanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates the teacher's profile panel.
     * 
     * @return The profile panel.
     */
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Mon Profil");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(40));

        // Display teacher information
        addProfileInfo(panel, "Nom Complet:", teacher.getFullName());
        addProfileInfo(panel, "Email:", teacher.getEmail());
        addProfileInfo(panel, "Département:", teacher.getDepartement());
        addProfileInfo(panel, "Spécialité:", teacher.getSpecialite());

        panel.add(Box.createVerticalGlue()); // Push everything to the top

        return panel;
    }

    /**
     * Helper method to add a label and value to the profile panel.
     */
    private void addProfileInfo(JPanel parentPanel, String labelText, String valueText) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(EMSI_GRAY);
        label.setPreferredSize(new Dimension(150, 20)); // Align labels

        JLabel value = new JLabel(valueText);
        value.setFont(SUBTITLE_FONT);
        value.setForeground(Color.BLACK);

        infoPanel.add(label);
        infoPanel.add(value);

        parentPanel.add(infoPanel);
        parentPanel.add(Box.createVerticalStrut(10)); // Spacing
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
        JPanel headerPanel = createHeaderPanel(); // En-tête (haut)
        JPanel sidebarPanel = createSidebarPanel(); // Barre latérale (gauche)
        contentPanel = createContentPanel(); // Contenu principal (centre)

        // Assemblage des composants dans le panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Crée l'en-tête du tableau de bord avec un dégradé de couleur,
     * le nom de l'enseignant et des boutons de notifications
     * 
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
                        getWidth(), 0, EMSI_DARK_GREEN);
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

        JLabel detailsLabel = new JLabel(
                "Département: " + teacher.getDepartement() + " | Spécialité: " + teacher.getSpecialite());
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
    private int getTotalReceivedMessagesCount() {
    MessageService messageService = new MessageService();
    int total = 0;
    EtudiantService etudiantService = new EtudiantService();
    List<Student> students = etudiantService.getStudentsByTeacherId(currentTeacherId);
    for (Student s : students) {
        List<Message> conv = messageService.getConversation(
            s.getId(), "etudiant", currentTeacherId, "enseignant"
        );
        total += conv.size();
    }
    return total;
}

    /**
     * Crée un bouton d'icône avec un effet de survol pour l'en-tête
     * 
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
     * 
     * @return Le panel de barre latérale configuré
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(EMSI_DARK_GREEN); // Utilisation du vert foncé pour la barre latérale
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10)); // Marges internes

        // Ajout du bouton de retour en haut à gauche
        JButton backButton = new JButton("←") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(EMSI_LIGHT_GRAY); // Couleur au clic
                } else if (getModel().isRollover()) {
                    g2d.setColor(EMSI_LIGHT_GREEN); // Couleur au survol
                } else {
                    g2d.setColor(Color.WHITE); // Couleur par défaut (blanc)
                }

                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "←";
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
        };
        backButton.setPreferredSize(new Dimension(40, 40));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose(); // Fermer le tableau de bord actuel
            SwingUtilities.invokeLater(() -> new EnseignantLogin().setVisible(true)); // Ouvrir l'écran de connexion
                                                                                      // enseignant
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backButton);

        sidebarPanel.add(topPanel);
        sidebarPanel.add(Box.createVerticalStrut(20)); // Espace après le bouton de retour

        // Logo ou titre de l'application
        JLabel logoLabel = new JLabel("EMSI");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(logoLabel);
        sidebarPanel.add(Box.createVerticalStrut(30));

        // --- Ajout : Informations personnelles enseignant (similaire à l'étudiant) ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Espace pour la photo (statique pour l'instant)
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(90, 90));
        photoLabel.setMaximumSize(new Dimension(90, 90));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Image par défaut (icône enseignant)
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/teacher_icon.jpg"));
        Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(img));
        infoPanel.add(photoLabel);
        infoPanel.add(Box.createVerticalStrut(8));

        JLabel nomLabel = new JLabel(teacher.getFullName());
        nomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nomLabel.setForeground(Color.WHITE);
        nomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(nomLabel);

        JLabel emailLabel = new JLabel(teacher.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        emailLabel.setForeground(new Color(220, 255, 220));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(emailLabel);

        JLabel depLabel = new JLabel("Département : " + teacher.getDepartement());
        depLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        depLabel.setForeground(Color.WHITE);
        depLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(depLabel);

        JLabel specLabel = new JLabel("Spécialité : " + teacher.getSpecialite());
        specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        specLabel.setForeground(Color.WHITE);
        specLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(specLabel);

        JLabel telLabel = new JLabel("Tél : " + teacher.getTelephone());
        telLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        telLabel.setForeground(Color.WHITE);
        telLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(telLabel);

        sidebarPanel.add(infoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        // --- Fin ajout infos personnelles ---

        // Menu items avec icônes Unicode
        String[][] menuItems = {
                { "\uD83C\uDFE0", "Tableau de bord" },
                { "\uD83D\uDCDA", "Mes classes" },
                { "\uD83D\uDCCB", "Gestion des notes" },
                { "\uD83D\uDCC5", "Absences" },
                { "\uD83D\uDCC5", "Planning" },
                { "\uD83D\uDCE8", "Messages" },
                { "\uD83D\uDC64", "Profil" }
        };

        for (String[] item : menuItems) {
            JPanel menuItem = createMenuItem(item[0], item[1]);
            if (item[1].equals("Messages")) {
                messagesMenuItem = menuItem;
            }
            sidebarPanel.add(menuItem);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }

        sidebarPanel.add(Box.createVerticalGlue());

        // Bouton de déconnexion
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(MENU_FONT);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(EMSI_GREEN);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> {
            dispose();
            new EnseignantLogin().setVisible(true);
        });

        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));

        return sidebarPanel;
    }


    private JPanel createMenuItem(String icon, String text) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(EMSI_DARK_GREEN);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setMaximumSize(new Dimension(250, 50));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(MENU_FONT);
        textLabel.setForeground(Color.WHITE);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(EMSI_GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(EMSI_DARK_GREEN);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuAction(text);
            }
        });

        return panel;
    }

    /**
     * Gère les actions du menu de navigation
     * 
     * @param option Texte de l'option de menu sélectionnée
     */
    private void handleMenuAction(String option) {
        switch (option) {
            case "Tableau de bord":
                showDashboardOverviewPanel();
                break;
            case "Mes classes":
                showClassesPanel();
                break;
            case "Gestion des notes":
                new SaisieNotesDialog(this, currentTeacherId).setVisible(true);
                break;
            case "Absences":
                showAbsencesPanel();
                break;
            case "Planning":
                showSchedulePanel();
                break;
            case "Messages":
                showMessagesPanel();
                break;
            case "Profil":
                showProfilePanel();
                break;
            case "Déconnexion":
                dispose();
                new EnseignantLogin().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Fonctionnalité '" + option + "' en cours de développement.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showDashboardOverviewPanel() {
        contentPanel.removeAll();
        contentPanel.add(createContentPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGradesPanel() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClassesPanel() {
        contentPanel.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Mes classes");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel to hold the list of class items
        JPanel classesListPanel = new JPanel();
        classesListPanel.setLayout(new BoxLayout(classesListPanel, BoxLayout.Y_AXIS));
        classesListPanel.setOpaque(false);
        classesListPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Add some top padding

        // Fetch classes
        TeacherService teacherService = new TeacherService();
        List<Classe> classes = teacherService.getClassesByTeacherId(currentTeacherId);

        // Create and add a panel for each class
        if (classes.isEmpty()) {
            JLabel noClassesLabel = new JLabel("Aucune classe trouvée pour cet enseignant.");
            noClassesLabel.setFont(SUBTITLE_FONT);
            noClassesLabel.setForeground(EMSI_GRAY);
            noClassesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            classesListPanel.add(noClassesLabel);
        } else {
            for (Classe classe : classes) {
                JPanel classItem = createClassItemPanel(classe);
                classesListPanel.add(classItem);
                classesListPanel.add(Box.createVerticalStrut(10)); // Spacing between items
            }
        }

        JScrollPane scrollPane = new JScrollPane(classesListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates a visually aesthetic panel for a single class item.
     * 
     * @param classe The Classe object to display.
     * @return A JPanel representing the class item.
     */
    private JPanel createClassItemPanel(Classe classe) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 1), // Green border
                BorderFactory.createEmptyBorder(15, 20, 15, 20) // Internal padding
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Fixed height, flexible width
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        JLabel classNameLabel = new JLabel(classe.getNom());
        classNameLabel.setFont(HEADING_FONT);
        classNameLabel.setForeground(EMSI_DARK_GREEN);

        panel.add(classNameLabel, BorderLayout.WEST);

        // Add a subtle indicator or arrow on the right
        JLabel arrowLabel = new JLabel(" > "); // Or use an icon if available
        arrowLabel.setFont(HEADING_FONT);
        arrowLabel.setForeground(EMSI_GRAY);
        panel.add(arrowLabel, BorderLayout.EAST);

        // Add click listener
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showStudentsPanel(classe.getNom()); // Call the next method with class name
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(EMSI_LIGHT_GRAY); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE); // Reset on exit
            }
        });

        return panel;
    }

    private void showStudentsPanel(String groupName) {
        contentPanel.removeAll();
        // Call the method to create and return the students panel
        JPanel studentsPanel = createStudentsPanel(groupName);
        contentPanel.add(studentsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStudentsPanel(String groupName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Étudiants de la classe : " + groupName);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table to display student data
        String[] columns = { "Photo", "Matricule", "Nom", "Prénom", "Email", "Filière", "Promotion" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) { // Photo column
                    return ImageIcon.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 3 && col <= 7; // Seules les colonnes de notes sont éditables
            }
        };

        JTable studentsTable = new JTable(model);
        studentsTable.setRowHeight(80); // Adjust row height to accommodate photos
        studentsTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        // Set custom renderer for the Photo column
        studentsTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        // Set custom renderer for text columns with padding, alignment, and alternating
        // colors
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // Apply border for padding
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                // Align text to the left
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                // Set font
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Consistent font

                // Alternating row colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BACKGROUND_WHITE : EMSI_LIGHT_GRAY); // Alternate white and light
                                                                                        // gray
                    c.setForeground(Color.BLACK); // Default text color
                } else {
                    // Selection colors
                    c.setBackground(EMSI_GREEN); // Use EMSI green for selected row background
                    c.setForeground(Color.WHITE); // Use white text on green background
                }

                return c;
            }
        };

        for (int i = 1; i < studentsTable.getColumnCount(); i++) {
            studentsTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }

        // Customize selection background and foreground colors explicitly on the table
        studentsTable.setSelectionBackground(EMSI_GREEN); // Ensure selection background is EMSI green
        studentsTable.setSelectionForeground(Color.WHITE); // Ensure selected text is white

        // Improve grid lines visibility and color
        studentsTable.setGridColor(new Color(220, 220, 220)); // Lighter grid lines
        studentsTable.setShowVerticalLines(true);
        studentsTable.setShowHorizontalLines(true);

        // Remove default intercell spacing to rely on renderer's border for spacing
        studentsTable.setIntercellSpacing(new Dimension(0, 0));

        // Apply custom header renderer to all columns
        TableHeaderRenderer headerRenderer = new TableHeaderRenderer();
        studentsTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // Improve scroll pane appearance
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.getViewport().setBackground(BACKGROUND_WHITE); // Match background
        scrollPane.setColumnHeaderView(studentsTable.getTableHeader());

        panel.add(scrollPane, BorderLayout.CENTER);

        // Fetch student data
        EtudiantService etudiantService = new EtudiantService();
        List<Student> students = etudiantService.getStudentsByGroupName(groupName);

        // Populate the table model
        for (Student student : students) {
            // Load student photo - assuming photo path is relative to resources/images/
            ImageIcon studentPhoto = loadImage(student.getPhoto());
            if (studentPhoto != null) {
                // Scale the image to fit the row height if necessary
                Image img = studentPhoto.getImage();
                // Use a slightly smaller size for the image within the cell
                Image scaledImg = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                studentPhoto = new ImageIcon(scaledImg);
            }

            Object[] row = {
                    studentPhoto, // Photo
                    student.getMatricule(),
                    student.getNom(),
                    student.getPrenom(),
                    student.getEmail(),
                    student.getFiliere(),
                    student.getPromotion()
            };
            model.addRow(row);
        }

        return panel;
    }

    // Custom cell renderer for displaying images in a JTable
    private class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            lbl.setText(null);
            lbl.setIcon((ImageIcon) value);
            // Center the image within the cell
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            // Add padding
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return lbl;
        }
    }

    // Custom cell renderer for table header
    private class TableHeaderRenderer extends DefaultTableCellRenderer {
        public TableHeaderRenderer() { // Constructeur sans argument
            setHorizontalAlignment(SwingConstants.CENTER); // Center header text
            setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold font, matching table styling
            setBackground(EMSI_DARK_GREEN); // EMSI dark green background
            setForeground(Color.WHITE); // White text
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, Color.WHITE), // White bottom and right border for
                                                                              // separation
                    BorderFactory.createEmptyBorder(8, 10, 8, 10) // Padding
            ));
            setOpaque(true); // Important pour que le fond coloré s'affiche
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            // Utiliser le rendu par défaut de la superclasse pour obtenir le composant
            // et afficher le texte de l'en-tête (value)
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Appliquer le style personnalisé au composant retourné
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                // Explicitly set the text to the column name
                label.setText(value != null ? value.toString() : "");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setBackground(EMSI_DARK_GREEN);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, Color.WHITE), // White bottom and right border for
                                                                                  // separation
                        BorderFactory.createEmptyBorder(8, 10, 8, 10) // Padding
                ));
                label.setOpaque(true); // S'assurer qu'il est opaque

                // Force validation and repaint to ensure text is drawn
                label.validate();
                label.repaint();

                return label;
            } else {
                // Fallback pour les composants qui ne sont pas des JLabel
                ((JComponent) c).setBorder(getBorder());
                c.setBackground(getBackground());
                c.setForeground(getForeground());
                c.setFont(getFont());
                ((JComponent) c).setOpaque(true); // S'assurer qu'il est opaque
                return c;
            }
        }
    }

    private void showSchedulePanel() {
        contentPanel.removeAll();
        contentPanel.add(createPlanningTab() );
        contentPanel.revalidate();
        contentPanel.repaint();
    }

private JPanel createPlanningTab() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(new javax.swing.border.EmptyBorder(40, 80, 40, 80));

    // Section Emploi du temps
    JLabel titreEmploi = new JLabel("Mon emploi du temps");
    titreEmploi.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titreEmploi.setForeground(new Color(0, 148, 68));
    titreEmploi.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titreEmploi);
    panel.add(Box.createVerticalStrut(24));

    int enseignantId = currentTeacher.getId();
    java.util.List<com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf> emplois =
        new com.emsi.gestionuniv.service.EmploiDuTempsPdfService().getEmploisEnseignantById(enseignantId);

    if (emplois.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucun emploi du temps PDF disponible pour vous.");
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(new Color(88, 88, 90));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyLabel);
    } else {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf emploi : emplois) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 240, 230)),
                new javax.swing.border.EmptyBorder(8, 12, 8, 12)
            ));

            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setForeground(new Color(0, 148, 68));
            icon.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 12));
            row.add(icon, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel titreLbl = new JLabel(emploi.getTitre());
            titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreLbl.setForeground(new Color(0, 104, 56));
            JLabel cibleLbl = new JLabel("ID Enseignant : " + emploi.getCible());
            cibleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cibleLbl.setForeground(new Color(88, 88, 90));
            infoPanel.add(titreLbl);
            infoPanel.add(cibleLbl);
            row.add(infoPanel, BorderLayout.CENTER);

            JButton openBtn = new JButton("Télécharger / Ouvrir");
            openBtn.setBackground(new Color(0, 148, 68));
            openBtn.setForeground(Color.WHITE);
            openBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            openBtn.addActionListener(ev -> {
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(emploi.getCheminPdf()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Impossible d'ouvrir le PDF.");
                }
            });

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnPanel.setOpaque(false);
            btnPanel.add(openBtn);
            row.add(btnPanel, BorderLayout.EAST);

            listPanel.add(row);
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll);
    }

    // Séparateur
    panel.add(Box.createVerticalStrut(40));
    JSeparator separator = new JSeparator();
    separator.setForeground(new Color(230, 240, 230));
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
    panel.add(separator);
    panel.add(Box.createVerticalStrut(40));

    // Section Planning d'examens
    JLabel titrePlanning = new JLabel("Planning des examens");
    titrePlanning.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titrePlanning.setForeground(new Color(0, 148, 68));
    titrePlanning.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titrePlanning);
    panel.add(Box.createVerticalStrut(24));

    java.util.List<com.emsi.gestionuniv.model.academic.PlanningExamen> plannings =
        new com.emsi.gestionuniv.service.PlanningExamenService().getPlanningsByEnseignant(enseignantId);

    if (plannings.isEmpty()) {
        JLabel emptyPlanningLabel = new JLabel("Aucun planning d'examen disponible pour vous.");
        emptyPlanningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyPlanningLabel.setForeground(new Color(88, 88, 90));
        emptyPlanningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyPlanningLabel);
    } else {
        JPanel planningListPanel = new JPanel();
        planningListPanel.setLayout(new BoxLayout(planningListPanel, BoxLayout.Y_AXIS));
        planningListPanel.setOpaque(false);

        for (com.emsi.gestionuniv.model.academic.PlanningExamen planning : plannings) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 240, 230)),
                new javax.swing.border.EmptyBorder(8, 12, 8, 12)
            ));

            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setForeground(new Color(0, 148, 68));
            icon.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 12));
            row.add(icon, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel titreLbl = new JLabel(planning.getTitre());
            titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreLbl.setForeground(new Color(0, 104, 56));
            JLabel classeLbl = new JLabel("Classe : " + planning.getClasse());
            classeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            classeLbl.setForeground(new Color(88, 88, 90));
            infoPanel.add(titreLbl);
            infoPanel.add(classeLbl);
            row.add(infoPanel, BorderLayout.CENTER);

            JButton openBtn = new JButton("Télécharger / Ouvrir");
            openBtn.setBackground(new Color(0, 148, 68));
            openBtn.setForeground(Color.WHITE);
            openBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            openBtn.addActionListener(ev -> {
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(planning.getCheminPdf()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Impossible d'ouvrir le PDF.");
                }
            });

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnPanel.setOpaque(false);
            btnPanel.add(openBtn);
            row.add(btnPanel, BorderLayout.EAST);

            planningListPanel.add(row);
        }
        JScrollPane planningScroll = new JScrollPane(planningListPanel);
        planningScroll.setBorder(BorderFactory.createEmptyBorder());
        planningScroll.setBackground(Color.WHITE);
        planningScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(planningScroll);
    }

    panel.add(Box.createVerticalGlue());
    return panel;
}

    private static JPanel getJPanel(Emploi_de_temps courseEntry) {
        System.out.println("DEBUG: Inside getJPanel for entry: " + courseEntry); // Added logging
        JPanel courseDetailsPanel = new JPanel();
        courseDetailsPanel.setLayout(new BoxLayout(courseDetailsPanel, BoxLayout.Y_AXIS));
        courseDetailsPanel.setOpaque(true); // Make sure it is opaque
        courseDetailsPanel.setBackground(Color.LIGHT_GRAY); // Set a background color
        courseDetailsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Increased padding

        System.out.println("DEBUG: Creating matiereLabel..."); // Added logging
        JLabel matiereLabel = new JLabel(courseEntry.getMatiere(), SwingConstants.CENTER);
        matiereLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Slightly larger font
        matiereLabel.setForeground(EMSI_DARK_GREEN); // Use dark green for course title
        matiereLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        System.out.println("DEBUG: Creating salleLabel..."); // Added logging
        JLabel salleLabel = new JLabel("Salle: " + courseEntry.getSalle(), SwingConstants.CENTER);
        salleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        salleLabel.setForeground(EMSI_GRAY); // Use gray for details
        salleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        System.out.println("DEBUG: Creating groupeLabel..."); // Added logging
        JLabel groupeLabel = new JLabel("Classe: " + courseEntry.getGroupe(), SwingConstants.CENTER);
        groupeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        groupeLabel.setForeground(EMSI_GRAY); // Use gray for details
        groupeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        System.out.println("DEBUG: Adding components to courseDetailsPanel..."); // Added logging
        courseDetailsPanel.add(matiereLabel);
        courseDetailsPanel.add(Box.createVerticalStrut(4)); // Add spacing
        courseDetailsPanel.add(salleLabel);
        courseDetailsPanel.add(groupeLabel);
        System.out.println("DEBUG: Finished adding components. Returning panel."); // Added logging
        return courseDetailsPanel;
    }

private void showMessagesPanel() {
    contentPanel.removeAll();
    contentPanel.add(createMessagesPanel());
    contentPanel.revalidate();
    contentPanel.repaint();
}

private JPanel createMessagesPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(242, 247, 242));

    EtudiantService etudiantService = new EtudiantService();
    List<Student> students = etudiantService.getStudentsByTeacherId(currentTeacherId);
    String[] noms = students.stream().map(s -> s.getPrenom() + " " + s.getNom()).toArray(String[]::new);
    int[] etudiantIds = students.stream().mapToInt(Student::getId).toArray();

    JComboBox<String> studentCombo = new JComboBox<>(noms);
    studentCombo.setFont(new Font("Segoe UI", Font.BOLD, 15));
    studentCombo.setBackground(Color.WHITE);
    studentCombo.setForeground(EMSI_GREEN);
    studentCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GREEN, 1, true),
            new EmptyBorder(10, 18, 10, 18)));

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    topPanel.setOpaque(false);
    JLabel avatarLabel = new JLabel("👨‍🎓");
    avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
    avatarLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
    JLabel nameLabel = new JLabel(noms.length > 0 ? noms[0] : "");
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    nameLabel.setForeground(EMSI_GREEN);
    topPanel.add(avatarLabel);
    topPanel.add(nameLabel);
    topPanel.add(Box.createHorizontalStrut(30));
    topPanel.add(studentCombo);
    topPanel.setBorder(new EmptyBorder(10, 60, 10, 60));
    panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

    JTextPane messagesArea = new JTextPane();
    messagesArea.setOpaque(false);
    messagesArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    messagesArea.setEditable(false);
    messagesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_LIGHT_GREEN, 2, true),
            new EmptyBorder(18, 18, 18, 18)));

    JScrollPane scrollPane = new JScrollPane(messagesArea);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
    scrollPane.setBackground(new Color(245, 255, 245));
    panel.add(scrollPane, BorderLayout.CENTER);

    MessageService messageService = new MessageService();
// ...dans createMessagesPanel(), dans le Runnable updateMessages...
Runnable updateMessages = () -> {
    messageService.reloadMessages();
    int idx = studentCombo.getSelectedIndex();
    if (idx < 0) return;
    avatarLabel.setText("👨‍🎓");
    nameLabel.setText(noms[idx]);
    int etudiantId = etudiantIds[idx];
    java.util.List<Message> conv = messageService.getConversation(
        currentTeacherId, "enseignant", etudiantId, "etudiant"
    );
    StringBuilder html = new StringBuilder(
        "<html><body style='font-family:Segoe UI,sans-serif;font-size:15px;background:transparent;'>"
    );
    for (Message m : conv) {
        boolean sentByMe = m.getSenderId() == currentTeacherId && m.getSenderType().equals("enseignant");
        if (sentByMe) {
            // Bulle à droite, radius arrondi à droite
            html.append(
                "<div style='text-align:right;margin-bottom:14px;'>" +
                "<span style='background:#009444;border:1.5px solid #009444;color:#fff;padding:10px 22px;" +
                "border-radius:45px;display:inline-block;max-width:60%;" +
                "box-shadow:0 2px 8px #b2f2bb33;font-family:Segoe UI,sans-serif;font-size:15px;'>" +
                m.getContent() + "</span></div>"
            );
        } else {
            // Bulle à gauche, radius arrondi à gauche
            html.append(
                "<div style='text-align:left;margin-bottom:14px;'>" +
                "<span style='background:#f5f5f5;border:1.5px solid #e0e0e0;color:#222;padding:10px 22px;" +
                "border-radius:45px;display:inline-block;max-width:60%;" +
                "box-shadow:0 2px 8px #bbb2;font-family:Segoe UI,sans-serif;font-size:15px;'>" +
                "<b>" + nameLabel.getText() + " :</b> " + m.getContent() + "</span></div>"
            );
        }
    }
    html.append("</body></html>");
    messagesArea.setContentType("text/html");
    messagesArea.setText(html.toString());
    messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
};
    studentCombo.addActionListener(e -> updateMessages.run());
    updateMessages.run();
    new javax.swing.Timer(2000, e -> updateMessages.run()).start();

    // Zone d'envoi stylée
    JPanel sendPanel = new JPanel(new BorderLayout(8, 0)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(225, 245, 225, 120));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2d.dispose();
        }
    };
    sendPanel.setOpaque(false);
    sendPanel.setBorder(new EmptyBorder(18, 60, 18, 60));
    JTextField inputField = new JTextField();
    inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GREEN, 1, true),
            new EmptyBorder(12, 18, 12, 18)));

    JButton sendBtn = new JButton("Envoyer") {
        private boolean hovering = false;
        {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(120, 44));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, x, y);
            g2.dispose();
        }
    };

    sendBtn.addActionListener(e -> {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            int idx = studentCombo.getSelectedIndex();
            int etudiantId = etudiantIds[idx];
            Message msg = new Message();
            msg.setSenderId(currentTeacherId);
            msg.setSenderType("enseignant");
            msg.setReceiverId(etudiantId);
            msg.setReceiverType("etudiant");
            msg.setContent(text);
            messageService.sendMessage(msg);
            inputField.setText("");
            updateMessages.run();
        }
    });
    inputField.addActionListener(e -> sendBtn.doClick());

    sendPanel.add(inputField, BorderLayout.CENTER);
    sendPanel.add(sendBtn, BorderLayout.EAST);

    panel.add(sendPanel, BorderLayout.SOUTH);

    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(220, 235, 220), 2, true)));
    
    panel.setName("messagesPanel");

    return panel;
}

    private void addMessage(JPanel panel, String text, boolean isTeacher) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel bubblePanel = new JPanel(new BorderLayout());
        bubblePanel.setBackground(isTeacher ? EMSI_GREEN : EMSI_LIGHT_GRAY);
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel messageLabel = new JLabel("<html><body style='width: 200px'>" + text + "</body></html>");
        messageLabel.setFont(SUBTITLE_FONT);
        messageLabel.setForeground(isTeacher ? Color.WHITE : EMSI_GRAY);

        bubblePanel.add(messageLabel, BorderLayout.CENTER);

        if (isTeacher) {
            messagePanel.add(bubblePanel, BorderLayout.EAST);
        } else {
            messagePanel.add(bubblePanel, BorderLayout.WEST);
        }

        panel.add(messagePanel);
        panel.add(Box.createVerticalStrut(10));
    }

    private void showAbsencesPanel() {
        contentPanel.removeAll();
        contentPanel.add(createAbsencesPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createAbsencesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252)); // Gris très clair moderne

        // Header avec gradient et design moderne
        JPanel header = createModernHeader();
        header.setLayout(new BorderLayout());
        JLabel dateLabel = new JLabel("Date : " + java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(dateLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 250, 252));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Course selector avec design moderne
        JPanel courseSection = createModernCourseSelector();
        mainContent.add(courseSection, BorderLayout.NORTH);

        // Table avec design moderne
        JPanel tableSection = createModernTableSection();
        mainContent.add(tableSection, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }
    public void ajouterAbsence(Abscence absence) {
    try (Connection conn = DBConnect.getConnection()) {
        String sql = "INSERT INTO Abscence (etudiant_id, cours_id, date, justifiee, justification) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, absence.getEtudiantId());
        ps.setInt(2, absence.getCoursId());
        ps.setDate(3, absence.getDate());
        ps.setBoolean(4, absence.isJustifiee());
        ps.setBytes(5, absence.getJustification());
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient EMSI moderne
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(34, 139, 34),  // Vert EMSI principal
                        getWidth(), 0, new Color(46, 164, 79)  // Vert plus clair
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Logo et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        // Icône moderne (simulée avec caractère Unicode)
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JLabel title = new JLabel("Gestion des Absences");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Interface Enseignant - EMSI");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 255, 220));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(subtitle, BorderLayout.SOUTH);

        titlePanel.add(iconLabel);
        titlePanel.add(textPanel);
        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createModernCourseSelector() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Coins arrondis
        section.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel selectorLabel = new JLabel("Sélectionner un cours");
        selectorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectorLabel.setForeground(new Color(51, 51, 51));
        selectorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // ComboBox moderne
        JComboBox<String> courseSelector = new JComboBox<>();
        courseSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseSelector.setPreferredSize(new Dimension(400, 40));
        courseSelector.setBackground(Color.WHITE);
        courseSelector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Remplir le sélecteur de cours
        System.out.println("DEBUG: currentTeacherId = " + currentTeacherId);
        CoursService coursService = new CoursService();
        List<com.emsi.gestionuniv.model.academic.cours> coursList = coursService.getCoursByEnseignant(currentTeacherId);
        for (cours c : coursList) {
            courseSelector.addItem((c.getCode() != null ? c.getCode() : "") + " - " +
                    (c.getIntitule() != null && !c.getIntitule().isEmpty() ? c.getIntitule() : c.getTitre()));
        }

        // Sélectionner le cours courant si défini
        if (currentCoursId > 0) {
            for (int i = 0; i < coursList.size(); i++) {
                if (coursList.get(i).getId() == currentCoursId) {
                    courseSelector.setSelectedIndex(i);
                    break;
                }
            }
        } else if (!coursList.isEmpty()) {
            // Si aucun cours sélectionné, prendre le premier par défaut
            currentCoursId = coursList.get(0).getId();
            courseSelector.setSelectedIndex(0);
        }

        // Listener pour mettre à jour currentCoursId et rafraîchir le panneau
        courseSelector.addActionListener(e -> {
            int selectedIndex = courseSelector.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < coursList.size()) {
                currentCoursId = coursList.get(selectedIndex).getId();
                System.out.println("DEBUG: currentCoursId sélectionné = " + currentCoursId);
                // Rafraîchir le panneau principal (Absences)
                contentPanel.removeAll();
                contentPanel.add(createAbsencesPanel());
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        JPanel selectorPanel = new JPanel(new BorderLayout());
        selectorPanel.setBackground(Color.WHITE);
        selectorPanel.add(selectorLabel, BorderLayout.NORTH);
        selectorPanel.add(courseSelector, BorderLayout.CENTER);

        section.add(selectorPanel, BorderLayout.WEST);

        // Statistiques rapides dans le header
        JPanel quickStats = createQuickStatsPanel();
        section.add(quickStats, BorderLayout.EAST);

        return section;
    }

    private JPanel createQuickStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 1, 15, 0));
        statsPanel.setBackground(Color.WHITE);

        AbscenceService absenceService = new AbscenceService();
        int[] stats = absenceService.getAbsenceStats(currentCoursId);

        // Only show the total card
        JPanel totalCard = createStatCard("Total", String.valueOf(stats[0]), new Color(52, 152, 219), "");
        statsPanel.add(totalCard);

        return statsPanel;
    }

    private JPanel createStatCard(String label, String value, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, accentColor),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setPreferredSize(new Dimension(120, 70));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(new Color(102, 102, 102));
        labelText.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        topPanel.add(iconLabel);
        topPanel.add(labelText);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

private JPanel createModernTableSection() {
    JPanel section = new JPanel(new BorderLayout());
    section.setBackground(new Color(245, 250, 245));
    section.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    // Carte blanche avec ombre et coins arrondis
    JPanel card = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Ombre douce
            g2d.setColor(new Color(0, 0, 0, 18));
            g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 22, 22);
            // Fond blanc arrondi
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2d.dispose();
        }
    };
    card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    card.setOpaque(false);

    // Titre moderne
    JLabel tableTitle = new JLabel("Liste des Absences");
    tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    tableTitle.setForeground(new Color(0, 148, 68));
    tableTitle.setBorder(BorderFactory.createEmptyBorder(24, 32, 12, 32));

    // Table moderne
    String[] columns = {"Matricule", "Nom", "Prénom", "Absent ?"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) return Boolean.class;
            return String.class;
        }
    };

    // Remplir le tableau
    AbscenceService absenceService = new AbscenceService();
    List<Abscence> absences = absenceService.getAbsencesByCours(currentCoursId);
    for (Abscence absence : absences) {
        model.addRow(new Object[]{
            absence.getEtudiant().getMatricule(),
            absence.getEtudiant().getNom(),
            absence.getEtudiant().getPrenom(),
            true
        });
    }

    JTable table = new JTable(model);
    table.setRowHeight(44);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 1));
    table.setBackground(Color.WHITE);
    table.setSelectionBackground(new Color(0, 148, 68, 30));
    table.setSelectionForeground(Color.BLACK);

    // Header moderne vert
    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 16));
    header.setBackground(new Color(0, 148, 68));
    header.setForeground(Color.WHITE);
    header.setBorder(BorderFactory.createEmptyBorder());
    header.setPreferredSize(new Dimension(0, 48));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBackground(new Color(0, 148, 68)); // Vert EMSI
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        return label;
    }
});

    // Style des cellules
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 247, 242));
            }
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            return c;
        }
    });

    // MouseListener pour "Voir"
    table.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 5 && "Voir".equals(table.getValueAt(row, col))) {
                Abscence absence = absences.get(row);
                byte[] imageBytes = absence.getJustification();
                if (imageBytes != null && imageBytes.length > 0) {
                    ImageIcon icon = new ImageIcon(imageBytes);
                    JLabel label = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH)));
                    JOptionPane.showMessageDialog(table, label, "Justificatif", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(table, "Aucun justificatif disponible.");
                }
            }
        }
    });

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
    scrollPane.getViewport().setBackground(Color.WHITE);

    // Panel des boutons d'action
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
    actionPanel.setBackground(Color.WHITE);

JButton exportButton = createModernButton("Exporter le rapport", new Color(220, 53, 69), false);
exportButton.setBackground(new Color(220, 53, 69));
exportButton.setForeground(Color.WHITE);
exportButton.setOpaque(true);
exportButton.setContentAreaFilled(true);
exportButton.setBorderPainted(false);

JButton addButton = createModernButton("Ajouter une absence", new Color(0, 148, 68), true);
addButton.setBackground(new Color(0, 148, 68));
addButton.setForeground(Color.WHITE);
addButton.setOpaque(true);
addButton.setContentAreaFilled(true);
addButton.setBorderPainted(false);
addButton.addActionListener(e -> {
    // Récupère la liste des étudiants du cours
    EtudiantService etudiantService = new EtudiantService();
    java.util.List<Student> students = etudiantService.getStudentsByCoursId(currentCoursId);
    if (students.isEmpty()) {
        JOptionPane.showMessageDialog(section, "Aucun étudiant trouvé pour ce cours.");
        return;
    }
    String[] noms = students.stream()
        .map(s -> s.getPrenom() + " " + s.getNom() + " (" + s.getMatricule() + ")")
        .toArray(String[]::new);

    String selected = (String) JOptionPane.showInputDialog(
        section,
        "Sélectionnez l'étudiant à marquer absent :",
        "Ajouter une absence",
        JOptionPane.PLAIN_MESSAGE,
        null,
        noms,
        noms[0]
    );

    if (selected != null) {
        int idx = java.util.Arrays.asList(noms).indexOf(selected);
        Student student = students.get(idx);

        // Crée et enregistre l'absence du jour
        Abscence absence = new Abscence();
        absence.setEtudiantId(student.getId());
        absence.setCoursId(currentCoursId);
        absence.setDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
        absence.setJustifiee(false);
        absence.setJustification(null);
        new AbscenceService().ajouterAbsence(absence);

        JOptionPane.showMessageDialog(section, "Absence ajoutée pour " + selected);
        // Rafraîchir le panneau des absences
        contentPanel.removeAll();
        contentPanel.add(createAbsencesPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }
});


    actionPanel.add(exportButton);
    actionPanel.add(addButton);
    actionPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

    card.add(tableTitle, BorderLayout.NORTH);
    card.add(scrollPane, BorderLayout.CENTER);
    card.add(actionPanel, BorderLayout.SOUTH);

    section.add(card, BorderLayout.CENTER);
    return section;
}

    private void customizeModernTable(JTable table) {
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(46, 164, 79, 30));
        table.setSelectionForeground(new Color(51, 51, 51));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Header moderne
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(34, 139, 34));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer pour les cellules
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);

                return c;
            }
        });
    }

    private JButton createModernButton(String text, Color backgroundColor, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(isPrimary ? Color.WHITE : backgroundColor);
        button.setBackground(isPrimary ? backgroundColor : Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(backgroundColor.darker());
                } else {
                    button.setBackground(new Color(backgroundColor.getRed(), backgroundColor.getGreen(),
                            backgroundColor.getBlue(), 20));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(isPrimary ? backgroundColor : Color.WHITE);
            }
        });

        return button;
    }

    // Classe pour les bordures arrondies
    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void showStatsPanel() {
        contentPanel.removeAll();
        contentPanel.add(createStatsPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Statistiques détaillées");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        // Sélecteur de période
        JComboBox<String> periodSelector = new JComboBox<>(new String[] {
                "Semestre 1",
                "Semestre 2",
                "Année complète"
        });
        periodSelector.setPreferredSize(new Dimension(200, 35));
        periodSelector.setFont(SUBTITLE_FONT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(periodSelector, BorderLayout.EAST);

        // Panel principal avec deux colonnes
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setOpaque(false);

        // Colonne de gauche : Statistiques générales
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);

        // Cartes de statistiques
        leftColumn.add(createStatCard("Nombre total d'étudiants", "187", EMSI_GREEN));
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(createStatCard("Moyenne générale", "14.5", new Color(0, 150, 136)));
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(createStatCard("Taux de réussite", "85%", new Color(255, 152, 0)));

        // Colonne de droite : Graphiques
        JPanel rightColumn = new JPanel(new GridLayout(2, 1, 0, 20));
        rightColumn.setOpaque(false);

        // Graphique de distribution des notes
        JPanel gradesChart = createChartPanel("Distribution des notes", new String[] {
                "0-5", "5-10", "10-15", "15-20"
        }, new int[] { 5, 15, 45, 35 });

        // Graphique de taux de présence
        JPanel attendanceChart = createChartPanel("Taux de présence", new String[] {
                "Présent", "Absent justifié", "Absent non justifié"
        }, new int[] { 85, 10, 5 });

        rightColumn.add(gradesChart);
        rightColumn.add(attendanceChart);

        mainContent.add(leftColumn);
        mainContent.add(rightColumn);

        // Panel des actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton exportButton = new JButton("Exporter les statistiques");
        JButton printButton = new JButton("Imprimer le rapport");

        exportButton.setBackground(EMSI_GREEN);
        printButton.setBackground(EMSI_GREEN);

        exportButton.setForeground(Color.WHITE);
        printButton.setForeground(Color.WHITE);

        actionPanel.add(exportButton);
        actionPanel.add(printButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private Component createStatCard(String label, String number, Color emsiGreen) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(emsiGreen, 2));
        card.setPreferredSize(new Dimension(150, 100));

        JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(emsiGreen);

        JLabel numberLabel = new JLabel(number, SwingConstants.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 24));
        numberLabel.setForeground(Color.DARK_GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(numberLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartPanel(String title, String[] labels, int[] values) {
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
                    g2d.drawRoundRect(i, i, getWidth() - 2 * i - 1, getHeight() - 2 * i - 1, 12, 12);
                }

                // Titre
                g2d.setFont(HEADING_FONT);
                g2d.setColor(EMSI_GRAY);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (getWidth() - titleWidth) / 2, 30);

                // Dessiner le graphique
                int barWidth = getWidth() / (labels.length * 2);
                int maxValue = 100;
                int startX = (getWidth() - (barWidth * labels.length)) / 2;
                int startY = getHeight() - 50;

                // Dessiner les barres
                for (int i = 0; i < labels.length; i++) {
                    int barHeight = (values[i] * (startY - 60)) / maxValue;
                    g2d.setColor(new Color(0, 148, 68, 200));
                    g2d.fillRoundRect(startX + (i * barWidth * 2), startY - barHeight, barWidth, barHeight, 5, 5);

                    // Dessiner les étiquettes
                    g2d.setFont(SUBTITLE_FONT);
                    g2d.setColor(EMSI_GRAY);
                    String label = labels[i] + " (" + values[i] + "%)";
                    int labelWidth = fm.stringWidth(label);
                    g2d.drawString(label, startX + (i * barWidth * 2) + (barWidth - labelWidth) / 2, startY + 20);
                }

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(0, 300));
        return panel;
    }

    /**
     * Crée le panel principal de contenu du tableau de bord
     * 
     * @return Le panel de contenu configuré
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Titre du contenu modernisé
        JLabel contentTitle = new JLabel("Tableau de bord");
        contentTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        contentTitle.setForeground(EMSI_GREEN);

        // Zone de recherche moderne
        final JTextField searchField = new JTextField(20) {
            {
                setOpaque(false);
                setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
        // SUPPRIME : titlePanel.add(searchField, BorderLayout.EAST);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // --- Compteurs dynamiques ---
        int nbCours = 0;
        int nbEtudiants = 0;
        int nbNotesEnAttente = 0;
        int nbMessages = 0;
        try {
            nbCours = coursService.getCoursByEnseignant(teacher.getId()).size();
            nbEtudiants = new com.emsi.gestionuniv.service.EtudiantService().getStudentsByTeacherId(teacher.getId()).size();
            java.util.List<com.emsi.gestionuniv.model.academic.cours> coursList = coursService.getCoursByEnseignant(teacher.getId());
            com.emsi.gestionuniv.service.NoteService noteService = new com.emsi.gestionuniv.service.NoteService();
            for (com.emsi.gestionuniv.model.academic.cours c : coursList) {
                List<Student> students = new com.emsi.gestionuniv.service.EtudiantService().getStudentsByCoursId(c.getId());
                List<com.emsi.gestionuniv.model.academic.Note> notes = noteService.getNotesByCoursId(c.getId());
                for (Student s : students) {
                    boolean hasValidNote = false;
                    for (com.emsi.gestionuniv.model.academic.Note n : notes) {
                        if (n.getEtudiantId() == s.getId() && n.getValidation() != null && !n.getValidation().trim().isEmpty() && !"En attente".equalsIgnoreCase(n.getValidation())) {
                            hasValidNote = true;
                            break;
                        }
                    }
                    if (!hasValidNote) nbNotesEnAttente++;
                }
            }
            nbMessages = new com.emsi.gestionuniv.service.MessageService().countMessagesForTeacher(teacher.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // --- Fin compteurs dynamiques ---

        // Panneaux de statistiques (cartes modernes)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        statsPanel.add(createModernStatPanel("Cours", String.valueOf(nbCours), "\uD83D\uDCDA")); // 📚
        statsPanel.add(Box.createHorizontalStrut(30));
        statsPanel.add(createModernStatPanel("Étudiants", String.valueOf(nbEtudiants), "\uD83D\uDC64")); // 👤
        statsPanel.add(Box.createHorizontalStrut(30));
        statsPanel.add(createModernStatPanel("Notes en attente", String.valueOf(nbNotesEnAttente), "\uD83D\uDCCB")); // 📋
        statsPanel.add(Box.createHorizontalStrut(30));
        statsPanel.add(createModernStatPanel("Messages", "", "\uD83D\uDCE8")); // 📨

        // SUPPRIME : Panneau des dernières activités et calendrier
        // JPanel activitiesPanel = createActivitiesPanel();
        // JPanel calendarPanel = createCalendarPanel();
        // JPanel dashboardContent = new JPanel(new GridLayout(1, 2, 30, 0));
        // dashboardContent.setOpaque(false);
        // dashboardContent.add(activitiesPanel);
        // dashboardContent.add(calendarPanel);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        // SUPPRIME : panel.add(dashboardContent, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createModernStatPanel(String title, String value, String icon) {
        final boolean[] hovering = { false };

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Ombre portée
                if (hovering[0]) {
                    g2d.setColor(new Color(0, 0, 0, 40));
                    g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 24, 24);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 18));
                    g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 24, 24);
                }

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(200, 160));
        panel.setMaximumSize(new Dimension(220, 180));
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                hovering[0] = true;
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(Cursor.getDefaultCursor());
                hovering[0] = false;
                panel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (title.equals("Cours")) {
                     showCoursPdfListPanel();
                } else if (title.equals("Étudiants")) {
                    showClassesPanel();
                } else if (title.equals("Notes en attente")) {
                    new SaisieNotesDialog(EnseignantDashboard.this, currentTeacherId).setVisible(true);
                } else if (title.equals("Messages")) {
                    showMessagesPanel();
                }
            }
        });

        // Icône
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Titre
        JLabel titre = new JLabel(title);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titre.setForeground(EMSI_GREEN); // assure-toi que EMSI_GREEN est défini
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Valeur
        JLabel valeur = new JLabel(value);
        valeur.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valeur.setForeground(new Color(44, 62, 80));
        valeur.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout à la carte
        panel.add(iconLabel);
        panel.add(titre);
        panel.add(Box.createVerticalStrut(10));
        panel.add(valeur);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Crée le panneau des dernières activités
     * 
     * @return Panel configuré
     */
    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Dernières activités");
        title.setFont(HEADING_FONT);
        title.setForeground(EMSI_GRAY);

        JTextArea activities = new JTextArea("Aucune activité récente.");
        activities.setEditable(false);
        activities.setBackground(Color.WHITE);
        activities.setFont(SUBTITLE_FONT);

        panel.add(title, BorderLayout.NORTH);
        panel.add(activities, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Charge une image depuis les ressources
     * 
     * @param path Chemin de l'image (relatif au dossier resources/images/)
     * @return L'image chargée ou null si non trouvée
     */
    private ImageIcon loadImage(String path) {
        try {
            // Le chemin de ressource complet sera /images/ suivi du chemin passé en
            // paramètre
            String fullPath = "/images/" + path;
            System.out.println("Attempting to load image from resource path: " + fullPath);

            URL imageUrl = getClass().getResource(fullPath);

            if (imageUrl != null) {
                System.out.println("Image found at URL: " + imageUrl);
                return new ImageIcon(imageUrl);
            } else {
                System.err.println("Image resource not found: " + fullPath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image from resource path: /images/" + path);
            e.printStackTrace();
            return null;
        }
    }

    // Ajoute ceci dans EnseignantDashboard
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Calendrier à venir..."));
        return panel;
    }

    // --- Ajout : Méthode de modification du profil enseignant ---
    private void showEditProfileDialog() {
        // Création du JDialog personnalisé
        JDialog dialog = new JDialog(this, "Modifier le profil", true);
        dialog.setUndecorated(true);
        dialog.setSize(370, 410);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(new Color(0, 0, 0, 0));

        // Main panel with EMSI green gradient, rounded corners, drop shadow
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                // Drop shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 32, 32);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel titre = new JLabel("Modifier mon profil");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titre);
        mainPanel.add(Box.createVerticalStrut(18));

        // Circular profile picture with border and shadow
        JLabel photoPreview = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(4, 4, 92, 92); // shadow
                g2.dispose();
            }
        };
        photoPreview.setPreferredSize(new Dimension(100, 100));
        photoPreview.setMaximumSize(new Dimension(100, 100));
        photoPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        String currentPhoto = teacher.getPhoto();
        if (currentPhoto != null && !currentPhoto.isEmpty()) {
            ImageIcon icon = new ImageIcon(currentPhoto);
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            photoPreview.setIcon(new ImageIcon(img));
        } else {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/teacher_icon.jpg"));
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            photoPreview.setIcon(new ImageIcon(img));
        }
        photoPreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 4, true),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        photoPreview.setOpaque(false);
        mainPanel.add(photoPreview);
        mainPanel.add(Box.createVerticalStrut(8));

        // Change photo button
        JButton choosePhotoBtn = new JButton("Changer la photo");
        choosePhotoBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        choosePhotoBtn.setBackground(EMSI_LIGHT_GREEN);
        choosePhotoBtn.setForeground(EMSI_DARK_GREEN);
        choosePhotoBtn.setFocusPainted(false);
        choosePhotoBtn.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 2, true));
        choosePhotoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        choosePhotoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        final String[] selectedPhoto = { currentPhoto };
        choosePhotoBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                choosePhotoBtn.setBackground(EMSI_GREEN);
                choosePhotoBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                choosePhotoBtn.setBackground(EMSI_LIGHT_GREEN);
                choosePhotoBtn.setForeground(EMSI_DARK_GREEN);
            }
        });
        choosePhotoBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                selectedPhoto[0] = path;
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                photoPreview.setIcon(new ImageIcon(img));
            }
        });
        mainPanel.add(choosePhotoBtn);
        mainPanel.add(Box.createVerticalStrut(18));

        // Modern phone field
        JLabel telLabel = new JLabel("Numéro de téléphone :");
        telLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        telLabel.setForeground(Color.WHITE);
        telLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(telLabel);
        mainPanel.add(Box.createVerticalStrut(4));
        JTextField telField = new JTextField(teacher.getTelephone());
        telField.setMaximumSize(new Dimension(220, 36));
        telField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        telField.setBackground(EMSI_LIGHT_GREEN);
        telField.setForeground(EMSI_DARK_GREEN);
        telField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
                new EmptyBorder(6, 12, 6, 12)));
        telField.setCaretColor(EMSI_GREEN);
        mainPanel.add(telField);
        mainPanel.add(Box.createVerticalStrut(22));

        // OK/Cancel buttons, modern style
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton okBtn = new JButton("Valider");
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okBtn.setBackground(EMSI_GREEN);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                okBtn.setBackground(EMSI_DARK_GREEN);
            }

            public void mouseExited(MouseEvent e) {
                okBtn.setBackground(EMSI_GREEN);
            }
        });
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(EMSI_LIGHT_GREEN);
        cancelBtn.setForeground(EMSI_DARK_GREEN);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 2, true));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(EMSI_GREEN);
                cancelBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(EMSI_LIGHT_GREEN);
                cancelBtn.setForeground(EMSI_DARK_GREEN);
            }
        });
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalStrut(18));
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel);

        // Fade-in animation
        mainPanel.setOpaque(false);
        dialog.setContentPane(mainPanel);
        dialog.setOpacity(0f);
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            float opacity = 0f;

            public void actionPerformed(ActionEvent e) {
                opacity += 0.08f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    timer.stop();
                }
                dialog.setOpacity(opacity);
            }
        });
        timer.start();

        // Button actions
        okBtn.addActionListener(e -> {
            teacher.setTelephone(telField.getText());
            teacher.setPhoto(selectedPhoto[0]);
            new TeacherService().updateTeacherProfile(teacher);
            dialog.dispose();
            dispose();
            new EnseignantDashboard(teacher).setVisible(true);
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Ajoute la méthode pour la modale de saisie de notes
    private void showNoteEntryDialog(Student student, int coursId, Note existingNote) {
        JDialog dialog = new JDialog(this, "Saisir les notes de " + student.getNom() + " " + student.getPrenom(), true);
        dialog.setUndecorated(true);
        dialog.setSize(370, 340);
        dialog.setLocationRelativeTo(this);

        // Panel principal avec fond blanc, coins arrondis, ombre
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 28, 28);
                // Fond blanc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel titre = new JLabel("Saisir les notes de " + student.getNom() + " " + student.getPrenom());
        titre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titre.setForeground(new Color(0, 148, 68)); // EMSI vert
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titre);
        mainPanel.add(Box.createVerticalStrut(18));

        // Champs de saisie modernes
        JTextField ccField = new JTextField(
                existingNote != null ? String.valueOf(existingNote.getControleContinu()) : "");
        JTextField examField = new JTextField(existingNote != null ? String.valueOf(existingNote.getExamen()) : "");
        JTextField tpField = new JTextField(existingNote != null ? String.valueOf(existingNote.getTp()) : "");

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);
        Color lightGreen = new Color(232, 250, 241);

        for (JTextField field : new JTextField[] { ccField, examField, tpField }) {
            field.setMaximumSize(new Dimension(220, 36));
            field.setFont(fieldFont);
            field.setBackground(lightGreen);
            field.setForeground(new Color(0, 104, 56));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 148, 68), 2, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            field.setCaretColor(new Color(0, 148, 68));
        }

        JLabel ccLabel = new JLabel("Contrôle Continu (obligatoire) :");
        ccLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ccLabel.setForeground(new Color(0, 148, 68));
        mainPanel.add(ccLabel);
        mainPanel.add(ccField);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel examLabel = new JLabel("Examen (obligatoire) :");
        examLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        examLabel.setForeground(new Color(0, 148, 68));
        mainPanel.add(examLabel);
        mainPanel.add(examField);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel tpLabel = new JLabel("TP/Devoir (facultatif) :");
        tpLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tpLabel.setForeground(new Color(0, 148, 68));
        mainPanel.add(tpLabel);
        mainPanel.add(tpField);
        mainPanel.add(Box.createVerticalStrut(22));

        // Boutons modernes
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton okBtn = new JButton("Valider");
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okBtn.setBackground(new Color(0, 148, 68));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                okBtn.setBackground(new Color(0, 104, 56));
            }

            public void mouseExited(MouseEvent e) {
                okBtn.setBackground(new Color(0, 148, 68));
            }
        });
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(lightGreen);
        cancelBtn.setForeground(new Color(0, 104, 56));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 148, 68), 2, true));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(0, 148, 68));
                cancelBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(lightGreen);
                cancelBtn.setForeground(new Color(0, 104, 56));
            }
        });
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalStrut(18));
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel);

        // Animation fade-in
        mainPanel.setOpaque(false);
        dialog.setContentPane(mainPanel);
        dialog.setOpacity(0f);
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            float opacity = 0f;

            public void actionPerformed(ActionEvent e) {
                opacity += 0.08f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    timer.stop();
                }
                dialog.setOpacity(opacity);
            }
        });
        timer.start();

        // Actions des boutons
        okBtn.addActionListener(e -> {
            try {
                double cc = Double.parseDouble(ccField.getText());
                double exam = Double.parseDouble(examField.getText());
                double tp = tpField.getText().isEmpty() ? 0.0 : Double.parseDouble(tpField.getText());

                if (cc < 0 || cc > 20 || exam < 0 || exam > 20 || tp < 0 || tp > 20) {
                    JOptionPane.showMessageDialog(dialog, "Les notes doivent être entre 0 et 20.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double noteFinale = tpField.getText().isEmpty() ? (cc + exam) / 2 : (cc + exam + tp) / 3;
                String validation = noteFinale >= 10 ? "Validée" : "Non validée";

                Note note = existingNote != null ? existingNote : new Note();
                note.setEtudiantId(student.getId());
                note.setCoursId(coursId);
                note.setControleContinu(cc);
                note.setExamen(exam);
                note.setTp(tp);
                note.setNoteFinale(noteFinale);
                note.setValidation(validation);

                NoteService noteService = new NoteService();
                noteService.saveOrUpdateNote(note);

                JOptionPane.showMessageDialog(dialog, "Note enregistrée avec succès.", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Veuillez saisir des valeurs numériques valides.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Modifie ou ajoute une méthode pour la saisie de notes par étudiant
    private void showGradesEntryPanel() {
        // Always clear the content panel first
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sélecteur de classe et matière
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel classLabel = new JLabel("Classe : ");
        classLabel.setFont(SUBTITLE_FONT);
        TeacherService teacherService = new TeacherService();
        List<TeacherService.Classe> classes = teacherService.getClassesByTeacherId(currentTeacherId);
        JComboBox<String> classCombo = new JComboBox<>();
        for (TeacherService.Classe classe : classes) {
            classCombo.addItem(classe.getNom());
        }
        topPanel.add(classLabel);
        topPanel.add(classCombo);

        // Sélecteur de matière
        JLabel courseLabel = new JLabel("Matière : ");
        courseLabel.setFont(SUBTITLE_FONT);
        CoursService coursService = new CoursService();
        JComboBox<String> courseCombo = new JComboBox<>();
        List<String> matieres = coursService.getMatieresByTeacherId(currentTeacherId);
        for (String matiere : matieres) {
            courseCombo.addItem(matiere);
        }
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(courseLabel);
        topPanel.add(courseCombo);

        panel.add(topPanel, BorderLayout.NORTH);

        // Tableau moderne des étudiants
        String[] columns = { "Matricule", "Nom", "Prénom" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        final int[] hoveredRow = { -1 };
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(232, 250, 241));
                } else {
                    c.setBackground(new Color(0, 148, 68));
                    c.setForeground(Color.WHITE);
                }
                if (row == hoveredRow[0] && !isRowSelected(row)) {
                    c.setBackground(new Color(200, 240, 210));
                }
                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                return c;
            }
        };
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(new Color(0, 148, 68));
        table.setSelectionForeground(Color.WHITE);
        // Header moderne
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(0, 148, 68));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        // Effet de survol
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredRow[0]) {
                    hoveredRow[0] = row;
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredRow[0] = -1;
                table.repaint();
            }
        });
        // Bordures arrondies et ombre sur le scrollpane
        JScrollPane scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 18));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 18, 18);
                g2d.dispose();
            }
        };
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les étudiants à la sélection de la classe
        classCombo.addActionListener(e -> {
            String selectedClass = (String) classCombo.getSelectedItem();
            if (selectedClass != null) {
                model.setRowCount(0);
                EtudiantService etudiantService = new EtudiantService();
                List<Student> students = etudiantService.getStudentsByGroupName(selectedClass);
                for (Student student : students) {
                    model.addRow(new Object[] { student.getMatricule(), student.getNom(), student.getPrenom() });
                }
            }
        });

        // --- Correction : charger les étudiants de la première classe par défaut ---
        if (classCombo.getItemCount() > 0) {
            classCombo.setSelectedIndex(0); // Déclenche l'ActionListener et remplit le tableau
        }

        // Listener pour ouvrir la modale de saisie de notes
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String selectedClass = (String) classCombo.getSelectedItem();
                    String selectedMatiere = (String) courseCombo.getSelectedItem();
                    if (selectedClass == null || selectedMatiere == null)
                        return;
                    EtudiantService etudiantService = new EtudiantService();
                    List<Student> students = etudiantService.getStudentsByGroupName(selectedClass);
                    Student student = students.get(row);
                    // Récupérer l'ID du cours à partir de l'intitulé
                    int coursId = coursService.getCoursIdByIntitule(selectedMatiere);
                    NoteService noteService = new NoteService();
                    Note note = noteService.getNoteByStudentAndCourse(student.getId(), coursId);
                    showNoteEntryDialog(student, coursId, note);
                }
            }
        });

        // Ajoute UNIQUEMENT ce panel au contentPanel
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

private void showCoursPdfListPanel() {
    contentPanel.removeAll();
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(245, 250, 245));
    panel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

    JLabel title = new JLabel("Mes cours PDF déposés");
    title.setFont(new Font("Segoe UI", Font.BOLD, 26));
    title.setForeground(EMSI_GREEN);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(title);
    panel.add(Box.createVerticalStrut(24));

    // Bouton flottant d'ajout (style Material)
    JButton addBtn = new JButton("+") {
        private boolean hovering = false;
        {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 32));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, x, y);
            g2.dispose();
        }
    };
    addBtn.setPreferredSize(new Dimension(54, 54));
    addBtn.setMaximumSize(new Dimension(54, 54));
    addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    addBtn.setToolTipText("Ajouter un cours PDF");
    addBtn.addActionListener(e -> showAddPdfPanel());

    JPanel addBtnPanel = new JPanel();
    addBtnPanel.setOpaque(false);
    addBtnPanel.setLayout(new BoxLayout(addBtnPanel, BoxLayout.X_AXIS));
    addBtnPanel.add(Box.createHorizontalGlue());
    addBtnPanel.add(addBtn);
    addBtnPanel.add(Box.createHorizontalGlue());
    panel.add(addBtnPanel);
    panel.add(Box.createVerticalStrut(18));

    // Liste des PDF
    List<com.emsi.gestionuniv.model.academic.CoursPdf> pdfs = new com.emsi.gestionuniv.service.CoursPdfService()
            .getCoursPdfByEnseignant(currentTeacherId);

    if (pdfs.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucun cours PDF déposé.");
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(EMSI_GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyLabel);
    } else {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (com.emsi.gestionuniv.model.academic.CoursPdf pdf : pdfs) {
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Ombre légère
                    g2.setColor(new Color(0, 0, 0, 18));
                    g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 24, 24);
                    // Fond blanc arrondi
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                    g2.dispose();
                }
            };
            card.setLayout(new BorderLayout(18, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            card.setPreferredSize(new Dimension(700, 70));
            card.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
            card.setOpaque(false);

            // Effet hover
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(240, 255, 240));
                    card.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                    card.repaint();
                }
            });

            // Icône PDF
            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            icon.setForeground(EMSI_GREEN);
            icon.setBorder(new EmptyBorder(0, 0, 0, 12));

            // Infos PDF
            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel titreLbl = new JLabel(pdf.getTitre());
            titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreLbl.setForeground(EMSI_DARK_GREEN);
            JLabel classeLbl = new JLabel("Classe : " + pdf.getClasse());
            classeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            classeLbl.setForeground(EMSI_GRAY);
            infoPanel.add(titreLbl);
            infoPanel.add(classeLbl);

            // Panel boutons
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));

            // Bouton vert arrondi
            JButton openBtn = new JButton("Ouvrir") {
                private boolean hovering = false;
                {
                    setFocusPainted(false);
                    setContentAreaFilled(false);
                    setBorderPainted(false);
                    setOpaque(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setForeground(Color.WHITE);
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
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                    g2.setColor(base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            openBtn.setPreferredSize(new Dimension(100, 36));
            openBtn.setMaximumSize(new Dimension(100, 36));
            openBtn.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(new File(pdf.getCheminPdf()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Impossible d'ouvrir le PDF.");
                }
            });

            // Bouton rouge arrondi pour supprimer
            JButton deleteBtn = new JButton("Supprimer") {
                private boolean hovering = false;
                {
                    setFocusPainted(false);
                    setContentAreaFilled(false);
                    setBorderPainted(false);
                    setOpaque(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setForeground(Color.WHITE);
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
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = hovering ? new Color(180, 0, 0) : new Color(220, 0, 0);
                    g2.setColor(base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            deleteBtn.setPreferredSize(new Dimension(110, 36));
            deleteBtn.setMaximumSize(new Dimension(110, 36));
            deleteBtn.addActionListener(ev -> {
                int confirm = JOptionPane.showConfirmDialog(panel, "Supprimer ce PDF ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new com.emsi.gestionuniv.service.CoursPdfService().deleteCoursPdf(pdf.getId());
                    showCoursPdfListPanel(); // Refresh list
                }
            });

            btnPanel.add(openBtn);
            btnPanel.add(deleteBtn);

            card.add(icon, BorderLayout.WEST);
            card.add(infoPanel, BorderLayout.CENTER);
            card.add(btnPanel, BorderLayout.EAST);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(14));
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(new Color(245, 250, 245));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll);
    }

    panel.add(Box.createVerticalGlue());

    contentPanel.removeAll();
    contentPanel.add(panel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}

    private void showAddPdfPanel() {
        contentPanel.removeAll();

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre douce
                g2d.setColor(new Color(0, 0, 0, 18));
                g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 32, 32);
                // Fond blanc arrondi
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2d.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(38, 48, 38, 48));
        card.setMaximumSize(new Dimension(480, 420));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setAlignmentY(Component.CENTER_ALIGNMENT);
        card.setOpaque(false);

        JLabel title = new JLabel("Ajouter un cours PDF");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(EMSI_GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(28));

        // Champ titre
        JLabel titreLbl = new JLabel("Titre du cours :");
        titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titreLbl.setForeground(EMSI_DARK_GREEN);
        titreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titreLbl);

        JTextField titreField = new JTextField();
        titreField.setMaximumSize(new Dimension(400, 36));
        titreField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titreField.setBackground(new Color(232, 250, 241));
        titreField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
                new EmptyBorder(8, 14, 8, 14)));
        titreField.setCaretColor(EMSI_GREEN);
        card.add(titreField);
        card.add(Box.createVerticalStrut(16));

        // Sélecteur de classe
        JLabel classeLbl = new JLabel("Classe :");
        classeLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        classeLbl.setForeground(EMSI_DARK_GREEN);
        classeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(classeLbl);

        TeacherService teacherService = new TeacherService();
        List<TeacherService.Classe> classes = teacherService.getClassesByTeacherId(currentTeacherId);
        JComboBox<String> classCombo = new JComboBox<>();
        for (TeacherService.Classe c : classes)
            classCombo.addItem(c.getNom());
        classCombo.setMaximumSize(new Dimension(400, 36));
        classCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        classCombo.setBackground(new Color(232, 250, 241));
        classCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
                new EmptyBorder(4, 10, 4, 10)));
        card.add(classCombo);
        card.add(Box.createVerticalStrut(16));

        // Sélecteur de fichier PDF
        JLabel fileLbl = new JLabel("Fichier PDF :");
        fileLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        fileLbl.setForeground(EMSI_DARK_GREEN);
        fileLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(fileLbl);

        JLabel fileLabel = new JLabel("Aucun fichier sélectionné");
        fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        fileLabel.setForeground(EMSI_GRAY);
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final File[] selectedFile = { null };
        JButton chooseBtn = new JButton("Choisir un fichier PDF") {
            private boolean hovering = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setFont(new Font("Segoe UI", Font.BOLD, 15));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        chooseBtn.setPreferredSize(new Dimension(220, 38));
        chooseBtn.setMaximumSize(new Dimension(220, 38));
        chooseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));
            if (fileChooser.showOpenDialog(card) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile[0].getName());
            }
        });
        card.add(chooseBtn);
        card.add(Box.createVerticalStrut(6));
        card.add(fileLabel);
        card.add(Box.createVerticalStrut(24));

        // Bouton d'ajout stylé
        JButton addBtn = new JButton("Ajouter le cours PDF") {
            private boolean hovering = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setFont(new Font("Segoe UI", Font.BOLD, 16));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        addBtn.setPreferredSize(new Dimension(220, 44));
        addBtn.setMaximumSize(new Dimension(220, 44));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        addBtn.addActionListener(e -> {
            String titre = titreField.getText().trim();
            String classe = (String) classCombo.getSelectedItem();
            if (titre.isEmpty() || selectedFile[0] == null || classe == null) {
                JOptionPane.showMessageDialog(card, "Veuillez remplir tous les champs et choisir un PDF.");
                return;
            }
            try {
                File destDir = new File("pdfs");
                if (!destDir.exists())
                    destDir.mkdir();
                File dest = new File(destDir, selectedFile[0].getName());
                java.nio.file.Files.copy(selectedFile[0].toPath(), dest.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                new com.emsi.gestionuniv.service.CoursPdfService().ajouterCoursPdf(titre, dest.getAbsolutePath(),
                        currentTeacherId, classe);
                JOptionPane.showMessageDialog(card, "Cours PDF ajouté !");
                showCoursPdfListPanel(); // Retour à la liste
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(card, "Erreur lors de la copie du fichier PDF.");
            }
        });
        card.add(addBtn);

        // Centrage vertical/horizontal
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        contentPanel.removeAll();
        contentPanel.add(wrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
}