package com.emsi.gestionuniv.view.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import com.emsi.gestionuniv.app.MainApplication;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private String username;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public AdminDashboard(String username) {
        // Vérifier que c'est bien l'admin
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
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurer le panneau principal avec BorderLayout
        contentPanel = new JPanel(new BorderLayout());

        // Créer la barre supérieure
        JPanel topPanel = createTopPanel();
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Créer le menu latéral
        menuPanel = createMenuPanel();
        contentPanel.add(menuPanel, BorderLayout.WEST);

        // Créer le panneau principal avec CardLayout pour les différentes vues
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Ajouter les différentes vues au panneau de cartes
        cardPanel.add(createDashboardPanel(), "dashboard");
        cardPanel.add(createStudentsPanel(), "students");
        cardPanel.add(createTeachersPanel(), "teachers");
        cardPanel.add(createCoursesPanel(), "courses");
        cardPanel.add(createSettingsPanel(), "settings");

        contentPanel.add(cardPanel, BorderLayout.CENTER);

        // Ajouter le panneau principal à la fenêtre
        add(contentPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(183, 28, 28));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Système de Gestion Universitaire - Administration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Connecté: " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(33, 33, 33));
        panel.setPreferredSize(new Dimension(200, getHeight()));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Menu items
        String[] menuItems = {
                "Tableau de bord", "Étudiants", "Enseignants", "Cours", "Paramètres"
        };
        String[] menuCards = {
                "dashboard", "students", "teachers", "courses", "settings"
        };

        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItemPanel = new JPanel(new BorderLayout());
            menuItemPanel.setBackground(new Color(33, 33, 33));
            menuItemPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
            menuItemPanel.setMaximumSize(new Dimension(200, 50));

            JLabel menuLabel = new JLabel(menuItems[i]);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            menuLabel.setForeground(Color.WHITE);

            final String cardName = menuCards[i];

            menuItemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuItemPanel.setBackground(new Color(66, 66, 66));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    menuItemPanel.setBackground(new Color(33, 33, 33));
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(cardPanel, cardName);
                }
            });

            menuItemPanel.add(menuLabel, BorderLayout.CENTER);
            panel.add(menuItemPanel);
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Bienvenue dans le tableau de bord administrateur");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        // Cartes de statistiques
        statsPanel.add(createStatCard("Étudiants", "187", new Color(33, 150, 243)));
        statsPanel.add(createStatCard("Enseignants", "42", new Color(0, 150, 136)));
        statsPanel.add(createStatCard("Cours", "56", new Color(255, 152, 0)));

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
        activityTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        activityTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane tableScrollPane = new JScrollPane(activityTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Dernières activités",
                1,
                0,
                new Font("Arial", Font.BOLD, 16)
        ));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestion des étudiants");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tableau des étudiants
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][] {
                        {"E001", "Mohamed Alami", "mohamed.alami@email.com", "Informatique", "2ème année"},
                        {"E002", "Fatima Zahra", "fatima.zahra@email.com", "Génie Civil", "3ème année"},
                        {"E003", "Karim Berrada", "karim.berrada@email.com", "Mécanique", "1ère année"},
                        {"E004", "Laila Bennani", "laila.bennani@email.com", "Informatique", "3ème année"},
                        {"E005", "Youssef Tazi", "youssef.tazi@email.com", "Électronique", "2ème année"}
                },
                new String[] {"ID", "Nom complet", "Email", "Filière", "Niveau"}
        );

        JTable studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTeachersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestion des enseignants");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tableau des enseignants
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][] {
                        {"P001", "Dr. Hassan Amrani", "hassan.amrani@email.com", "Mathématiques", "Professeur"},
                        {"P002", "Pr. Samira Khaldi", "samira.khaldi@email.com", "Physique", "Maître de conférences"},
                        {"P003", "Dr. Omar Benali", "omar.benali@email.com", "Informatique", "Professeur associé"},
                        {"P004", "Dr. Nadia Lahlou", "nadia.lahlou@email.com", "Langues", "Professeur assistante"},
                        {"P005", "Pr. Ahmed Sadiki", "ahmed.sadiki@email.com", "Électronique", "Professeur"}
                },
                new String[] {"ID", "Nom complet", "Email", "Département", "Statut"}
        );

        JTable teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(30);
        teacherTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        teacherTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane tableScrollPane = new JScrollPane(teacherTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestion des cours");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tableau des cours
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][] {
                        {"C001", "Programmation Java", "Dr. Omar Benali", "Informatique", "48h"},
                        {"C002", "Analyse Mathématique", "Dr. Hassan Amrani", "Mathématiques", "36h"},
                        {"C003", "Électronique Analogique", "Pr. Ahmed Sadiki", "Électronique", "42h"},
                        {"C004", "Anglais Technique", "Dr. Nadia Lahlou", "Langues", "24h"},
                        {"C005", "Mécanique des Fluides", "Pr. Samira Khaldi", "Physique", "36h"}
                },
                new String[] {"Code", "Intitulé", "Enseignant", "Département", "Volume horaire"}
        );

        JTable courseTable = new JTable(tableModel);
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        courseTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane tableScrollPane = new JScrollPane(courseTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Paramètres du système");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);

        // Paramètres de l'application
        JLabel generalSettingsLabel = new JLabel("Paramètres généraux");
        generalSettingsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel yearLabel = new JLabel("Année académique:");
        JLabel themeLabel = new JLabel("Thème de l'interface:");
        JLabel languageLabel = new JLabel("Langue:");

        JComboBox<String> yearCombo = new JComboBox<>(new String[]{"2024-2025", "2025-2026", "2026-2027"});
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Sombre", "Clair", "Système"});
        JComboBox<String> languageCombo = new JComboBox<>(new String[]{"Français", "Anglais", "Arabe"});

        // Paramètres de base de données
        JLabel dbSettingsLabel = new JLabel("Paramètres de la base de données");
        dbSettingsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel dbHostLabel = new JLabel("Hôte:");
        JLabel dbNameLabel = new JLabel("Nom de la base:");
        JLabel dbUserLabel = new JLabel("Utilisateur:");

        JTextField dbHostField = new JTextField("localhost");
        JTextField dbNameField = new JTextField("gestion_univ_db");
        JTextField dbUserField = new JTextField("admin");

        // Ajout des composants au formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(generalSettingsLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(yearCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(themeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(themeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(languageLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(languageCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)), gbc);

        gbc.gridy = 5;
        formPanel.add(dbSettingsLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6;
        formPanel.add(dbHostLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dbHostField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(dbNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dbNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(dbUserLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dbUserField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Enregistrer");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(33, 150, 243));
        saveButton.setForeground(Color.WHITE);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Pour tester cette classe individuellement
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminDashboard("admin");
            }
        });
    }
}