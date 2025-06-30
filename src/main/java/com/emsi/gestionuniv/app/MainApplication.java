package com.emsi.gestionuniv.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import com.emsi.gestionuniv.view.enseignant.EnseignantDashboard;
import com.emsi.gestionuniv.view.etudiant.EtudiantDashboard;
import com.emsi.gestionuniv.view.Login.LoginEtudiant;
import com.emsi.gestionuniv.view.Login.EnseignantLogin;
import com.emsi.gestionuniv.view.Login.AdminLogin;

public class MainApplication {
    // Palette de couleurs moderne EMSI
    private static final Color EMSI_PRIMARY = new Color(0, 120, 74);
    private static final Color EMSI_SECONDARY = new Color(18, 175, 105);
    private static final Color EMSI_ACCENT = new Color(100, 255, 155);
    private static final Color EMSI_LIGHT = new Color(248, 252, 250);
    private static final Color EMSI_WHITE = new Color(255, 255, 255);
    private static final Color EMSI_GRAY = new Color(108, 117, 125);
    private static final Color EMSI_DARK = new Color(33, 37, 41);
    private static final Color EMSI_SHADOW = new Color(0, 0, 0, 30);

    // Polices modernes
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FOOTER_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public static void main(String[] args) {
        // Configuration du Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Désactiver les décorateurs par défaut pour un look plus moderne
            JFrame.setDefaultLookAndFeelDecorated(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuration de la fenêtre principale
        JFrame frame = new JFrame();
        frame.setTitle("EMSI - Système de Gestion Universitaire");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 600));

        // Création du contenu principal
        JPanel mainContent = createMainContent(frame);
        frame.add(mainContent);

        // Effet de fondu à l'ouverture
        // frame.setOpacity(0.0f); // Suppression de la transparence pour garder la barre de titre
        frame.setVisible(true);

        Timer fadeInTimer = new Timer(50, null);
        fadeInTimer.addActionListener(new ActionListener() {
            float opacity = 0.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeInTimer.stop();
                }
                // frame.setOpacity(opacity); // Suppression de la transparence pour garder la barre de titre
            }
        });
        fadeInTimer.start();
    }

    private static JPanel createMainContent(JFrame parentFrame) {
        // Panneau principal avec fond dégradé
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dégradé moderne
                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_LIGHT,
                        w, h, new Color(235, 248, 241)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, w, h);

                // Ajout de formes géométriques subtiles en arrière-plan
                drawBackgroundShapes(g2d, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // En-tête avec logo et titre
        JPanel headerPanel = createHeaderPanel();

        // Panneau central avec les boutons de profil
        JPanel centerPanel = createCenterPanel(parentFrame);

        // Pied de page
        JPanel footerPanel = createFooterPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static void drawBackgroundShapes(Graphics2D g2d, int w, int h) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
        g2d.setColor(EMSI_PRIMARY);

        // Cercles décoratifs
        g2d.fillOval(w - 200, -100, 300, 300);
        g2d.fillOval(-100, h - 200, 250, 250);

        // Formes géométriques
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(w - 150, h - 150, 100, 100);
        g2d.drawOval(50, 50, 80, 80);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private static JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Logo et titre dans un panneau central
        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setLayout(new BoxLayout(logoTitlePanel, BoxLayout.Y_AXIS));
        logoTitlePanel.setOpaque(false);

        // Logo EMSI avec effet
        JLabel logoLabel = new JLabel(createModernLogo());
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre principal avec animation au survol
        JLabel titleLabel = new JLabel("Système de Gestion Universitaire");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(EMSI_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Sous-titre avec style moderne
        JLabel subtitleLabel = new JLabel("Choisissez votre profil pour accéder à votre espace");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(EMSI_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoTitlePanel.add(logoLabel);
        logoTitlePanel.add(titleLabel);
        logoTitlePanel.add(subtitleLabel);

        headerPanel.add(logoTitlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private static JPanel createCenterPanel(JFrame parentFrame) {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 15, 0, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Création des cartes de profil modernes
        ModernProfileCard adminCard = new ModernProfileCard("Administrateur", "👤", EMSI_PRIMARY,
                "Gestion complète du système");
        ModernProfileCard enseignantCard = new ModernProfileCard("Enseignant", "🎓", EMSI_SECONDARY,
                "Espace pédagogique avancé");
        ModernProfileCard etudiantCard = new ModernProfileCard("Étudiant", "📚", new Color(52, 152, 219),
                "Portail étudiant personnalisé");

        // Ajout des événements
        addCardEventHandlers(adminCard, parentFrame, () -> new AdminLogin());
        addCardEventHandlers(enseignantCard, parentFrame, () -> {
            EnseignantLogin.main(new String[0]);
            return null;
        });
        addCardEventHandlers(etudiantCard, parentFrame, () -> new LoginEtudiant());

        gbc.gridx = 0;
        centerPanel.add(adminCard, gbc);
        gbc.gridx = 1;
        centerPanel.add(enseignantCard, gbc);
        gbc.gridx = 2;
        centerPanel.add(etudiantCard, gbc);

        return centerPanel;
    }

    private static JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel(
                String.format("© %d EMSI - École Marocaine des Sciences de l'Ingénieur | Version 3.0",
                        java.time.Year.now().getValue()),
                JLabel.CENTER
        );
        footerLabel.setFont(FOOTER_FONT);
        footerLabel.setForeground(EMSI_GRAY);

        footerPanel.add(footerLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    private static void addCardEventHandlers(ModernProfileCard card, JFrame parentFrame, java.util.concurrent.Callable<Object> action) {
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.setPressed(true);

                Timer pressTimer = new Timer(150, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            action.call();
                            parentFrame.dispose();
                        } catch (Exception ex) {
                            showModernErrorDialog(parentFrame, "Erreur de navigation", ex);
                        }
                    }
                });
                pressTimer.setRepeats(false);
                pressTimer.start();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setHovered(false);
                card.setPressed(false);
            }
        });
    }

    // Classe pour les cartes de profil modernes
    static class ModernProfileCard extends JPanel {
        private final String title;
        private final String icon;
        private final Color primaryColor;
        private final String description;
        private boolean hovered = false;
        private boolean pressed = false;
        private float animationProgress = 0.0f;
        private Timer animationTimer;

        public ModernProfileCard(String title, String icon, Color primaryColor, String description) {
            this.title = title;
            this.icon = icon;
            this.primaryColor = primaryColor;
            this.description = description;

            setPreferredSize(new Dimension(280, 200));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(false);

            animationTimer = new Timer(16, e -> {
                if (hovered && animationProgress < 1.0f) {
                    animationProgress += 0.1f;
                } else if (!hovered && animationProgress > 0.0f) {
                    animationProgress -= 0.1f;
                }
                animationProgress = Math.max(0.0f, Math.min(1.0f, animationProgress));
                repaint();
            });
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Effet d'ombre portée
            if (hovered || pressed) {
                g2d.setColor(new Color(0, 0, 0, (int)(50 * animationProgress)));
                g2d.fillRoundRect(5, 5, w - 5, h - 5, 20, 20);
            }

            // Fond de la carte avec dégradé
            Color bgStart = pressed ? primaryColor.brighter() : EMSI_WHITE;
            Color bgEnd = pressed ? primaryColor : new Color(248, 249, 250);

            GradientPaint cardGradient = new GradientPaint(0, 0, bgStart, 0, h, bgEnd);
            g2d.setPaint(cardGradient);
            g2d.fillRoundRect(0, 0, w - 5, h - 5, 20, 20);

            // Bordure colorée
            g2d.setColor(primaryColor);
            g2d.setStroke(new BasicStroke(2 + animationProgress * 2));
            g2d.drawRoundRect(1, 1, w - 7, h - 7, 20, 20);

            // Icône
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            FontMetrics iconMetrics = g2d.getFontMetrics();
            int iconX = (w - iconMetrics.stringWidth(icon)) / 2;
            int iconY = 60;
            g2d.setColor(primaryColor);
            g2d.drawString(icon, iconX, iconY);

            // Titre
            g2d.setFont(BUTTON_FONT);
            g2d.setColor(pressed ? EMSI_WHITE : EMSI_DARK);
            FontMetrics titleMetrics = g2d.getFontMetrics();
            int titleX = (w - titleMetrics.stringWidth(title)) / 2;
            int titleY = iconY + 40;
            g2d.drawString(title, titleX, titleY);

            // Description
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.setColor(pressed ? new Color(255, 255, 255, 200) : EMSI_GRAY);
            FontMetrics descMetrics = g2d.getFontMetrics();
            int descX = (w - descMetrics.stringWidth(description)) / 2;
            int descY = titleY + 25;
            g2d.drawString(description, descX, descY);

            // Effet de brillance au survol
            if (hovered) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * animationProgress));
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, w - 5, h - 5, 20, 20);
            }
        }

        public void setHovered(boolean hovered) {
            this.hovered = hovered;
        }

        public void setPressed(boolean pressed) {
            this.pressed = pressed;
            repaint();
        }
    }

    private static ImageIcon createModernLogo() {
        BufferedImage logo = new BufferedImage(120, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Création d'un logo moderne EMSI
        g2d.setColor(EMSI_PRIMARY);
        g2d.fillRoundRect(10, 10, 100, 60, 15, 15);

        g2d.setColor(EMSI_WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "EMSI";
        int textX = (120 - fm.stringWidth(text)) / 2;
        int textY = (80 + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);

        g2d.dispose();
        return new ImageIcon(logo);
    }

    private static void showModernErrorDialog(JFrame parent, String message, Exception ex) {
        JDialog errorDialog = new JDialog(parent, "Erreur", true);
        errorDialog.setSize(400, 200);
        errorDialog.setLocationRelativeTo(parent);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(EMSI_WHITE);

        // Icône d'erreur moderne
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Message d'erreur
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(message);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(EMSI_DARK);

        JTextArea detailsArea = new JTextArea(ex.getMessage());
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setForeground(EMSI_GRAY);
        detailsArea.setOpaque(false);
        detailsArea.setEditable(false);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setLineWrap(true);

        messagePanel.add(titleLabel, BorderLayout.NORTH);
        messagePanel.add(detailsArea, BorderLayout.CENTER);

        // Bouton OK moderne
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(EMSI_PRIMARY);
        okButton.setForeground(EMSI_WHITE);
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.add(contentPanel);
        errorDialog.setVisible(true);
    }
}