package com.emsi.gestionuniv.view.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import com.emsi.gestionuniv.app.MainApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * Tableau de bord administrateur avec un style moderne inspiré du dashboard enseignant.
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

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
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
                        getWidth(), 0, EMSI_DARK_GREEN
                );
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
        backButton.setBackground(new Color(0,0,0,0));
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

        JLabel titleLabel = new JLabel("Système de Gestion Universitaire - Administration");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);
        leftPanel.add(Box.createHorizontalStrut(18));
        leftPanel.add(titleLabel);

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
                    JOptionPane.YES_NO_OPTION
            );
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
                " Paramètres"
        };
        String[] menuCards = {
                "dashboard", "students", "teachers", "courses", "settings"
        };

        menuButtons.clear();
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], menuCards[i], i == 0);
            menuButtons.add(menuButton);
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        sidebarPanel.add(Box.createVerticalGlue());

        JButton logoutButton = createMenuButton("Déconnexion", "logout", false);
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment vous déconnecter?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (response == JOptionPane.YES_OPTION) {
                new MainApplication().main(new String[0]);
                dispose();
            }
        });
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
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
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
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
            if ("logout".equals(card)) return;
            for (JButton b : menuButtons) {
                if (b == button) {
                    ((JButton) b).putClientProperty("selected", true);
                    if (b instanceof JButton) ((JButton) b).setSelected(true);
                } else {
                    ((JButton) b).putClientProperty("selected", false);
                    if (b instanceof JButton) ((JButton) b).setSelected(false);
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
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Bienvenue dans le tableau de bord administrateur");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(EMSI_GRAY);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createModernStatPanel("Étudiants", "187", "", new Color(33, 150, 243)));
        statsPanel.add(createModernStatPanel("Enseignants", "42", "", new Color(0, 150, 136)));
        statsPanel.add(createModernStatPanel("Cours", "56", "", new Color(255, 152, 0)));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(welcomeLabel, BorderLayout.NORTH);
        northPanel.add(Box.createRigidArea(new Dimension(0, 30)), BorderLayout.CENTER);
        northPanel.add(statsPanel, BorderLayout.SOUTH);

        // Tableau des dernières activités
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][] {
                        {"Ajout d'étudiant", "Ahmed Bensouda", "Aujourd'hui 10:30"},
                        {"Modification de cours", "Programmation Web", "Aujourd'hui 09:15"},
                        {"Ajout d'enseignant", "Dr. Karim Alaoui", "Hier 15:45"},
                        {"Suppression d'étudiant", "Rachid Lajouad", "Hier 14:22"},
                        {"Modification d'emploi du temps", "Groupe B", "Avant-hier 11:05"}
                },
                new String[] {"Action", "Description", "Date"}
        );

        JTable activityTable = new JTable(tableModel);
        activityTable.setRowHeight(30);
        activityTable.getTableHeader().setFont(STAT_TITLE_FONT);
        activityTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activityTable.setShowGrid(false);
        activityTable.setIntercellSpacing(new Dimension(0, 0));
        activityTable.setSelectionBackground(new Color(230, 245, 233));

        JScrollPane tableScrollPane = new JScrollPane(activityTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Dernières activités",
                1,
                0,
                STAT_TITLE_FONT
        ));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModernStatPanel(String title, String value, String icon, Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 14, 14);
                }
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(STAT_TITLE_FONT);
        titleLabel.setForeground(EMSI_GRAY);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(STAT_VALUE_FONT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(new Color(50, 50, 50));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Liste des étudiants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Récupération des étudiants
        java.util.List<com.emsi.gestionuniv.model.user.Student> students = new com.emsi.gestionuniv.service.EtudiantService().getAllStudents();
        String[] columns = {"Matricule", "Prénom", "Nom", "Email", "Filière", "Promotion", "Groupe"};
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
            public boolean isCellEditable(int row, int column) { return false; }
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
        return panel;
    }

    private JPanel createTeachersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Liste des enseignants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Récupération des enseignants
        java.util.List<com.emsi.gestionuniv.model.user.Teacher> teachers = new com.emsi.gestionuniv.service.TeacherService().getAllTeachers();
        com.emsi.gestionuniv.service.TeacherService teacherService = new com.emsi.gestionuniv.service.TeacherService();
        String[] columns = {"Matricule", "Prénom", "Nom", "Email", "Telephone", "Spécialité", "Groupes"};
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
            java.util.List<com.emsi.gestionuniv.service.TeacherService.Classe> groupes = teacherService.getClassesByTeacherId(t.getId());
            String groupesStr = groupes.stream().map(com.emsi.gestionuniv.service.TeacherService.Classe::getNom).reduce((a, b) -> a + ", " + b).orElse("");
            data[i][6] = groupesStr;
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMSI_LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Liste des cours");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);
        panel.add(titleLabel, BorderLayout.NORTH);

        java.util.List<com.emsi.gestionuniv.model.academic.cours> coursList = new com.emsi.gestionuniv.service.CoursService().getAllCours();
        String[] columns = {"Code", "Intitulé", "Filière", "Niveau", "Effectif", "Volume horaire"};
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
            public boolean isCellEditable(int row, int column) { return false; }
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
        return panel;
    }

    private JPanel createSettingsPanel() {
        return createPlaceholderPanel("Paramètres du système");
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("admin"));
    }
}