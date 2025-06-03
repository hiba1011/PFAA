package com.emsi.gestionuniv.view.etudiant;

import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.app.MainApplication;
import com.emsi.gestionuniv.service.EtudiantService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;

public class EtudiantDashboard {
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    public static final Color EMSI_LIGHT_GREEN = new Color(225, 245, 225);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color WHITE = new Color(255, 255, 255);

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
            frame.dispose();
            if (previousFrame != null) {
                previousFrame.setVisible(true);
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
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.addTab("Tableau de bord", createDashboardTab());
        tabbedPane.addTab("Informations", new JLabel("Informations détaillées de l'étudiant"));
        tabbedPane.addTab("Notes", new JLabel("Tableau des notes"));
        tabbedPane.addTab("Emploi du temps", new JLabel("Emploi du temps"));

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
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

        JLabel info = new JLabel("Consultez vos informations, notes et emploi du temps depuis cet espace.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        info.setForeground(EMSI_GRAY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        dash.add(welcome);
        dash.add(Box.createVerticalStrut(12));
        dash.add(info);
        dash.add(Box.createVerticalGlue());
        return dash;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Student etudiant = new Student();
            new EtudiantDashboard(etudiant, null).setVisible(true); // null si pas de fenêtre précédente
        });
    }
}