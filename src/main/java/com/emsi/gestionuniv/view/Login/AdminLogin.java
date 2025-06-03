package com.emsi.gestionuniv.view.Login;

import com.emsi.gestionuniv.view.admin.AdminDashboard;
import com.emsi.gestionuniv.app.MainApplication;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class AdminLogin extends JFrame {
    private JPasswordField passwordField;
    private JButton loginButton;
    private static final String ADMIN_PASSWORD = "admin123";

    // Couleurs et polices modernes
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    private static final Color FOCUS_COLOR = new Color(0, 148, 68, 60);
    private static final Color ACCENT_COLOR = new Color(183, 123, 91);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font INPUT_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);

    public AdminLogin() {
        setTitle("EMSI - Connexion Administrateur");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createEMSILogoImage());
        setResizable(false);

        // Panel principal avec effet d'ombre et fond arrondi
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Ombre portée
                for (int i = 8; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 10 + i * 2));
                    g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 32, 32);
                }
                // Fond blanc arrondi
                g2d.setColor(BACKGROUND_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainPanel.setOpaque(false);

        // Panel gauche décoratif avec dégradé et logo
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2d.dispose();
            }
        };
        leftPanel.setPreferredSize(new Dimension(320, 480));
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BorderLayout());

        // --- Flèche retour moderne en haut à gauche du leftPanel ---
        JPanel arrowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arrowPanel.setOpaque(false);
        JButton backArrow = new JButton("\u2190"); // Unicode flèche gauche
        backArrow.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Plus petite
        backArrow.setForeground(Color.WHITE); // Blanche pour contraste sur vert
        backArrow.setBackground(new Color(0,0,0,0));
        backArrow.setFocusPainted(false);
        backArrow.setBorderPainted(false);
        backArrow.setContentAreaFilled(false);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setToolTipText("Retour à l'accueil");
        backArrow.addActionListener(e -> {
            dispose(); // Ferme la fenêtre de login admin
            MainApplication.main(new String[0]); // Ouvre MainApplication
        });
        // Effet de survol pour la flèche
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
        arrowPanel.add(backArrow);

        // Panel pour le contenu centré (logo, texte, slogan)
        JPanel centerLeftPanel = new JPanel();
        centerLeftPanel.setOpaque(false);
        centerLeftPanel.setLayout(new BoxLayout(centerLeftPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel(new ImageIcon(createEMSILogoImage(90)));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel emsiLabel = new JLabel("EMSI");
        emsiLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        emsiLabel.setForeground(Color.WHITE);
        emsiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        centerLeftPanel.add(Box.createVerticalGlue());
        centerLeftPanel.add(logoLabel);
        centerLeftPanel.add(Box.createVerticalStrut(10));
        centerLeftPanel.add(emsiLabel);
        centerLeftPanel.add(Box.createVerticalStrut(10));
        centerLeftPanel.add(Box.createVerticalGlue());

        leftPanel.add(arrowPanel, BorderLayout.NORTH);
        leftPanel.add(centerLeftPanel, BorderLayout.CENTER);

        // Panel droit (formulaire)
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Connexion Administrateur");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_GREEN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Veuillez saisir votre mot de passe pour accéder au tableau de bord.");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(80, 80, 80));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Mot de passe administrateur");
        passwordLabel.setFont(INPUT_LABEL_FONT);
        passwordLabel.setForeground(EMSI_DARK_GREEN);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = createModernPasswordField();
        passwordField.setMaximumSize(new Dimension(350, 48));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = createModernLoginButton("SE CONNECTER");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(350, 48));
        loginButton.setPreferredSize(new Dimension(350, 48));
        loginButton.addActionListener(e -> attemptLogin());

        // Ajout des composants au panel droit
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(subtitleLabel);
        rightPanel.add(Box.createVerticalStrut(32));
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(28));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createVerticalGlue());

        // Ajout des panels au mainPanel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setSize(800, 480);
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> passwordField.requestFocusInWindow());
        setVisible(true);
    }

    private void attemptLogin() {
        String enteredPassword = new String(passwordField.getPassword());
        if (enteredPassword.isEmpty()) {
            showErrorDialog("Veuillez saisir votre mot de passe");
            return;
        }
        if (ADMIN_PASSWORD.equals(enteredPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Connexion réussie!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new AdminDashboard("admin");
        } else {
            showErrorDialog("Mot de passe incorrect");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{}, null
        );
        JDialog dialog = optionPane.createDialog(this, "Erreur");
        dialog.setVisible(true);
    }

    private static Image createEMSILogoImage() {
        return createEMSILogoImage(64);
    }

    private static Image createEMSILogoImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(EMSI_GREEN);
        g2d.fillOval(0, 0, size, size);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics fm = g2d.getFontMetrics();
        String e = "E";
        int x = (size - fm.stringWidth(e)) / 2;
        int y = (size + fm.getAscent()) / 2 - 6;
        g2d.drawString(e, x, y);
        g2d.dispose();
        return img;
    }

    private static JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(EMSI_LIGHT_GRAY);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    if (hasFocus()) {
                        g2d.setColor(FOCUS_COLOR);
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 16, 16);
                    }
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setOpaque(false);
        field.setForeground(new Color(40,40,40));
        field.setCaretColor(EMSI_GREEN);
        field.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        return field;
    }

    private static JButton createModernLoginButton(String text) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, EMSI_DARK_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                } else if (hovering) {
                    gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), EMSI_DARK_GREEN);
                } else {
                    gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), getHeight(), new Color(0, 130, 60));
                }
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                if (!getModel().isPressed() && !hovering) {
                    g2d.setPaint(new GradientPaint(
                            0, 0, new Color(255, 255, 255, 60),
                            0, getHeight()/2, new Color(255, 255, 255, 0)
                    ));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 16, 16);
                }
                g2d.setFont(BUTTON_FONT);
                FontMetrics m = g2d.getFontMetrics();
                int x = (getWidth() - m.stringWidth(getText())) / 2;
                int y = ((getHeight() - m.getHeight()) / 2) + m.getAscent();
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawString(getText(), x+1, y+1);
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AdminLogin();
        });
    }
}