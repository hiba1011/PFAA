package com.emsi.gestionuniv.view.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.view.etudiant.EtudiantDashboard;

public class LoginEtudiant extends JFrame {
    // Couleurs du thème EMSI
    private static final Color EMSI_GREEN = new Color(0, 130, 65);
    private static final Color EMSI_DARK_GREEN = new Color(0, 100, 50);
    private static final Color EMSI_LIGHT_GREEN = new Color(225, 245, 225);
    private static final Color EMSI_ACCENT = new Color(46, 180, 100);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color BG_COLOR = new Color(245, 250, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    private JTextField matriculeField;
    private JPasswordField passwordField;

    public LoginEtudiant() {
        setTitle("EMSI - Connexion Étudiant");
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(createEmsiLogo(32, 32));

        // Panel principal avec fond dégradé
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Création du header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Création du panel central avec le formulaire
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Création du footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, EMSI_GREEN, getWidth(), 0, EMSI_DARK_GREEN);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Logo EMSI
        JLabel logoLabel = new JLabel(new ImageIcon(createEmsiLogo(40, 40)));
        logoLabel.setText("EMSI");
        logoLabel.setForeground(WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // Titre
        JLabel titleLabel = new JLabel("Système de Gestion Universitaire", SwingConstants.CENTER);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                        25, 25, getWidth() - 50, getHeight() - 50, 20, 20);
                g2d.fill(roundedRectangle);

                // Bordure légère
                g2d.setColor(new Color(230, 230, 230));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(roundedRectangle);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(null);

        // Titre du formulaire
        JLabel formTitle = new JLabel("Connexion à votre espace étudiant", SwingConstants.CENTER);
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(EMSI_GREEN);
        formTitle.setBounds(50, 50, 450, 30);
        formPanel.add(formTitle);

        // Sous-titre
        JLabel subtitle = new JLabel("Entrez vos identifiants pour accéder à votre espace", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBounds(50, 85, 450, 20);
        formPanel.add(subtitle);

        // Label Matricule
        JLabel matriculeLabel = new JLabel("Matricule");
        matriculeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        matriculeLabel.setForeground(TEXT_COLOR);
        matriculeLabel.setBounds(100, 130, 100, 20);
        formPanel.add(matriculeLabel);

        // Champ Matricule
        matriculeField = new JTextField();
        matriculeField.setBounds(100, 155, 350, 50);
        styliserChampTexte(matriculeField, "Entrez votre matricule");
        formPanel.add(matriculeField);

        // Label Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setBounds(100, 220, 150, 20);
        formPanel.add(passwordLabel);

        // Champ Mot de passe
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 245, 350, 50);
        styliserChampTexte(passwordField, "••••••••");

        // Action : Touche "Entrée" déclenche la connexion
        passwordField.addActionListener(e -> handleLogin());
        formPanel.add(passwordField);

        // Bouton de connexion avec style personnalisé
        JButton loginButton = createModernLoginButton("SE CONNECTER");
        loginButton.setBounds(100, 320, 350, 50);
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton);

        // Lien mot de passe oublié
        JLabel forgotPassword = new JLabel("Mot de passe oublié ?", SwingConstants.CENTER);
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPassword.setForeground(EMSI_GREEN);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.setBounds(100, 390, 350, 20);
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginEtudiant.this,
                        "Veuillez contacter l'administration pour réinitialiser votre mot de passe.",
                        "Mot de passe oublié",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPassword.setText("<html><u>Mot de passe oublié ?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPassword.setText("Mot de passe oublié ?");
            }
        });
        formPanel.add(forgotPassword);

        return formPanel;
    }

    private void handleLogin() {
        // Récupération des valeurs des champs
        String matricule = matriculeField.getText().trim();
        String pwd = new String(passwordField.getPassword());

        // Vérification que les champs ne sont pas vides
        if (matricule.isEmpty() || pwd.isEmpty() ||
                matricule.equals("Entrez votre matricule") || pwd.equals("••••••••")) {
            showErrorDialog("Veuillez remplir tous les champs");
            return;
        }

        // Authentification de l'étudiant
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Student student = EtudiantService.findStudentByMatricule(matricule, pwd);

            if (student != null) {
                // Authentification réussie
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    new EtudiantDashboard(student).setVisible(true);
                });
            } else {
                showErrorDialog("Matricule ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showErrorDialog("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel copyrightLabel = new JLabel("© 2025 École Marocaine des Sciences de l'Ingénieur - Tous droits réservés");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(copyrightLabel);

        return footerPanel;
    }

    private void styliserChampTexte(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
                field.setForeground(TEXT_COLOR);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(EMSI_GREEN, 2, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                field.setBackground(WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                field.setBackground(new Color(245, 245, 245));
            }
        });
    }

    private JButton createModernLoginButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(EMSI_DARK_GREEN);
                } else if (getModel().isRollover()) {
                    g2.setColor(EMSI_ACCENT);
                } else {
                    g2.setColor(EMSI_GREEN);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);

        return button;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginEtudiant());
    }
}