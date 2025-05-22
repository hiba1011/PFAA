package com.emsi.gestionuniv.view.etudiant;

import com.emsi.gestionuniv.model.user.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;

public class EtudiantDashboard {
    private static final Color EMSI_GREEN = new Color(0, 130, 65);
    private static final Color EMSI_DARK_GREEN = new Color(0, 100, 50);
    private static final Color EMSI_LIGHT_GREEN = new Color(225, 245, 225);
    private static final Color WHITE = new Color(255, 255, 255);

    private JLabel photoLabel;
    private Student etudiant;
    private JFrame frame;

    public EtudiantDashboard(Student etudiant) {
        this.etudiant = etudiant;
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
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(createEmsiLogo(32, 32));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        createHeader(mainPanel);
        createSidebar(mainPanel);
        createContent(mainPanel);

        frame.add(mainPanel);
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
            }
        };
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel(new ImageIcon(createEmsiLogo(40, 40)));
        logoLabel.setText("EMSI");
        logoLabel.setForeground(WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel titleLabel = new JLabel("Espace Étudiant", SwingConstants.CENTER);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton logoutBtn = createStyledButton("Déconnexion");
        logoutBtn.addActionListener(e -> confirmLogout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(logoutBtn);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebar(JPanel mainPanel) {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(WHITE);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
                new EmptyBorder(20, 15, 20, 15)
        ));

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

        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.setOpaque(false);
        photoPanel.add(photoLabel);

        JPanel infoPanel = createStudentInfoPanel();

        JButton modifierProfilBtn = createStyledButton("Modifier le profil");
        modifierProfilBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifierProfilBtn.addActionListener(e -> showNotImplementedMessage());

        sidebarPanel.add(photoPanel);
        sidebarPanel.add(infoPanel);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(modifierProfilBtn);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(EMSI_GREEN);
        btn.setBackground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        return btn;
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
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de bord", new JLabel("Bienvenue sur votre tableau de bord !"));
        tabbedPane.addTab("Informations", new JLabel("Informations détaillées de l'étudiant"));
        tabbedPane.addTab("Notes", new JLabel("Tableau des notes"));
        tabbedPane.addTab("Emploi du temps", new JLabel("Emploi du temps"));

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void changerPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                etudiant.setPhoto(selectedFile.getAbsolutePath());
                updatePhoto(selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "Photo mise à jour avec succès !");
            }
        }
    }

    private void updatePhoto(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
                return;
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
            }
        }
        BufferedImage defaultImg = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImg.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, 120, 120);
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
            new EtudiantDashboard(etudiant).setVisible(true);
        });
    }
}
