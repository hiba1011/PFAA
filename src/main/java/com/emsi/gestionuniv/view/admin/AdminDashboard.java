package com.emsi.gestionuniv.view.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import com.emsi.gestionuniv.app.MainApplication;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.stream.Collectors;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.model.academic.cours;

/**
 * Tableau de bord administrateur avec un style moderne inspiré du dashboard
 * enseignant.
 */
public class AdminDashboard extends JFrame {
    // Couleurs et polices inspirées du dashboard enseignant
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font STAT_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font STAT_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JPanel contentPanel;
    private JPanel menuPanel;
    private String username;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private JTextField photoField;
    private JButton choosePhotoButton;
    private String selectedPhotoPath = "";

    public AdminDashboard(String username) {
        if (!"admin".equals(username)) {
            JOptionPane.showMessageDialog(this,
                    "Accès non autorisé",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        this.username = username;
        setTitle("Tableau de bord administrateur");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Look and feel moderne
        setUIProperties();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_WHITE);

        // Header moderne
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Sidebar moderne
        menuPanel = createSidebarPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Card panel pour les vues
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(EMSI_LIGHT_GRAY);

        cardPanel.add(createDashboardPanel(), "dashboard");
        cardPanel.add(createStudentsPanel(), "students");
        cardPanel.add(createTeachersPanel(), "teachers");
        cardPanel.add(createCoursesPanel(), "courses");
        cardPanel.add(createSettingsPanel(), "settings");
        cardPanel.add(createConseilsPanel(), "conseils");
        cardPanel.add(createAbsencesPanel(), "absences");
        cardPanel.add(createEmploiDuTempsPdfPanel(), "emploi_du_temps_pdf");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
        cardPanel.add(createPlanningExamensPanel(), "planning_examens");
        
    }
   private JPanel createPlanningExamensPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Déposer un planning d'examen (PDF)");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titleLabel.setForeground(new Color(0, 148, 68));
    titleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
    panel.add(titleLabel, BorderLayout.NORTH);

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
    formPanel.setOpaque(false);

    // Sélecteur type (étudiant ou enseignant)
    JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Etudiant", "Enseignant" });
    // Sélecteur classe
    JComboBox<String> classeCombo = new JComboBox<>(new com.emsi.gestionuniv.service.CoursService().getAllGroupes().toArray(new String[0]));
    // Sélecteur enseignant (par ID ou nom)
    List<com.emsi.gestionuniv.model.user.Teacher> teachers = new com.emsi.gestionuniv.service.TeacherService().getAllTeachers();
    String[] teacherItems = teachers.stream()
        .map(t -> t.getId() + " - " + t.getPrenom() + " " + t.getNom())
        .toArray(String[]::new);
    JComboBox<String> enseignantCombo = new JComboBox<>(teacherItems);

    // Sélection dynamique
    typeCombo.addActionListener(e -> {
        if (typeCombo.getSelectedItem().equals("Etudiant")) {
            classeCombo.setEnabled(true);
            enseignantCombo.setEnabled(false);
        } else {
            classeCombo.setEnabled(false);
            enseignantCombo.setEnabled(true);
        }
    });
    classeCombo.setEnabled(true);
    enseignantCombo.setEnabled(false);

    JTextField titreField = new JTextField();
    JTextField cheminField = new JTextField();
    cheminField.setEditable(false);
    JButton choisirBtn = new JButton("Choisir PDF");
    choisirBtn.addActionListener(e -> {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(panel);
        if (res == JFileChooser.APPROVE_OPTION) {
            cheminField.setText(fc.getSelectedFile().getAbsolutePath());
        }
    });

    formPanel.add(new JLabel("Type :"));
    formPanel.add(typeCombo);
    formPanel.add(new JLabel("Classe :"));
    formPanel.add(classeCombo);
    formPanel.add(new JLabel("Enseignant :"));
    formPanel.add(enseignantCombo);
    formPanel.add(new JLabel("Titre :"));
    formPanel.add(titreField);
    formPanel.add(new JLabel("Fichier PDF :"));
    formPanel.add(cheminField);
    formPanel.add(new JLabel(""));
    formPanel.add(choisirBtn);

    JButton ajouterBtn = new JButton("Déposer le planning");
    ajouterBtn.setBackground(new Color(0, 148, 68));
    ajouterBtn.setForeground(Color.WHITE);
    ajouterBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
    ajouterBtn.addActionListener(e -> {
        String type = typeCombo.getSelectedItem().equals("Etudiant") ? "etudiant" : "enseignant";
        String cible;
        if (type.equals("etudiant")) {
            cible = (String) classeCombo.getSelectedItem();
        } else {
            // Récupère l'ID de l'enseignant sélectionné (avant le tiret)
            String selected = (String) enseignantCombo.getSelectedItem();
            if (selected == null || !selected.contains(" - ")) {
                JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un enseignant.");
                return;
            }
            cible = selected.split(" - ")[0].trim();
        }
        String titre = titreField.getText().trim();
        String chemin = cheminField.getText().trim();
        if (titre.isEmpty() || cible == null || cible.isEmpty() || chemin.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Veuillez remplir tous les champs.");
            return;
        }
        com.emsi.gestionuniv.model.academic.PlanningExamen planning = new com.emsi.gestionuniv.model.academic.PlanningExamen();
        planning.setType(type);
        planning.setCible(cible);
        planning.setTitre(titre);
        planning.setCheminPdf(chemin);
        planning.setClasse(cible); // Garder la compatibilité avec l'ancien champ
        new com.emsi.gestionuniv.service.PlanningExamenService().ajouterPlanning(planning);
        
        // Si c'est un planning d'examen pour une classe, l'ajouter aussi pour chaque enseignant de cette classe
        if (type.equals("etudiant")) {
            // Récupérer tous les cours de la classe
            java.util.List<com.emsi.gestionuniv.model.academic.cours> coursClasse = new com.emsi.gestionuniv.service.CoursService().getAllCours();
            java.util.Set<Integer> enseignants = new java.util.HashSet<>();
            for (com.emsi.gestionuniv.model.academic.cours c : coursClasse) {
                // On suppose que le champ 'niveau' ou 'filiere' ou 'intitule' ou 'code' contient le nom du groupe/classe
                // Ici, on compare avec le nom de la classe sélectionnée
                if (c.getNiveau() != null && c.getNiveau().equalsIgnoreCase(cible)) {
                    enseignants.add(c.getEnseignantId());
                } else if (c.getFiliere() != null && c.getFiliere().equalsIgnoreCase(cible)) {
                    enseignants.add(c.getEnseignantId());
                } else if (c.getIntitule() != null && c.getIntitule().equalsIgnoreCase(cible)) {
                    enseignants.add(c.getEnseignantId());
                } else if (c.getCode() != null && c.getCode().equalsIgnoreCase(cible)) {
                    enseignants.add(c.getEnseignantId());
                }
            }
            for (Integer enseignantId : enseignants) {
                if (enseignantId != null && enseignantId > 0) {
                    System.out.println("[DEBUG] Ajout planning d'examen pour enseignant ID=" + enseignantId + ", classe=" + cible + ", titre=" + titre);
                    com.emsi.gestionuniv.model.academic.PlanningExamen planningEns = new com.emsi.gestionuniv.model.academic.PlanningExamen();
                    planningEns.setType("enseignant");
                    planningEns.setCible(String.valueOf(enseignantId));
                    planningEns.setTitre(titre);
                    planningEns.setCheminPdf(chemin);
                    planningEns.setClasse(cible); // Garder la compatibilité
                    new com.emsi.gestionuniv.service.PlanningExamenService().ajouterPlanning(planningEns);
                }
            }
        }
        JOptionPane.showMessageDialog(panel, "Planning d'examen déposé !");
        titreField.setText("");
        cheminField.setText("");
    });

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnPanel.setOpaque(false);
    btnPanel.add(ajouterBtn);

    panel.add(formPanel, BorderLayout.CENTER);
    panel.add(btnPanel, BorderLayout.SOUTH);

    return panel;
}

    private JPanel createEmploiDuTempsPdfPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Déposer un emploi du temps (PDF)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 148, 68));
        titleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        formPanel.setOpaque(false);

        // Sélecteur type (étudiant ou enseignant)
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Etudiant", "Enseignant" });
        // Sélecteur classe
        JComboBox<String> classeCombo = new JComboBox<>(new CoursService().getAllGroupes().toArray(new String[0]));
        // Sélecteur enseignant (par ID ou nom)
        List<Teacher> teachers = new TeacherService().getAllTeachers();
        String[] teacherItems = teachers.stream()
            .map(t -> t.getId() + " - " + t.getPrenom() + " " + t.getNom())
            .toArray(String[]::new);
        JComboBox<String> enseignantCombo = new JComboBox<>(teacherItems);

        // Sélection dynamique
        typeCombo.addActionListener(e -> {
            if (typeCombo.getSelectedItem().equals("Etudiant")) {
                classeCombo.setEnabled(true);
                enseignantCombo.setEnabled(false);
            } else {
                classeCombo.setEnabled(false);
                enseignantCombo.setEnabled(true);
            }
        });
        classeCombo.setEnabled(true);
        enseignantCombo.setEnabled(false);

        JTextField titreField = new JTextField();
        JTextField cheminField = new JTextField();
        cheminField.setEditable(false);
        JButton choisirBtn = new JButton("Choisir PDF");
        choisirBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(panel);
            if (res == JFileChooser.APPROVE_OPTION) {
                cheminField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        formPanel.add(new JLabel("Type :"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Classe :"));
        formPanel.add(classeCombo);
        formPanel.add(new JLabel("Enseignant :"));
        formPanel.add(enseignantCombo);
        formPanel.add(new JLabel("Titre :"));
        formPanel.add(titreField);
        formPanel.add(new JLabel("Fichier PDF :"));
        formPanel.add(cheminField);
        formPanel.add(new JLabel(""));
        formPanel.add(choisirBtn);

        JButton ajouterBtn = new JButton("Déposer l'emploi du temps");
        ajouterBtn.setBackground(new Color(0, 148, 68));
        ajouterBtn.setForeground(Color.WHITE);
        ajouterBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        ajouterBtn.addActionListener(e -> {
            String type = typeCombo.getSelectedItem().equals("Etudiant") ? "etudiant" : "enseignant";
            String cible;
            if (type.equals("etudiant")) {
                cible = (String) classeCombo.getSelectedItem();
            } else {
                // Récupère l'ID de l'enseignant sélectionné (avant le tiret)
                String selected = (String) enseignantCombo.getSelectedItem();
                if (selected == null || !selected.contains(" - ")) {
                    JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un enseignant.");
                    return;
                }
                cible = selected.split(" - ")[0].trim();
            }
            String titre = titreField.getText().trim();
            String chemin = cheminField.getText().trim();
            if (titre.isEmpty() || cible == null || cible.isEmpty() || chemin.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Veuillez remplir tous les champs.");
                return;
            }
            com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf emploi = new com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf();
            emploi.setType(type);
            emploi.setCible(cible);
            emploi.setTitre(titre);
            emploi.setCheminPdf(chemin);
            new com.emsi.gestionuniv.service.EmploiDuTempsPdfService().ajouterEmploi(emploi);
            // Si c'est un emploi du temps pour une classe, l'ajouter aussi pour chaque enseignant de cette classe
            if (type.equals("etudiant")) {
                // Récupérer tous les cours de la classe
                java.util.List<com.emsi.gestionuniv.model.academic.cours> coursClasse = new com.emsi.gestionuniv.service.CoursService().getAllCours();
                java.util.Set<Integer> enseignants = new java.util.HashSet<>();
                for (com.emsi.gestionuniv.model.academic.cours c : coursClasse) {
                    // On suppose que le champ 'niveau' ou 'filiere' ou 'intitule' ou 'code' contient le nom du groupe/classe
                    // Ici, on compare avec le nom de la classe sélectionnée
                    if (c.getNiveau() != null && c.getNiveau().equalsIgnoreCase(cible)) {
                        enseignants.add(c.getEnseignantId());
                    } else if (c.getFiliere() != null && c.getFiliere().equalsIgnoreCase(cible)) {
                        enseignants.add(c.getEnseignantId());
                    } else if (c.getIntitule() != null && c.getIntitule().equalsIgnoreCase(cible)) {
                        enseignants.add(c.getEnseignantId());
                    } else if (c.getCode() != null && c.getCode().equalsIgnoreCase(cible)) {
                        enseignants.add(c.getEnseignantId());
                    }
                }
                for (Integer enseignantId : enseignants) {
                    if (enseignantId != null && enseignantId > 0) {
                        System.out.println("[DEBUG] Ajout emploi du temps pour enseignant ID=" + enseignantId + ", classe=" + cible + ", titre=" + titre);
                        com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf emploiEns = new com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf();
                        emploiEns.setType("enseignant");
                        emploiEns.setCible(String.valueOf(enseignantId));
                        emploiEns.setTitre(titre);
                        emploiEns.setCheminPdf(chemin);
                        new com.emsi.gestionuniv.service.EmploiDuTempsPdfService().ajouterEmploi(emploiEns);
                    }
                }
            }
            JOptionPane.showMessageDialog(panel, "Emploi du temps déposé !");
            titreField.setText("");
            cheminField.setText("");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(ajouterBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setUIProperties() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Panel.background", BACKGROUND_WHITE);
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", MENU_FONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_GREEN,
                        getWidth(), 0, EMSI_DARK_GREEN);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(-20, -20, 150, 150);
                g2d.fillOval(getWidth() - 100, getHeight() - 40, 80, 80);
                g2d.dispose();
            }
        };
        panel.setBorder(new EmptyBorder(18, 30, 18, 30));

        // --- Ajout du bouton flèche retour ---
        JButton backButton = new JButton("\u2190"); // Unicode flèche gauche
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(0, 0, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setToolTipText("Retour");
        backButton.addActionListener(e -> {
            // Ferme le dashboard et retourne à l'écran précédent (login)
            dispose();
            new com.emsi.gestionuniv.view.Login.AdminLogin();
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(SUBTITLE_FONT);
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(MENU_FONT);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment vous déconnecter?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                new MainApplication().main(new String[0]);
                dispose();
            }
        });

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                for (int i = 0; i < 6; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawLine(getWidth() - i, 0, getWidth() - i, getHeight());
                }
                g2d.dispose();
            }
        };
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(new EmptyBorder(25, 15, 25, 15));

        JLabel logoLabel = new JLabel("EMSI");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(EMSI_GREEN);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebarPanel.add(logoLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] menuItems = {
                " Tableau de bord",
                "  Étudiants",
                "  Enseignants",
                "  Cours",
                " Absences",
                " Conseils disciplinaires"
        };
        String[] menuCards = {
                "dashboard", "students", "teachers", "courses", "absences", "conseils"
        };

        menuButtons.clear();
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], menuCards[i], i == 0);
            menuButtons.add(menuButton);
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        JButton emploiBtn = createMenuButton("Emploi du temps PDF", "emploi_du_temps_pdf", false);
        menuButtons.add(emploiBtn);
        sidebarPanel.add(emploiBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        // Après les autres boutons
        JButton planningBtn = createMenuButton("Planning examens", "planning_examens", false);
        menuButtons.add(planningBtn);
        sidebarPanel.add(planningBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton logoutButton = createMenuButton("Déconnexion", "logout", false);
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment vous déconnecter?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                new MainApplication().main(new String[0]);
                dispose();
            }
        });
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }
private JPanel createAbsencesPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Gestion des Absences par Classe");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(new Color(0, 148, 68));
    titleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
    panel.add(titleLabel, BorderLayout.NORTH);

    // Sélecteur de classe
    CoursService coursService = new CoursService();
    java.util.List<String> classes = coursService.getAllGroupes();
    final java.util.List<com.emsi.gestionuniv.model.academic.Abscence>[] absencesHolder = new java.util.List[]{new ArrayList<>()};
    JComboBox<String> classeCombo = new JComboBox<>(classes.toArray(new String[0]));
    classeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    classeCombo.setPreferredSize(new Dimension(220, 32));
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.setOpaque(false);
    topPanel.add(new JLabel("Classe : "));
    topPanel.add(classeCombo);
    panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

    // Tableau des absences
    String[] columns = { "Étudiant", "Cours", "Date", "Justification", "Justifiée", "Action" };
    Object[][] data = {};
    DefaultTableModel model = new DefaultTableModel(data, columns) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Justification et Action sont éditables
            return column == 3 || column == 5;
        }
    };
    JTable table = new JTable(model);
    table.setRowHeight(28);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
    table.getTableHeader().setBackground(new Color(245, 245, 245));
    table.getTableHeader().setForeground(new Color(0, 148, 68));
    // Rendre la colonne Action éditable et affichée avec un JComboBox uniquement (non éditable)
    JComboBox<String> actionCombo = new JComboBox<>(new String[]{"", "Blâme oral", "Conseil disciplinaire", "Exclusion"});
    actionCombo.setEditable(false); // Empêche la saisie manuelle
    DefaultCellEditor actionEditor = new DefaultCellEditor(actionCombo);
    actionEditor.setClickCountToStart(1);
    table.getColumnModel().getColumn(5).setCellEditor(actionEditor);
    table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public void setValue(Object value) {
            setText(value != null ? value.toString() : "");
        }
    });
    // Rendre la colonne Justification éditable et affichée avec un JComboBox uniquement (non éditable)
    JComboBox<String> motifCombo = new JComboBox<>(new String[]{"", "Motif administratif", "Certificat médical", "Mot justificatif"});
    motifCombo.setEditable(false); // Empêche la saisie manuelle
    DefaultCellEditor motifEditor = new DefaultCellEditor(motifCombo);
    motifEditor.setClickCountToStart(1);
    table.getColumnModel().getColumn(3).setCellEditor(motifEditor);
    table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public void setValue(Object value) {
            setText(value != null ? value.toString() : "");
        }
    });
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 148, 68), 1));
    panel.add(scrollPane, BorderLayout.CENTER);

    // Rafraîchir le tableau selon la classe sélectionnée
    classeCombo.addActionListener(e -> {
        String selectedClasse = (String) classeCombo.getSelectedItem();
        absencesHolder[0] = new com.emsi.gestionuniv.service.AbscenceService().getAbsencesByClasse(selectedClasse);
        EtudiantService etuService = new EtudiantService();
        CoursService coursServ = new CoursService();
        Object[][] rows = new Object[absencesHolder[0].size()][columns.length];
        for (int i = 0; i < absencesHolder[0].size(); i++) {
            var a = absencesHolder[0].get(i);
            String etuNom = etuService.getNomById(a.getEtudiantId());
            String coursNom = coursServ.getIntituleById(a.getCoursId());
            rows[i][0] = etuNom;
            rows[i][1] = coursNom;
            rows[i][2] = a.getDate();
            rows[i][3] = (a.getJustificationTexte() != null && !a.getJustificationTexte().isEmpty()) ? a.getJustificationTexte() : "";
            rows[i][4] = a.isJustifiee() ? "Oui" : "Non";
            rows[i][5] = (a.getAction() != null) ? a.getAction() : "";
        }
        model.setDataVector(rows, columns);
        // Réappliquer les cell editors et renderers après chaque setDataVector
        table.getColumnModel().getColumn(3).setCellEditor(motifEditor);
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value != null ? value.toString() : "");
            }
        });
        table.getColumnModel().getColumn(5).setCellEditor(actionEditor);
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value != null ? value.toString() : "");
            }
        });
    });

    // Listener pour édition de justification ou action
    model.addTableModelListener(e -> {
        if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row < 0 || col < 0) return;
            Abscence abs = absencesHolder[0].get(row);
            if (col == 3) { // Justification motif
                String motif = (String) model.getValueAt(row, 3);
                abs.setJustificationTexte(motif);
                abs.setJustifiee(motif != null && !motif.isEmpty());
                new com.emsi.gestionuniv.service.AbscenceService().updateJustificationTexte(abs.getId(), motif);
            } else if (col == 5) { // Action
                String action = (String) model.getValueAt(row, 5);
                abs.setAction(action);
                new com.emsi.gestionuniv.service.AbscenceService().updateAction(abs.getId(), action);
                if ("Conseil disciplinaire".equals(action)) {
                    // Ajouter à la table des conseils disciplinaires
                    com.emsi.gestionuniv.model.academic.ConseilDisciplinaire conseil = new com.emsi.gestionuniv.model.academic.ConseilDisciplinaire();
                    conseil.setEtudiantId(abs.getEtudiantId());
                    conseil.setCoursId(abs.getCoursId());
                    conseil.setType("Conseil disciplinaire");
                    conseil.setDate(java.time.LocalDateTime.now());
                    conseil.setCommentaire("Conseil disciplinaire suite à une absence non justifiée le " + abs.getDate());
                    new com.emsi.gestionuniv.service.ConseilDisciplinaireService().ajouterConseil(conseil);
                    // Notifier l'étudiant
                    new com.emsi.gestionuniv.service.MessageService().notifierEtudiantConseil(abs.getEtudiantId(), abs.getDate());
                    // Rafraîchir le panneau des conseils disciplinaires
                    if (cardPanel != null && cardLayout != null) {
                        cardPanel.add(createConseilsPanel(), "conseils");
                        cardLayout.show(cardPanel, "conseils");
                    }
                }
            }
        }
    });

    // Déclenche le chargement initial
    if (classeCombo.getItemCount() > 0)
        classeCombo.setSelectedIndex(0);

    return panel;
}

    private JButton createMenuButton(String text, String card, boolean selected) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            private boolean isSelected = selected;

            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(MENU_FONT);
                setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2d.setColor(EMSI_GREEN);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (hovering) {
                    g2d.setColor(EMSI_LIGHT_GRAY);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2d.setFont(MENU_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int x = 16;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.setColor(isSelected ? Color.WHITE : EMSI_GRAY);
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }

            public void setSelected(boolean selected) {
                this.isSelected = selected;
                repaint();
            }
        };
        button.addActionListener(e -> {
            if ("logout".equals(card))
                return;
            for (JButton b : menuButtons) {
                if (b == button) {
                    ((JButton) b).putClientProperty("selected", true);
                    if (b instanceof JButton)
                        ((JButton) b).setSelected(true);
                } else {
                    ((JButton) b).putClientProperty("selected", false);
                    if (b instanceof JButton)
                        ((JButton) b).setSelected(false);
                }
            }
            cardLayout.show(cardPanel, card);
            menuPanel.repaint();
        });
        button.putClientProperty("selected", selected);
        button.setSelected(selected);
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Titre principal moderne
        JLabel welcomeLabel = new JLabel("Bienvenue dans le tableau de bord administrateur");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(16, 185, 129)); // Vert EMSI moderne
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Cartes statistiques modernes
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 32, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, 32, 0));

        EtudiantService etudiantService = new EtudiantService();
        TeacherService teacherService = new TeacherService();
        CoursService coursService = new CoursService();

        int nbEtudiants = etudiantService.countEtudiants();
        int nbEnseignants = teacherService.countTeachers();
        int nbCours = coursService.countCours();

        statsPanel
                .add(createModernStatCard("Étudiants", String.valueOf(nbEtudiants), "👨‍🎓", new Color(16, 185, 129)));
        statsPanel.add(
                createModernStatCard("Enseignants", String.valueOf(nbEnseignants), "👩‍🏫", new Color(59, 130, 246)));
        statsPanel.add(createModernStatCard("Cours", String.valueOf(nbCours), "📚", new Color(251, 191, 36)));

        // Section top (titre + stats)
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);
        northPanel.add(welcomePanel);
        northPanel.add(statsPanel);

        // Tableau des dernières activités dynamiques
        List<Student> derniersEtudiants = etudiantService.getLastEtudiants(2);
        List<Teacher> derniersEnseignants = teacherService.getLastTeachers(1);
        List<cours> derniersCours = coursService.getLastCours(2);

        List<Object[]> activities = new ArrayList<>();
        if (!derniersEtudiants.isEmpty()) {
            Student s = derniersEtudiants.get(0);
            activities.add(new Object[] { "Ajout d'étudiant", s.getPrenom() + " " + s.getNom(), "Aujourd'hui" });
        }
        if (!derniersCours.isEmpty()) {
            cours c = derniersCours.get(0);
            activities.add(new Object[] { "Ajout de cours", c.getIntitule(), "Aujourd'hui" });
        }
        if (!derniersEnseignants.isEmpty()) {
            Teacher t = derniersEnseignants.get(0);
            activities.add(new Object[] { "Ajout d'enseignant", t.getPrenom() + " " + t.getNom(), "Hier" });
        }
        if (derniersEtudiants.size() > 1) {
            Student s2 = derniersEtudiants.get(1);
            activities.add(new Object[] { "Ajout d'étudiant", s2.getPrenom() + " " + s2.getNom(), "Hier" });
        }
        if (derniersCours.size() > 1) {
            cours c2 = derniersCours.get(1);
            activities.add(new Object[] { "Ajout de cours", c2.getIntitule(), "Avant-hier" });
        }

        DefaultTableModel tableModel = new DefaultTableModel(
                activities.toArray(new Object[0][0]),
                new String[] { "Action", "Description", "Date" });

        JTable activityTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(236, 253, 245)); // Lignes
                                                                                                         // zébrées
                } else {
                    c.setBackground(new Color(209, 250, 229));
                }
                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(8, 12, 8, 12));
                }
                return c;
            }
            
        };
        
        activityTable.setRowHeight(36);
        activityTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        activityTable.getTableHeader().setBackground(new Color(220, 255, 220)); // Vert très clair
        activityTable.getTableHeader().setForeground(new Color(0, 104, 56)); // Vert foncé EMSI
        activityTable.getTableHeader().setPreferredSize(new Dimension(100, 36)); // Hauteur de l'en-tête
        activityTable.setShowGrid(false);
        activityTable.setIntercellSpacing(new Dimension(0, 0));
        activityTable.setSelectionBackground(new Color(209, 250, 229));

        // --- AJOUT POUR RENDRE LES COLONNES VISIBLES ---
        activityTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        activityTable.setPreferredScrollableViewportSize(new Dimension(600, activityTable.getRowHeight() * 6));
        activityTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Action
        activityTable.getColumnModel().getColumn(1).setPreferredWidth(320); // Description
        activityTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Date

        JScrollPane tableScrollPane = new JScrollPane(activityTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Dernières activités",
                1,
                0,
                new Font("Segoe UI", Font.BOLD, 15)));
        tableScrollPane.setBackground(Color.WHITE);
        tableScrollPane.setPreferredSize(new Dimension(0, 220));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(24, 0, 0, 0));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // Carte statistique moderne
    private JPanel createModernStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(6, getHeight() - 18, getWidth() - 12, 12, 12, 12); // ombre douce en bas
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(0, 110));
        card.setOpaque(false);

        // Icône
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        iconLabel.setForeground(accentColor);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Valeur
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(new Color(31, 41, 55));
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Titre
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(accentColor.darker());
        titleLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header avec titre et bouton de gestion
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);

        JButton manageButton = new JButton("Gérer les Étudiants") {
            private boolean hovering = false;
            {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        manageButton.setPreferredSize(new Dimension(180, 35));
        manageButton.setBorderPainted(false);
        manageButton.setFocusPainted(false);
        manageButton.setContentAreaFilled(false);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageButton.addActionListener(e -> {
            StudentManagementDialog dialog = new StudentManagementDialog(this);
            dialog.setVisible(true);
            refreshStudentsTable();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(manageButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        JPanel tablePanel = createStudentsTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2),
                "Liste des Étudiants",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                EMSI_GREEN));

        // Récupération des étudiants
        java.util.List<com.emsi.gestionuniv.model.user.Student> students = new com.emsi.gestionuniv.service.EtudiantService()
                .getAllStudents();
        String[] columns = { "Matricule", "Prénom", "Nom", "Email", "Filière", "Promotion", "Groupe" };
        Object[][] data = new Object[students.size()][columns.length];
        for (int i = 0; i < students.size(); i++) {
            var s = students.get(i);
            data[i][0] = s.getMatricule();
            data[i][1] = s.getPrenom();
            data[i][2] = s.getNom();
            data[i][3] = s.getEmail();
            data[i][4] = s.getFiliere();
            data[i][5] = s.getPromotion();
            data[i][6] = s.getGroupe();
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(EMSI_LIGHT_GRAY);
        table.getTableHeader().setForeground(EMSI_GREEN);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Stocker la référence de la table pour pouvoir la rafraîchir
        this.studentsTable = table;
        this.studentsTableModel = model;

        return panel;
    }

    private void refreshStudentsTable() {
        if (studentsTableModel != null) {
            java.util.List<com.emsi.gestionuniv.model.user.Student> students = new com.emsi.gestionuniv.service.EtudiantService()
                    .getAllStudents();
            studentsTableModel.setRowCount(0);
            for (var s : students) {
                Object[] row = {
                        s.getMatricule(),
                        s.getPrenom(),
                        s.getNom(),
                        s.getEmail(),
                        s.getFiliere(),
                        s.getPromotion(),
                        s.getGroupe()
                };
                studentsTableModel.addRow(row);
            }
        }
    }

    private JPanel createTeachersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header avec titre et bouton de gestion
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("Gestion des Enseignants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);

        JButton manageButton = new JButton("Gérer les Enseignants") {
            private boolean hovering = false;
            {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        manageButton.setPreferredSize(new Dimension(180, 35));
        manageButton.setBorderPainted(false);
        manageButton.setFocusPainted(false);
        manageButton.setContentAreaFilled(false);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageButton.addActionListener(e -> {
            TeacherManagementDialog dialog = new TeacherManagementDialog(this);
            dialog.setVisible(true);
            refreshTeachersTable();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(manageButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        JPanel tablePanel = createTeachersTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeachersTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2),
                "Liste des Enseignants",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                EMSI_GREEN));

        // Récupération des enseignants
        java.util.List<com.emsi.gestionuniv.model.user.Teacher> teachers = new com.emsi.gestionuniv.service.TeacherService()
                .getAllTeachers();
        com.emsi.gestionuniv.service.TeacherService teacherService = new com.emsi.gestionuniv.service.TeacherService();
        String[] columns = { "Matricule", "Prénom", "Nom", "Email", "Telephone", "Spécialité", "Groupes" };
        Object[][] data = new Object[teachers.size()][columns.length];
        for (int i = 0; i < teachers.size(); i++) {
            var t = teachers.get(i);
            data[i][0] = t.getId();
            data[i][1] = t.getPrenom();
            data[i][2] = t.getNom();
            data[i][3] = t.getEmail();
            data[i][4] = t.getTelephone();
            data[i][5] = t.getSpecialite();
            // Récupérer les groupes enseignés
            java.util.List<com.emsi.gestionuniv.service.TeacherService.Classe> groupes = teacherService
                    .getClassesByTeacherId(t.getId());
            String groupesStr = groupes.stream().map(com.emsi.gestionuniv.service.TeacherService.Classe::getNom)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            data[i][6] = groupesStr;
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(EMSI_LIGHT_GRAY);
        table.getTableHeader().setForeground(EMSI_GREEN);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Stocker la référence de la table pour pouvoir la rafraîchir
        this.studentsTable = table;
        this.studentsTableModel = model;

        return panel;
    }

    private void refreshTeachersTable() {
        if (studentsTableModel != null) {
            java.util.List<com.emsi.gestionuniv.model.user.Teacher> teachers = new com.emsi.gestionuniv.service.TeacherService()
                    .getAllTeachers();
            com.emsi.gestionuniv.service.TeacherService teacherService = new com.emsi.gestionuniv.service.TeacherService();
            studentsTableModel.setRowCount(0);
            for (var t : teachers) {
                java.util.List<com.emsi.gestionuniv.service.TeacherService.Classe> groupes = teacherService
                        .getClassesByTeacherId(t.getId());
                String groupesStr = groupes.stream().map(com.emsi.gestionuniv.service.TeacherService.Classe::getNom)
                        .reduce((a, b) -> a + ", " + b).orElse("");
                Object[] row = {
                        t.getId(),
                        t.getPrenom(),
                        t.getNom(),
                        t.getEmail(),
                        t.getTelephone(),
                        t.getSpecialite(),
                        groupesStr
                };
                studentsTableModel.addRow(row);
            }
        }
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header avec titre et bouton de gestion
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("Gestion des Cours");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);

        JButton manageButton = new JButton("Gérer les Cours") {
            private boolean hovering = false;
            {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = hovering ? EMSI_DARK_GREEN : EMSI_GREEN;
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        manageButton.setPreferredSize(new Dimension(180, 35));
        manageButton.setBorderPainted(false);
        manageButton.setFocusPainted(false);
        manageButton.setContentAreaFilled(false);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageButton.addActionListener(e -> {
            CoursManagementDialog dialog = new CoursManagementDialog(this);
            dialog.setVisible(true);
            refreshCoursesTable();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(manageButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        JPanel tablePanel = createCoursesTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCoursesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 2),
                "Liste des Cours",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                EMSI_GREEN));

        // Récupération des cours
        java.util.List<com.emsi.gestionuniv.model.academic.cours> coursList = new com.emsi.gestionuniv.service.CoursService()
                .getAllCours();
        String[] columns = { "Code", "Intitulé", "Filière", "Niveau", "Effectif", "Volume horaire" };
        Object[][] data = new Object[coursList.size()][columns.length];
        for (int i = 0; i < coursList.size(); i++) {
            var c = coursList.get(i);
            data[i][0] = c.getCode();
            data[i][1] = c.getIntitule();
            data[i][2] = c.getFiliere();
            data[i][3] = c.getNiveau();
            data[i][4] = c.getEffectif();
            data[i][5] = c.getVolumeHoraire();

        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(EMSI_LIGHT_GRAY);
        table.getTableHeader().setForeground(EMSI_GREEN);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Stocker la référence de la table pour pouvoir la rafraîchir
        this.studentsTable = table;
        this.studentsTableModel = model;

        return panel;
    }

    private void refreshCoursesTable() {
        if (studentsTableModel != null) {
            java.util.List<com.emsi.gestionuniv.model.academic.cours> coursList = new com.emsi.gestionuniv.service.CoursService()
                    .getAllCours();
            studentsTableModel.setRowCount(0);
            for (var c : coursList) {
                Object[] row = {
                        c.getCode(),
                        c.getIntitule(),
                        c.getFiliere(),
                        c.getNiveau(),
                        c.getEffectif(),
                        c.getVolumeHoraire()
                };
                studentsTableModel.addRow(row);
            }
        }
    }

    private JPanel createSettingsPanel() {
        return createPlaceholderPanel("Paramètres du système");
    }

    private JPanel createConseilsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Conseils disciplinaires");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 148, 68));
        titleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Récupération des conseils
        java.util.List<com.emsi.gestionuniv.model.academic.ConseilDisciplinaire> conseils = new com.emsi.gestionuniv.service.ConseilDisciplinaireService()
                .getAllConseils();

        String[] columns = { "Étudiant", "Cours", "Date", "Justification", "Statut", "Action" };
        Object[][] data = new Object[conseils.size()][columns.length];
        com.emsi.gestionuniv.service.EtudiantService etuService = new com.emsi.gestionuniv.service.EtudiantService();
        com.emsi.gestionuniv.service.CoursService coursService = new com.emsi.gestionuniv.service.CoursService();

        for (int i = 0; i < conseils.size(); i++) {
            var c = conseils.get(i);
            String etudiantNom = etuService.getNomById(c.getEtudiantId());
            String coursNom = coursService.getIntituleById(c.getCoursId());
            data[i][0] = etudiantNom;
            data[i][1] = coursNom;
            data[i][2] = c.getDate().toString();
            data[i][3] = c.getCommentaire();
            data[i][4] = c.getType();
            data[i][5] = "Modifier";
        }

        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(236, 253, 245)); // Lignes
                                                                                                         // zébrées
                } else {
                    c.setBackground(new Color(209, 250, 229));
                }
                c.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(8, 12, 8, 12));
                }
                return c;
            }
        };
        table.setRowHeight(36);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(220, 255, 220)); // Vert très clair
        table.getTableHeader().setForeground(new Color(0, 104, 56)); // Vert foncé EMSI
        table.getTableHeader().setPreferredSize(new Dimension(100, 36)); // Hauteur de l'en-tête
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(209, 250, 229));

        // --- AJOUT POUR RENDRE LES COLONNES VISIBLES ---
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setPreferredScrollableViewportSize(new Dimension(600, table.getRowHeight() * 6));
        table.getColumnModel().getColumn(0).setPreferredWidth(180); // Étudiant
        table.getColumnModel().getColumn(1).setPreferredWidth(320); // Cours
        table.getColumnModel().getColumn(2).setPreferredWidth(140); // Date
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Justification
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Statut
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Action

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 148, 68), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Placeholder moderne pour les autres panels
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        JLabel label = new JLabel(title + " (à personnaliser)");
        label.setFont(TITLE_FONT);
        label.setForeground(EMSI_GRAY);
        panel.add(label);
        return panel;
    }

    // Pour tester cette classe individuellement
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> new AdminDashboard("admin"));
    // }
}