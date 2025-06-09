package com.emsi.gestionuniv.view.enseignant;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.model.academic.Note;
import com.emsi.gestionuniv.service.AbscenceService;
import com.emsi.gestionuniv.service.NoteService;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.view.Login.EnseignantLogin;
import com.emsi.gestionuniv.service.TeacherService.Classe;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.model.user.Student;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import com.emsi.gestionuniv.model.planning.Emploi_de_temps;
import com.emsi.gestionuniv.service.EmploiDuTempsService;
import java.text.DecimalFormat; // Ajoute cet import en haut du fichier

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

        // Menu items avec icônes Unicode
        String[][] menuItems = {
                { "\uD83C\uDFE0", "Tableau de bord" },
                { "\uD83D\uDCDA", "Mes classes" },
                { "\uD83D\uDCCB", "Gestion des notes" },
                { "\uD83D\uDCC5", "Planning" },
                { "\uD83D\uDCE8", "Messages" },
                { "\uD83D\uDC64", "Profil" }
        };

        for (String[] item : menuItems) {
            JPanel menuItem = createMenuItem(item[0], item[1]);
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
                showGradesPanel();
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
        contentPanel.add(createGradesPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

private JPanel createGradesPanel() {
    System.out.println("DEBUG: createGradesPanel() appelé");
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(BACKGROUND_WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Ajoute des logs pour vérifier les étapes
    System.out.println("DEBUG: Initialisation des composants du panel de gestion des notes");

    // Sélecteur de classe
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.setOpaque(false);
    JLabel classLabel = new JLabel("Classe : ");
    classLabel.setFont(SUBTITLE_FONT);

TeacherService teacherService = new TeacherService();
List<TeacherService.Classe> classes = teacherService.getClassesByTeacherId(currentTeacherId);

JComboBox<String> classCombo = new JComboBox<>();
for (TeacherService.Classe classe : classes) {
    classCombo.addItem(classe.getNom()); // Assure-toi que `getNom()` retourne le nom de la classe
}

    topPanel.add(classLabel);
    topPanel.add(classCombo);

       panel.add(topPanel, BorderLayout.NORTH);

    // Ajoute des logs pour vérifier les combobox
    System.out.println("DEBUG: Combobox de classe initialisé avec " + classCombo.getItemCount() + " éléments");

    
    // Sélecteur de matière
    JLabel courseLabel = new JLabel("Matière : ");
    courseLabel.setFont(SUBTITLE_FONT);

        // Tableau des notes
    String[] columns = {"ID_etudiant","Nom", "Prénom", "Contrôle Continu", "Examen", "TP", "Note Finale", "Validation" };
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return col >= 3 && col <= 7; // Seules les notes sont éditables
        }
    };
    // Ajoute un écouteur pour recalculer la note finale et la mention
model.addTableModelListener(e -> {
    int row = e.getFirstRow(); // Ligne modifiée
    int column = e.getColumn(); // Colonne modifiée

    // Vérifie si la modification concerne les colonnes des notes
    if (column >= 3 && column <= 5) { // Colonnes Contrôle Continu, Examen, TP
        try {
            // Récupère les valeurs des colonnes
            double controleContinu = Double.parseDouble(model.getValueAt(row, 3).toString());
            double examen = Double.parseDouble(model.getValueAt(row, 4).toString());
            double tp = Double.parseDouble(model.getValueAt(row, 5).toString());

            // Calcule la note finale
            double noteFinale = (controleContinu + examen + tp) / 3;

            // Détermine la mention
            String validation = noteFinale >= 10 ? "Validée" : "Non validée";

            // Met à jour les colonnes Note Finale et Validation
            model.setValueAt(noteFinale, row, 6); // Colonne Note Finale
            model.setValueAt(validation, row, 7); // Colonne Validation
        } catch (NumberFormatException ex) {
            // Gestion des erreurs si les valeurs ne sont pas valides
            JOptionPane.showMessageDialog(panel, "Veuillez entrer des valeurs numériques valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
});

    JComboBox<String> courseCombo = new JComboBox<>();
    CoursService coursService = new CoursService();
    List<String> matieres = coursService.getMatieresByTeacherId(currentTeacherId);
    for (String matiere : matieres) {
        courseCombo.addItem(matiere);
    }
    

    // Ajoute un ActionListener pour mettre à jour currentCoursId
courseCombo.addActionListener(e -> {
    String selectedIntitule = (String) courseCombo.getSelectedItem();
    if (selectedIntitule != null) {
        currentCoursId = coursService.getCoursIdByIntitule(selectedIntitule); // Récupère l'ID du cours
        System.out.println("DEBUG: currentCoursId mis à jour : " + currentCoursId);

        // Charger les notes des étudiants
        String selectedClass = (String) classCombo.getSelectedItem();
        if (selectedClass != null) {
            NoteService noteService = new NoteService();
            List<Note> notes = noteService.getNotesByClassAndCourse(selectedClass, currentCoursId);

            model.setRowCount(0); // Réinitialise le tableau
            for (Note note : notes) {
                model.addRow(new Object[] {
                    note.getEtudiantId(),
                    note.getNomEtudiant(), // Remplace par le nom réel si nécessaire
                    note.getPrenomEtudiant(), // Remplace par le prénom réel si nécessaire
                    note.getControleContinu(),
                    note.getExamen(),
                    note.getTp(),
                    note.getNoteFinale(),
                    note.getValidation()
                });
            }
            System.out.println("DEBUG: Tableau des notes mis à jour avec " + model.getRowCount() + " lignes.");
        }
    }
});
topPanel.add(Box.createHorizontalStrut(20));
topPanel.add(courseLabel);
topPanel.add(courseCombo);

    panel.add(topPanel, BorderLayout.NORTH);

    // Ajoute des logs pour vérifier les combobox
    System.out.println("DEBUG: Combobox de matière initialisé avec " + courseCombo.getItemCount() + " éléments");


    JTable table = new JTable(model);
    table.setRowHeight(30);

    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Ajoute des logs pour vérifier le tableau
    System.out.println("DEBUG: Tableau des notes initialisé");

    // Bouton pour charger les élèves
String selectedClass = (String) classCombo.getSelectedItem();
String selectedCourse = (String) courseCombo.getSelectedItem();
if (selectedClass != null && selectedCourse != null) {
    NoteService noteService = new NoteService();
    List<Note> notes = noteService.getNotesByClassAndCourse(selectedClass, currentCoursId);

    model.setRowCount(0); // Réinitialise le tableau
    for (Note note : notes) {
        model.addRow(new Object[] {
            note.getEtudiantId(),
            "Nom Étudiant", // Remplace par le nom réel si nécessaire
            "Prénom Étudiant", // Remplace par le prénom réel si nécessaire
            note.getControleContinu(),
            note.getExamen(),
            note.getTp(),
            note.getNoteFinale(),
            note.getValidation()
        });
    }
    System.out.println("DEBUG: Tableau des notes mis à jour avec " + model.getRowCount() + " lignes.");
}
        // Bouton pour enregistrer les notes
    JButton saveButton = new JButton("Enregistrer les notes");
saveButton.addActionListener(e -> {
    List<Note> notes = new ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        Note note = new Note();
        note.setEtudiantId((Integer) model.getValueAt(i, 0)); // ID de l'étudiant
        note.setCoursId(currentCoursId); // ID du cours
        note.setControleContinu(Double.parseDouble(model.getValueAt(i, 3).toString()));
        note.setExamen(Double.parseDouble(model.getValueAt(i, 4).toString()));
        note.setTp(Double.parseDouble(model.getValueAt(i, 5).toString()));
        double noteFinale = (note.getControleContinu() + note.getExamen() + note.getTp()) / 3;
        note.setNoteFinale(noteFinale);
        note.setValidation(noteFinale >= 10 ? "Validée" : "Non validée");
        notes.add(note);
    }

    NoteService noteService = new NoteService();
    for (Note note : notes) {
        boolean success = noteService.saveOrUpdateNote(note);
        if (!success) {
            JOptionPane.showMessageDialog(panel, "Erreur lors de l'enregistrement des notes.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    JOptionPane.showMessageDialog(panel, "Notes enregistrées avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
});

JButton addStudentButton = new JButton("Ajouter un étudiant");
addStudentButton.addActionListener(e -> {
    // Ouvrir une boîte de dialogue pour ajouter un étudiant
    JTextField studentIdField = new JTextField();
    JTextField studentNameField = new JTextField();
    JTextField studentSurnameField = new JTextField();
    JTextField controleContinuField = new JTextField();
    JTextField examenField = new JTextField();
    JTextField tpField = new JTextField();

    JPanel dialogPanel = new JPanel(new GridLayout(0, 2));
    dialogPanel.add(new JLabel("ID Étudiant :"));
    dialogPanel.add(studentIdField);
    dialogPanel.add(new JLabel("Nom :"));
    dialogPanel.add(studentNameField);
    dialogPanel.add(new JLabel("Prénom :"));
    dialogPanel.add(studentSurnameField);
    dialogPanel.add(new JLabel("Contrôle Continu :"));
    dialogPanel.add(controleContinuField);
    dialogPanel.add(new JLabel("Examen :"));
    dialogPanel.add(examenField);
    dialogPanel.add(new JLabel("TP :"));
    dialogPanel.add(tpField);

    int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Ajouter un étudiant", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            String studentName = studentNameField.getText();
            String studentSurname = studentSurnameField.getText();
            double controleContinu = Double.parseDouble(controleContinuField.getText());
            double examen = Double.parseDouble(examenField.getText());
            double tp = Double.parseDouble(tpField.getText());
            double noteFinale = (controleContinu + examen + tp) / 3;
            String validation = noteFinale >= 10 ? "Validée" : "Non validée";

            // Ajouter l'étudiant au tableau
            model.addRow(new Object[] {
                studentId,
                studentName,
                studentSurname,
                controleContinu,
                examen,
                tp,
                noteFinale,
                validation
            });

            // Ajouter l'étudiant dans la base de données
            NoteService noteService = new NoteService();
            Note note = new Note();
            note.setEtudiantId(studentId);
            note.setCoursId(currentCoursId);
            note.setControleContinu(controleContinu);
            note.setExamen(examen);
            note.setTp(tp);
            note.setNoteFinale(noteFinale);
            note.setValidation(validation);

            boolean success = noteService.saveOrUpdateNote(note);
            if (!success) {
                JOptionPane.showMessageDialog(panel, "Erreur lors de l'ajout de l'étudiant.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Étudiant ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Veuillez entrer des valeurs valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
});

JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
buttonPanel.add(saveButton);
buttonPanel.add(addStudentButton);

panel.add(buttonPanel, BorderLayout.SOUTH);

return panel;
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
        contentPanel.add(createSchedulePanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Planning des cours");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        // Sélecteur de semaine
        JComboBox<String> weekSelector = new JComboBox<>(new String[] {
                "Semaine 1", "Semaine 2", "Semaine 3", "Semaine 4"
        });
        weekSelector.setPreferredSize(new Dimension(150, 35));
        weekSelector.setFont(SUBTITLE_FONT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(weekSelector, BorderLayout.EAST);

        // Grille du planning
        JPanel scheduleGrid = new JPanel(new GridLayout(8, 6, 3, 3)); // Adjusted rows for more time slots
        scheduleGrid.setBackground(EMSI_GREEN); // Use EMSI green for grid lines

        // En-têtes des colonnes (jours)
        String[] days = { "", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi" };
        // Adjusted hours array to include all unique time slots from the data
        String[] hours = { "08:30-10:30", "10:00-12:00", "10:45-12:45", "14:00-16:00", "16:15-18:15", "18:30-20:30",
                "08:30-11:30" };

        // Map to store timetable entries by day and hour
        Map<String, Map<String, Emploi_de_temps>> timetableMap = new HashMap<>();

        // Fetch timetable data for the current teacher
        EmploiDuTempsService emploiDuTempsService = new EmploiDuTempsService();
        List<Emploi_de_temps> teacherTimetable = emploiDuTempsService.getEmploiDuTempsParEnseignant(currentTeacherId);

        // Map English day names to French day names
        Map<String, String> englishToFrenchDays = new HashMap<>();
        englishToFrenchDays.put("Monday", "Lundi");
        englishToFrenchDays.put("Tuesday", "Mardi");
        englishToFrenchDays.put("Wednesday", "Mercredi");
        englishToFrenchDays.put("Thursday", "Jeudi");
        englishToFrenchDays.put("Friday", "Vendredi");
        // Add other days if necessary (Saturday, Sunday)
        englishToFrenchDays.put("Saturday", "Samedi");
        englishToFrenchDays.put("Sunday", "Dimanche");

        // Populate the timetable map
        for (Emploi_de_temps entry : teacherTimetable) {
            String englishDay = entry.getJourSemaine();
            String frenchDay = englishToFrenchDays.get(englishDay);

            System.out.println("DEBUG: Processing entry: " + entry); // Added logging
            System.out.println("DEBUG: Mapped English day " + englishDay + " to French day " + frenchDay); // Added
                                                                                                           // logging

            if (frenchDay != null) {
                String startTime = entry.getHeureDebut().substring(0, 5);
                String endTime = entry.getHeureFin().substring(0, 5);
                String timeSlot = startTime + "-" + endTime;

                System.out.println("DEBUG: Processing time slot: " + timeSlot); // Added logging

                timetableMap.computeIfAbsent(frenchDay, k -> new HashMap<>()).put(timeSlot, entry);
                System.out.println(
                        "DEBUG: Added entry to timetableMap for day " + frenchDay + " and time slot " + timeSlot); // Added
                                                                                                                   // logging
            } else {
                System.out.println("DEBUG: No French mapping found for English day: " + englishDay); // Added logging
            }
        }

        // Création de la grille
        for (int i = 0; i < 8; i++) { // Loop through rows (including hour headers)
            for (int j = 0; j < 6; j++) { // Loop through columns (including day headers)
                JPanel cell = new JPanel(new BorderLayout());
                cell.setBorder(BorderFactory.createEmptyBorder());
                cell.setBackground(BACKGROUND_WHITE); // Set cell background to white

                String currentDay = days[j]; // Get French day from grid header

                if (i > 0 && j > 0) { // Only process cells within the timetable grid area (not headers)
                    String currentTimeSlot = hours[i - 1]; // Get time slot from hours array using row index
                    System.out.println(
                            "DEBUG: Checking timetableMap for day " + currentDay + " and time slot " + currentTimeSlot); // Added
                                                                                                                         // logging
                    if (timetableMap.containsKey(currentDay)
                            && timetableMap.get(currentDay).containsKey(currentTimeSlot)) {
                        System.out.println("DEBUG: Timetable entry found for day " + currentDay + " and time slot "
                                + currentTimeSlot); // Added logging
                        Emploi_de_temps courseEntry = timetableMap.get(currentDay).get(currentTimeSlot);

                        System.out.println("DEBUG: Calling getJPanel for course entry: " + courseEntry); // Added
                                                                                                         // logging
                        JPanel courseDetailsPanel = getJPanel(courseEntry);

                        if (courseDetailsPanel != null) { // Check if panel is not null
                            cell.setBackground(EMSI_LIGHT_GRAY); // Highlight cells with courses
                            cell.add(courseDetailsPanel, BorderLayout.CENTER);
                            System.out.println("DEBUG: Added courseDetailsPanel to cell for " + currentDay + " "
                                    + currentTimeSlot); // Added logging
                        } else {
                            System.out.println("DEBUG: getJPanel returned null for entry: " + courseEntry); // Added
                                                                                                            // logging
                        }
                    } else {
                        System.out.println("DEBUG: No timetable entry found for day " + currentDay + " and time slot "
                                + currentTimeSlot); // Added logging
                    }
                }

                if (i == 0) {
                    // En-têtes des jours
                    JLabel dayLabel = new JLabel(days[j], SwingConstants.CENTER);
                    dayLabel.setFont(HEADING_FONT);
                    dayLabel.setForeground(Color.WHITE); // White text for headers
                    dayLabel.setBackground(EMSI_GREEN); // Green background for headers
                    dayLabel.setOpaque(true);
                    dayLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    cell.add(dayLabel, BorderLayout.CENTER);
                } else if (j == 0) {
                    // En-têtes des heures
                    JLabel hourLabel = new JLabel(hours[i - 1], SwingConstants.CENTER);
                    hourLabel.setFont(HEADING_FONT);
                    hourLabel.setForeground(Color.WHITE); // White text for headers
                    hourLabel.setBackground(EMSI_GREEN); // Green background for headers
                    hourLabel.setOpaque(true);
                    hourLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    cell.add(hourLabel, BorderLayout.CENTER);
                }

                scheduleGrid.add(cell);
            }
        }

        JScrollPane scrollPane = new JScrollPane(scheduleGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Panel des actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align to the right
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton printButton = new JButton("Imprimer le planning");

        printButton.setBackground(EMSI_GREEN);
        printButton.setForeground(Color.WHITE);
        printButton.setFont(SUBTITLE_FONT);
        printButton.setFocusPainted(false);
        printButton.setBorderPainted(false);
        printButton.setPreferredSize(new Dimension(180, 35)); // Set preferred size

        actionPanel.add(printButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // Ensure the panel is updated
        panel.revalidate();
        panel.repaint();

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
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Messagerie");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        // Bouton nouveau message
        JButton newMessageButton = new JButton("Nouveau message");
        newMessageButton.setBackground(EMSI_GREEN);
        newMessageButton.setForeground(Color.WHITE);
        newMessageButton.setFont(SUBTITLE_FONT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newMessageButton, BorderLayout.EAST);

        // Panel principal divisé en deux parties
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBackground(BACKGROUND_WHITE);

        // Liste des conversations (gauche)
        JPanel conversationsPanel = new JPanel(new BorderLayout());
        conversationsPanel.setBackground(Color.WHITE);
        conversationsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Barre de recherche des conversations
        JTextField searchField = new JTextField("Rechercher une conversation...");
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(SUBTITLE_FONT);
        searchField.setForeground(Color.GRAY);

        // Liste des conversations
        DefaultListModel<String> conversationsModel = new DefaultListModel<>();
        conversationsModel.addElement("Mohammed Alaoui - INF101");
        conversationsModel.addElement("Fatima Benali - INF102");
        conversationsModel.addElement("Karim Chraibi - INF201");
        conversationsModel.addElement("Sara Dahmani - INF202");
        conversationsModel.addElement("Youssef El Fathi - INF301");

        JList<String> conversationsList = new JList<>(conversationsModel);
        conversationsList.setFont(SUBTITLE_FONT);
        conversationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationsList.setFixedCellHeight(50);

        JScrollPane conversationsScrollPane = new JScrollPane(conversationsList);
        conversationsScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        conversationsPanel.add(searchField, BorderLayout.NORTH);
        conversationsPanel.add(conversationsScrollPane, BorderLayout.CENTER);

        // Zone de conversation (droite)
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);

        // En-tête de la conversation
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(Color.WHITE);
        chatHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel chatTitle = new JLabel("Mohammed Alaoui - INF101");
        chatTitle.setFont(HEADING_FONT);
        chatTitle.setForeground(EMSI_GRAY);

        chatHeader.add(chatTitle, BorderLayout.WEST);

        // Zone des messages
        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);

        // Messages d'exemple
        addMessage(messagesPanel, "Bonjour professeur, j'ai une question concernant le TP de la semaine dernière.",
                false);
        addMessage(messagesPanel, "Bonjour Mohammed, je vous écoute.", true);
        addMessage(messagesPanel, "Je n'ai pas compris la partie sur les collections en Java.", false);
        addMessage(messagesPanel,
                "Je peux vous expliquer cela en détail. Les collections en Java sont des structures de données qui permettent de stocker et manipuler des groupes d'objets.",
                true);

        JScrollPane messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Zone de saisie
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);

        JTextField messageInput = new JTextField();
        messageInput.setFont(SUBTITLE_FONT);
        messageInput.setPreferredSize(new Dimension(0, 40));

        JButton sendButton = new JButton("Envoyer");
        sendButton.setBackground(EMSI_GREEN);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(SUBTITLE_FONT);

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(chatHeader, BorderLayout.NORTH);
        chatPanel.add(messagesScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(conversationsPanel);
        splitPane.setRightComponent(chatPanel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

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
        panel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Gestion des absences");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        // Course selector
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        coursePanel.setBackground(Color.WHITE);
        JComboBox<String> courseSelector = new JComboBox<>();

        // Remplir le sélecteur de cours
        CoursService coursService = new CoursService();
        List<com.emsi.gestionuniv.model.academic.cours> coursList = coursService.getCoursByEnseignant(currentTeacherId);
        for (cours c : coursList) {
            courseSelector.addItem(c.getCode() + " - " + c.getTitre());
        }

        coursePanel.add(new JLabel("Sélectionner un cours : "));
        coursePanel.add(courseSelector);
        header.add(coursePanel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        String[] columns = { "Matricule", "Nom", "Prénom", "Date", "Justifiée", "Justification", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Seules les colonnes de justification sont éditables
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 102, 0));
        table.getTableHeader().setForeground(Color.WHITE);

        // Remplir le tableau avec les données de la base
        AbscenceService absenceService = new AbscenceService();
        List<Abscence> absences = absenceService.getAbsencesByCours(currentCoursId);
        for (Abscence absence : absences) {
            Object[] row = {
                    absence.getEtudiant().getMatricule(),
                    absence.getEtudiant().getNom(),
                    absence.getEtudiant().getPrenom(),
                    absence.getDate(),
                    absence.isJustifiee(),
                    absence.getJustification(),
                    "Modifier"
            };
            model.addRow(row);
        }

        // Ajouter un écouteur pour la modification des absences
        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 4 || column == 5) {
                    Abscence absence = absences.get(row);
                    if (column == 4) {
                        absence.setJustifiee((Boolean) table.getValueAt(row, column));
                    } else {
                        absence.setJustification((String) table.getValueAt(row, column));
                    }
                    absenceService.updateAbsence(absence);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Color.WHITE);

        int[] stats = absenceService.getAbsenceStats(currentCoursId);
        JLabel totalLabel = new JLabel("Total des absences : " + stats[0]);
        JLabel justifiedLabel = new JLabel("Absences justifiées : " + stats[1]);
        JLabel unjustifiedLabel = new JLabel("Absences non justifiées : " + stats[2]);

        for (JLabel label : new JLabel[] { totalLabel, justifiedLabel, unjustifiedLabel }) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            statsPanel.add(label);
        }

        tablePanel.add(statsPanel, BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton addButton = new JButton("Ajouter une absence");
        JButton exportButton = new JButton("Exporter le rapport");

        for (JButton button : new JButton[] { addButton, exportButton }) {
            button.setBackground(new Color(0, 102, 0));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            actionPanel.add(button);
        }

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
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
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
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
     * 
     * @param title Titre de la statistique
     * @param value Valeur numérique à afficher
     * @param icon  Icône Unicode
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
                    g2d.drawRoundRect(i, i, getWidth() - 2 * i - 1, getHeight() - 2 * i - 1, 12, 12);
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
        iconLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);

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



}