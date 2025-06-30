package com.emsi.gestionuniv.view.etudiant;

import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.app.MainApplication;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.model.planning.EmploiDuTempsPdf;
import com.emsi.gestionuniv.model.planning.Emploi_de_temps;
import com.emsi.gestionuniv.service.ConseilDisciplinaireService;
import com.emsi.gestionuniv.service.EmploiDuTempsPdfService;
import com.emsi.gestionuniv.model.academic.Note;
import com.emsi.gestionuniv.service.NoteService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.emsi.gestionuniv.model.academic.ConseilDisciplinaire;
import com.emsi.gestionuniv.model.academic.Message;
import com.emsi.gestionuniv.service.MessageService;
import java.text.SimpleDateFormat;

public class EtudiantDashboard {
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    public static final Color EMSI_LIGHT_GREEN = new Color(225, 245, 225);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color WHITE = new Color(255, 255, 255);
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JLabel photoLabel;
    private Student etudiant;
    private JFrame frame;
    private JFrame previousFrame;

    public EtudiantDashboard(Student etudiant, JFrame previousFrame) {
        this.etudiant = etudiant;
        this.previousFrame = previousFrame;
        initializeUI();
    }

    private void initializeUI() {
        setupLookAndFeel();
        createMainWindow();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", EMSI_GREEN);
            UIManager.put("Button.foreground", WHITE);
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TabbedPane.selected", EMSI_LIGHT_GREEN);
        } catch (Exception e) {
            System.err.println("Erreur look and feel: " + e.getMessage());
        }
    }

    private void createMainWindow() {
        frame = new JFrame("EMSI - Espace Étudiant");
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(createEmsiLogo(32, 32));

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                for (int i = 8; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 6 + i * 2));
                    g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 28, 28);
                }
                g2d.dispose();
            }
        };
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        

        createHeader(mainPanel);
        createSidebar(mainPanel);
        createContent(mainPanel);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void createHeader(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), 0, EMSI_DARK_GREEN);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-30, -20, 120, 120);
                g2d.fillOval(getWidth() - 80, getHeight() - 40, 70, 70);
            }
        };
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 80));
        headerPanel.setBorder(new EmptyBorder(10, 25, 10, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        JButton backArrow = new JButton("\u2190");
        backArrow.setFont(new Font("Segoe UI", Font.BOLD, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setBackground(new Color(0, 0, 0, 0));
        backArrow.setFocusPainted(false);
        backArrow.setBorderPainted(false);
        backArrow.setContentAreaFilled(false);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setToolTipText("Retour à la page précédente");
backArrow.addActionListener(e -> {
    if (tabbedPane != null) {
        tabbedPane.setSelectedIndex(0); // Retourne au dashboard principal
    }
});
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backArrow.setForeground(new Color(220, 255, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backArrow.setForeground(Color.WHITE);
            }
        });
        leftPanel.add(backArrow);

        JLabel logoLabel = new JLabel(new ImageIcon(createEmsiLogo(40, 40)));
        logoLabel.setText("EMSI");
        logoLabel.setForeground(WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel titleLabel = new JLabel("Espace Étudiant", SwingConstants.CENTER);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton logoutBtn = createModernLogoutButton("Déconnexion");
        logoutBtn.addActionListener(e -> confirmLogout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(logoutBtn);

        JPanel leftHeader = new JPanel();
        leftHeader.setOpaque(false);
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.X_AXIS));
        leftHeader.add(leftPanel);
        leftHeader.add(logoLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebar(JPanel mainPanel) {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                for (int i = 0; i < 6; i++) {
                    g2d.setColor(new Color(0, 0, 0, 2 + i));
                    g2d.drawLine(getWidth() - i, 0, getWidth() - i, getHeight());
                }
                g2d.dispose();
            }
        };
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(30, 18, 30, 12));
        sidebarPanel.setOpaque(false);

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(120, 120));
        photoLabel.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 3));
        photoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updatePhoto(etudiant.getPhoto());
        photoLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                changerPhoto();
            }
        });

        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        photoPanel.setOpaque(false);
        photoPanel.add(photoLabel);

        JPanel infoPanel = createStudentInfoPanel();

        JButton modifierProfilBtn = createModernSidebarButton("Modifier le profil");
        modifierProfilBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifierProfilBtn.addActionListener(e -> showEditProfileDialog());

        sidebarPanel.add(photoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(infoPanel);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(modifierProfilBtn);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }

    private void showEditProfileDialog() {
        JTextField prenomField = new JTextField(etudiant.getPrenom());
        JTextField nomField = new JTextField(etudiant.getNom());
        JPasswordField passwordField = new JPasswordField(etudiant.getPassword());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Mot de passe:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                frame, panel, "Modifier le profil", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            etudiant.setPrenom(prenomField.getText());
            etudiant.setNom(nomField.getText());
            etudiant.setPassword(new String(passwordField.getPassword()));
            new EtudiantService().updateStudentProfile(etudiant);
            frame.dispose();
            new EtudiantDashboard(etudiant, previousFrame);
        }
    }

    private JButton createModernSidebarButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovering = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                if (hovering) {
                    g2d.setColor(EMSI_LIGHT_GREEN);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.setColor(EMSI_GREEN);
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setMaximumSize(new Dimension(180, 40));
        return btn;
    }

    private JButton createModernLogoutButton(String text) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 15));
                setPreferredSize(new Dimension(150, 42));
                setMaximumSize(new Dimension(150, 42));
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

                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, EMSI_DARK_GREEN, getWidth(), getHeight(), EMSI_GREEN);
                } else if (hovering) {
                    gradient = new GradientPaint(0, 0, EMSI_GREEN.brighter(), getWidth(), getHeight(), EMSI_DARK_GREEN);
                } else {
                    gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                }
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Icône "power" unicode
                String icon = "\u23FB"; // ⏻
                Font iconFont = new Font("Segoe UI Symbol", Font.PLAIN, 20);
                g2d.setFont(iconFont);
                int iconY = ((getHeight() - g2d.getFontMetrics().getHeight()) / 2) + g2d.getFontMetrics().getAscent();
                g2d.setColor(Color.WHITE);
                g2d.drawString(icon, 18, iconY);

                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = 44;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };
        return button;
    }

    private JPanel createStudentInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel nomLabel = new JLabel(etudiant.getPrenom() + " " + etudiant.getNom());
        nomLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel(etudiant.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel filiereLabel = new JLabel("Filière : " + etudiant.getFiliere());
        filiereLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel promoLabel = new JLabel("Promotion : " + etudiant.getPromotion());
        promoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nomLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(filiereLabel);
        infoPanel.add(promoLabel);

        return infoPanel;
    }

private void createContent(JPanel mainPanel) {
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBackground(WHITE);
    contentPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    tabbedPane = new JTabbedPane();
    tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
    tabbedPane.addTab("Tableau de bord", createDashboardTab());
    tabbedPane.addTab("Informations", createStaticInfoTab());
    tabbedPane.addTab("Notes", createNotesTab());
    tabbedPane.addTab("Absences", createAbsencesTab());
    tabbedPane.addTab("Emploi du temps", createEmploiDuTempsTab());
    tabbedPane.addTab("Messages", createMessagesTab());

    contentPanel.add(tabbedPane, BorderLayout.CENTER);
}
    private JPanel createStaticInfoTab() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel titre = new JLabel("Informations personnelles");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(EMSI_GREEN);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(0, 2, 18, 18));
        grid.setOpaque(false);

        grid.add(createInfoLabel("Nom complet :"));
        grid.add(createInfoValue(etudiant.getPrenom() + " " + etudiant.getNom()));
        grid.add(createInfoLabel("Matricule :"));
        grid.add(createInfoValue(etudiant.getMatricule()));
        grid.add(createInfoLabel("Email :"));
        grid.add(createInfoValue(etudiant.getEmail()));
        grid.add(createInfoLabel("Filière :"));
        grid.add(createInfoValue(etudiant.getFiliere()));
        grid.add(createInfoLabel("Promotion :"));
        grid.add(createInfoValue(etudiant.getPromotion()));
        grid.add(createInfoLabel("Groupe :"));
        grid.add(createInfoValue(etudiant.getGroupe()));
        grid.add(createInfoLabel("Adresse :"));
        grid.add(createInfoValue("123, Rue de l'Université, Rabat")); // Remplace par une vraie donnée si tu l'as

        panel.add(titre);
        panel.add(Box.createVerticalStrut(30));
        panel.add(grid);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(EMSI_DARK_GREEN);
        return label;
    }

    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(EMSI_GRAY);
        return label;
    }

    private JPanel createNotesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);

        JLabel titre = new JLabel("Mes notes du semestre", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(EMSI_GREEN);
        titre.setBorder(new EmptyBorder(20, 0, 20, 0));
        panel.add(titre, BorderLayout.NORTH);

        String[] columns = { "Matière", "Contrôle Continu", "Examen", "TP", "Note Finale", "Validation" };

        // Récupérer les notes de l'étudiant
        List<Note> notes = new NoteService().getNotesByEtudiantId(etudiant.getId());

        Object[][] data = new Object[notes.size()][columns.length];
        for (int i = 0; i < notes.size(); i++) {
            var n = notes.get(i);
            data[i][0] = n.getMatiere();
            data[i][1] = n.getControleContinu();
            data[i][2] = n.getExamen();
            data[i][3] = n.getTp();
            data[i][4] = n.getNoteFinale();
            data[i][5] = n.getValidation();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(EMSI_LIGHT_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 255, 200));
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(245, 255, 245));
                } else {
                    c.setBackground(Color.WHITE);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAbsencesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);

        JLabel titre = new JLabel("Mes absences", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(EMSI_GREEN);
        titre.setBorder(new EmptyBorder(20, 0, 20, 0));
        panel.add(titre, BorderLayout.NORTH);

        String[] columns = { "Date", "Cours", "Justifiée", "Justification", "Action" };
        java.util.List<com.emsi.gestionuniv.model.academic.Abscence> absences = new com.emsi.gestionuniv.service.AbscenceService().getAbsencesByEtudiantId(etudiant.getId());
        com.emsi.gestionuniv.service.CoursService coursService = new com.emsi.gestionuniv.service.CoursService();
        Object[][] data = new Object[absences.size()][columns.length];
        for (int i = 0; i < absences.size(); i++) {
            var a = absences.get(i);
            String nomCours = "";
            var cours = coursService.getCoursById(a.getCoursId());
            if (cours != null) {
                nomCours = cours.getIntitule() != null && !cours.getIntitule().isEmpty() ? cours.getIntitule() : cours.getTitre();
            }
            data[i][0] = a.getDate();
            data[i][1] = nomCours;
            data[i][2] = a.isJustifiee() ? "Oui" : "Non";
            data[i][3] = (a.getJustification() != null && a.getJustification().length > 0) ? "Voir" : "";
            data[i][4] = (!a.isJustifiee() && (a.getJustification() == null || a.getJustification().length == 0))
            ? "Ajouter justificatif" : "";
        }
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (col == 4 && "Ajouter justificatif".equals(table.getValueAt(row, col))) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sélectionnez une image justificative");
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Enregistre le chemin du justificatif dans la base
                com.emsi.gestionuniv.model.academic.Abscence abs = absences.get(row);

                // Met à jour en base (ajoute une méthode updateJustificatif dans AbscenceService)
                new com.emsi.gestionuniv.service.AbscenceService().updateJustificatif(abs.getId(), selectedFile);
                // Rafraîchis le tableau
                contentPanel.removeAll();
                contentPanel.add(createAbsencesTab(), BorderLayout.CENTER);
                contentPanel.revalidate();
                contentPanel.repaint();
                JOptionPane.showMessageDialog(frame, "Justificatif ajouté. En attente de validation par l'administration.");
            }
        }
         if (col == 3 && "Voir".equals(table.getValueAt(row, col))) {
            // Récupère le byte[] depuis le modèle d'absence
            byte[] imageBytes = absences.get(row).getJustification();
            if (imageBytes != null && imageBytes.length > 0) {
                ImageIcon icon = new ImageIcon(imageBytes);
                JLabel label = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
                JOptionPane.showMessageDialog(null, label, "Justificatif", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
});
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(EMSI_LIGHT_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 255, 200));
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(245, 255, 245));
                } else {
                    c.setBackground(Color.WHITE);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createConseilsTab() {
        com.emsi.gestionuniv.service.CoursService coursService = new com.emsi.gestionuniv.service.CoursService();
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(WHITE);

    JLabel titre = new JLabel("Conseils disciplinaires", SwingConstants.CENTER);
    titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titre.setForeground(EMSI_GREEN);
    titre.setBorder(new EmptyBorder(20, 0, 20, 0));
    panel.add(titre, BorderLayout.NORTH);

    String[] columns = { "Cours", "Type", "Date" };
    List<ConseilDisciplinaire> conseils = new ConseilDisciplinaireService().getConseilsByEtudiant(etudiant.getId());
    Object[][] data = new Object[conseils.size()][columns.length];
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // ou "dd/MM/yyyy" selon ton besoin
    for (int i = 0; i < conseils.size(); i++) {
        ConseilDisciplinaire c = conseils.get(i);
        // Affiche le nom du cours
        String nomCours = "";
        var cours = coursService.getCoursById(c.getCoursId());
        if (cours != null) {
            nomCours = cours.getIntitule() != null && !cours.getIntitule().isEmpty() ? cours.getIntitule() : cours.getTitre();
        }
        data[i][0] = nomCours;
        data[i][1] = c.getType();
        Object dateObj = c.getDate();
String dateStr;
if (dateObj instanceof java.util.Date) {
    dateStr = sdf.format((java.util.Date) dateObj);
} else if (dateObj instanceof String) {
    String s = (String) dateObj;
    if (s.contains("T")) {
        dateStr = s.split("T")[0]; // Prend la partie avant le T
    } else if (s.contains(" ")) {
        dateStr = s.split(" ")[0]; // Prend la partie avant l'espace
    } else {
        dateStr = s;
    }
} else {
    dateStr = dateObj != null ? dateObj.toString() : "";
}
data[i][2] = dateStr;
    }
    JTable table = new JTable(data, columns);
    table.setRowHeight(32);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
    table.getTableHeader().setBackground(EMSI_LIGHT_GREEN);
    table.getTableHeader().setForeground(Color.BLACK);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}

    private JPanel createDashboardTab() {
        JPanel dash = new JPanel();
        dash.setBackground(WHITE);
        dash.setLayout(new BoxLayout(dash, BoxLayout.Y_AXIS));
        dash.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel welcome = new JLabel("Bienvenue sur votre tableau de bord !");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(EMSI_GREEN);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("Accédez rapidement à vos services étudiants :");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        info.setForeground(EMSI_GRAY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        dash.add(welcome);
        dash.add(Box.createVerticalStrut(10));
        dash.add(info);
        dash.add(Box.createVerticalStrut(25));

        JPanel grid = new JPanel(new GridLayout(2, 3, 30, 30));
        grid.setOpaque(false);

        grid.add(createDashboardCard("📚", "Mes cours", "Consultez la liste de vos cours et supports."));
        grid.add(createDashboardCard("📝", "Mes absences", "Visualisez vos absences et retards."));
        grid.add(createDashboardCard("📅", "Planning examens", "Consultez le calendrier de vos examens."));
        grid.add(createDashboardCard("⚖️", "Conseils disciplinaires",
                "Consultez vos éventuels conseils disciplinaires."));
        grid.add(createDashboardCard("⭐", "Mes notes", "Consultez vos notes et résultats."));
        grid.add(createDashboardCard("👤", "Mon profil", "Gérez vos informations personnelles."));

        dash.add(grid);
        dash.add(Box.createVerticalGlue());
        return dash;
    }

private JPanel createDashboardCard(String icon, String title, String description) {
    JPanel card = new JPanel();
    card.setBackground(EMSI_LIGHT_GREEN);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
            new EmptyBorder(18, 18, 18, 18)));
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
    titleLabel.setForeground(EMSI_GREEN);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel descLabel = new JLabel("<html><div style='text-align:center;'>" + description + "</div></html>");
    descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    descLabel.setForeground(EMSI_GRAY);
    descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    card.add(iconLabel);
    card.add(Box.createVerticalStrut(8));
    card.add(titleLabel);
    card.add(Box.createVerticalStrut(5));
    card.add(descLabel);

    card.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(210, 255, 210));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            card.setBackground(EMSI_LIGHT_GREEN);
        }
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (tabbedPane == null) return;
            switch (title) {
                case "Mes cours":
                     showMesCoursPanel(); // ou l'index de l'onglet "Informations" ou crée un onglet "Cours"
                    break;
                case "Mes notes":
                    tabbedPane.setSelectedIndex(2);
                    break;
                case "Mes absences":
                    tabbedPane.setSelectedIndex(3);
                    break;
                case "Emploi du temps":
                    tabbedPane.setSelectedIndex(4);
                    break;
                case "Messages":
                    tabbedPane.setSelectedIndex(5);
                    break;
                case "Mon profil":
                    tabbedPane.setSelectedIndex(1); // ou l'index de l'onglet "Informations"
                    break;
                case "Planning examens":
                    showPlanningExamensPanel(); // si tu veux un panel spécial, sinon crée un onglet
                    break;
                case "Conseils disciplinaires":
                     showConseilsDiscPanel();
                    break;
                default:
                    showNotImplementedMessage();
            }
        }
    });

    return card;
}
private JPanel createEmploiDuTempsTab() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(WHITE);
    panel.setBorder(new EmptyBorder(40, 80, 40, 80));

    JLabel titre = new JLabel("Emploi du temps");
    titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titre.setForeground(new Color(0, 148, 68));
    titre.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titre);
    panel.add(Box.createVerticalStrut(24));

    String classe = etudiant.getGroupe();
    List<EmploiDuTempsPdf> emplois = new EmploiDuTempsPdfService().getEmploisEtudiantByClasse(classe);
    if (emplois.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucun emploi du temps PDF disponible pour votre classe.");
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(EMSI_GRAY);
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
                new EmptyBorder(8, 12, 8, 12)
            ));

            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setForeground(EMSI_GREEN);
            icon.setBorder(new EmptyBorder(0, 0, 0, 12));
            row.add(icon, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel titreLbl = new JLabel(emploi.getTitre());
            titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreLbl.setForeground(EMSI_DARK_GREEN);
            JLabel classeLbl = new JLabel("Classe : " + emploi.getCible());
            classeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            classeLbl.setForeground(EMSI_GRAY);
            infoPanel.add(titreLbl);
            infoPanel.add(classeLbl);
            row.add(infoPanel, BorderLayout.CENTER);

            JButton openBtn = new JButton("Télécharger / Ouvrir") {
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
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            openBtn.setPreferredSize(new Dimension(170, 38));
            openBtn.setMaximumSize(new Dimension(170, 38));
            openBtn.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(new File(emploi.getCheminPdf()));
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

    panel.add(Box.createVerticalGlue());
    return panel;
}

    private void changerPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                etudiant.setPhoto(selectedFile.getAbsolutePath());
                updatePhoto(selectedFile.getAbsolutePath());
                // Sauvegarde en base de données :
                new EtudiantService().updateStudentPhoto(etudiant.getId(), selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "Photo mise à jour avec succès !");
            }
        }
    }

    private void updatePhoto(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon icon;
                if (new File(path).exists()) {
                    icon = new ImageIcon(path);
                } else {
                    java.net.URL imgURL = getClass().getResource(path);
                    if (imgURL != null) {
                        icon = new ImageIcon(imgURL);
                    } else {
                        throw new Exception("Image non trouvée: " + path);
                    }
                }
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
                return;
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
            }
        }
        BufferedImage defaultImg = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImg.createGraphics();
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillOval(0, 0, 120, 120);
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillOval(30, 30, 60, 60); // tête
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(40, 80, 40, 25); // épaules
        g2d.dispose();
        photoLabel.setIcon(new ImageIcon(defaultImg));
    }

    private void confirmLogout() {
        int option = JOptionPane.showConfirmDialog(
                frame,
                "Voulez-vous vraiment vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            frame.dispose();
            MainApplication.main(new String[0]);
        }
    }

    private void showNotImplementedMessage() {
        JOptionPane.showMessageDialog(
                frame,
                "Fonctionnalité à implémenter",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
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

    /** Méthode utilitaire pour afficher ou masquer le dashboard **/
    public void setVisible(boolean visible) {
        if (frame != null) {
            frame.setVisible(visible);
        }
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         Student etudiant = new Student();
    //         new EtudiantDashboard(etudiant, null).setVisible(true); // null si pas de fenêtre précédente
    //     });
    // }

    private JPanel createMessagesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 240));

        JLabel titre = new JLabel("Messagerie privée", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(EMSI_GREEN);
        titre.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(titre, BorderLayout.NORTH);

        // Liste dynamique des enseignants
        List<com.emsi.gestionuniv.model.user.Teacher> enseignantsList = new com.emsi.gestionuniv.service.TeacherService().getAllTeachers();
        int[] enseignantsIds = enseignantsList.stream().mapToInt(com.emsi.gestionuniv.model.user.Teacher::getId).toArray();
        String[] enseignants = enseignantsList.stream().map(t -> t.getFullName()).toArray(String[]::new);
        String[] avatars = enseignantsList.stream().map(t -> "🧑‍🏫").toArray(String[]::new);

        JComboBox<String> enseignantCombo = new JComboBox<>(enseignants);
        enseignantCombo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        enseignantCombo.setBackground(Color.WHITE);
        enseignantCombo.setForeground(EMSI_GREEN);
        enseignantCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topPanel.setOpaque(false);
        JLabel avatarLabel = new JLabel(avatars[0]);
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        avatarLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        JLabel nameLabel = new JLabel(enseignants[0]);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(EMSI_GREEN);
        topPanel.add(avatarLabel);
        topPanel.add(nameLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        topPanel.add(enseignantCombo);
        topPanel.setBorder(new EmptyBorder(10, 60, 10, 60));
        panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        JTextPane messagesArea = new JTextPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 255, 245), getWidth(), getHeight(),
                        new Color(230, 245, 230));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        messagesArea.setOpaque(false);
        messagesArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messagesArea.setEditable(false);
        messagesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_LIGHT_GREEN, 2, true),
                new EmptyBorder(18, 18, 18, 18)));

        JScrollPane scrollPane = new JScrollPane(messagesArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        scrollPane.setBackground(new Color(245, 255, 245));
        panel.add(scrollPane, BorderLayout.CENTER);

        MessageService messageService = new MessageService();

        Runnable updateMessages = () -> {
            messageService.reloadMessages();
            int idx = enseignantCombo.getSelectedIndex();
            avatarLabel.setText(avatars[idx]);
            nameLabel.setText(enseignants[idx]);
            int enseignantId = enseignantsIds[idx];
            java.util.List<Message> conv = messageService.getConversation(
                etudiant.getId(), "etudiant", enseignantId, "enseignant"
            );
            StringBuilder html = new StringBuilder(
                "<html><body style='font-family:Segoe UI,sans-serif;font-size:15px;background:transparent;'>"
            );
            for (Message m : conv) {
                boolean sentByMe = m.getSenderId() == etudiant.getId() && m.getSenderType().equals("etudiant");
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
        enseignantCombo.addActionListener(e -> updateMessages.run());
        updateMessages.run();

        // Rafraîchissement automatique toutes les 2 secondes
        new javax.swing.Timer(2000, e -> updateMessages.run()).start();

        // Zone d'envoi de message stylée
        JPanel sendPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(220, 235, 220, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2d.dispose();
            }
        };
        sendPanel.setOpaque(false);
        sendPanel.setBorder(new EmptyBorder(18, 60, 18, 60));
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GREEN, 1, true),
                new EmptyBorder(10, 16, 10, 16)));
        JButton sendBtn = new JButton("Envoyer");
        sendBtn.setBackground(EMSI_GREEN);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sendBtn.setFocusPainted(false);

        sendBtn.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                int idx = enseignantCombo.getSelectedIndex();
                int enseignantId = enseignantsIds[idx];
                Message msg = new Message();
                msg.setSenderId(etudiant.getId());
                msg.setSenderType("etudiant");
                msg.setReceiverId(enseignantId);
                msg.setReceiverType("enseignant");
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

        return panel;
    }

private void showMesCoursPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(WHITE);
    panel.setBorder(new EmptyBorder(40, 80, 40, 80));

    JLabel title = new JLabel("Mes cours PDF");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(EMSI_GREEN);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(title);
    panel.add(Box.createVerticalStrut(24));

    String classe = etudiant.getGroupe();
    List<com.emsi.gestionuniv.model.academic.CoursPdf> pdfs = new com.emsi.gestionuniv.service.CoursPdfService().getCoursPdfByClasse(classe);

    if (pdfs.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucun cours PDF disponible pour votre classe.");
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(EMSI_GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyLabel);
    } else {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (com.emsi.gestionuniv.model.academic.CoursPdf pdf : pdfs) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 240, 230)),
                new EmptyBorder(8, 12, 8, 12)
            ));

            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setForeground(EMSI_GREEN);
            icon.setBorder(new EmptyBorder(0, 0, 0, 12));
            row.add(icon, BorderLayout.WEST);

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
            row.add(infoPanel, BorderLayout.CENTER);

            JButton openBtn = new JButton("Télécharger / Ouvrir") {
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
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            openBtn.setPreferredSize(new Dimension(170, 38));
            openBtn.setMaximumSize(new Dimension(170, 38));
            openBtn.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(new File(pdf.getCheminPdf()));
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

    panel.add(Box.createVerticalGlue());

    // Ajoute ou sélectionne l'onglet "Mes cours"
    int idx = -1;
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        if ("Mes cours".equals(tabbedPane.getTitleAt(i))) {
            idx = i;
            break;
        }
    }
    if (idx == -1) {
        tabbedPane.addTab("Mes cours", panel);
        idx = tabbedPane.getTabCount() - 1;
    } else {
        tabbedPane.setComponentAt(idx, panel);
    }
    tabbedPane.setSelectedIndex(idx);
}
    private void showPlanningExamensPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(WHITE);
    panel.setBorder(new EmptyBorder(40, 80, 40, 80));

    JLabel title = new JLabel("Planning des examens");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(EMSI_GREEN);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(title);
    panel.add(Box.createVerticalStrut(24));

    String classe = etudiant.getGroupe();
    java.util.List<com.emsi.gestionuniv.model.academic.PlanningExamen> plannings =
        new com.emsi.gestionuniv.service.PlanningExamenService().getPlanningsByClasse(classe);

    if (plannings.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucun planning d'examen disponible pour votre classe.");
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(EMSI_GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyLabel);
    } else {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (com.emsi.gestionuniv.model.academic.PlanningExamen planning : plannings) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 240, 230)),
                new EmptyBorder(8, 12, 8, 12)
            ));

            JLabel icon = new JLabel("\uD83D\uDCC4");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setForeground(EMSI_GREEN);
            icon.setBorder(new EmptyBorder(0, 0, 0, 12));
            row.add(icon, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel titreLbl = new JLabel(planning.getTitre());
            titreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreLbl.setForeground(EMSI_DARK_GREEN);
            JLabel classeLbl = new JLabel("Classe : " + planning.getClasse());
            classeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            classeLbl.setForeground(EMSI_GRAY);
            infoPanel.add(titreLbl);
            infoPanel.add(classeLbl);
            row.add(infoPanel, BorderLayout.CENTER);

            JButton openBtn = new JButton("Télécharger / Ouvrir") {
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
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            openBtn.setPreferredSize(new Dimension(170, 38));
            openBtn.setMaximumSize(new Dimension(170, 38));
            openBtn.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(new File(planning.getCheminPdf()));
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

    panel.add(Box.createVerticalGlue());

    // Ajoute ou sélectionne l'onglet "Planning examens"
    int idx = -1;
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        if ("Planning examens".equals(tabbedPane.getTitleAt(i))) {
            idx = i;
            break;
        }
    }
    if (idx == -1) {
        tabbedPane.addTab("Planning examens", panel);
        idx = tabbedPane.getTabCount() - 1;
    } else {
        tabbedPane.setComponentAt(idx, panel);
    }
    tabbedPane.setSelectedIndex(idx);
}
private void showConseilsDiscPanel() {
    JPanel panel = createConseilsTab(); // Utilise ta méthode existante

    // Ajoute ou sélectionne l'onglet "Conseils disciplinaires"
    int idx = -1;
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        if ("Conseils disciplinaires".equals(tabbedPane.getTitleAt(i))) {
            idx = i;
            break;
        }
    }
    if (idx == -1) {
        tabbedPane.addTab("Conseils disciplinaires", panel);
        idx = tabbedPane.getTabCount() - 1;
    } else {
        tabbedPane.setComponentAt(idx, panel);
    }
    tabbedPane.setSelectedIndex(idx);
}
}