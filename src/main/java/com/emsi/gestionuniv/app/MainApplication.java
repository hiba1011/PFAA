package com.emsi.gestionuniv.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import com.emsi.gestionuniv.view.enseignant.EnseignantDashboard;
import com.emsi.gestionuniv.view.etudiant.EtudiantDashboard;
import com.emsi.gestionuniv.view.Login.LoginEtudiant;
import com.emsi.gestionuniv.view.Login.EnseignantLogin;
import com.emsi.gestionuniv.view.Login.AdminLogin;

public class MainApplication {
    // Couleurs EMSI
    private static final Color EMSI_DARK_GREEN = new Color(0, 100, 60);
    private static final Color EMSI_LIGHT_GREEN = new Color(18, 175, 105);
    private static final Color EMSI_ACCENT_GREEN = new Color(119, 221, 119);
    private static final Color EMSI_WHITE = new Color(255, 255, 255);
    private static final Color EMSI_LIGHT_GRAY = new Color(240, 245, 240);

    public static void main(String[] args) {
        // Set Look and Feel to System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("EMSI - Système de Gestion Universitaire");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Centrer la fenêtre
        frame.setIconImage(createImageIcon("/resources/emsi_icon.png", "EMSI Logo").getImage());

        // Création du panneau principal avec un layout moderne
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(EMSI_WHITE);

        // Panneau du haut pour le logo et le titre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Logo EMSI
        JLabel logoLabel = new JLabel(createImageIcon("/resources/emsi_logo.png", "EMSI Logo"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(logoLabel, BorderLayout.NORTH);

        // Titre avec style moderne
        JLabel titleLabel = new JLabel("Système de Gestion Universitaire", JLabel.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        titleLabel.setForeground(EMSI_DARK_GREEN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Veuillez sélectionner votre profil pour continuer", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 18));
        subtitleLabel.setForeground(EMSI_DARK_GREEN.darker());
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Panneau central pour les boutons
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        centerPanel.setOpaque(false);

        // Création des boutons modernes avec icônes
        JPanel adminPanel = createProfileButton("Admin", "admin_icon.png", EMSI_DARK_GREEN);
        JPanel enseignantPanel = createProfileButton("Enseignant", "teacher_icon.png", EMSI_DARK_GREEN);
        JPanel etudiantPanel = createProfileButton("Étudiant", "student_icon.png", EMSI_DARK_GREEN);

        centerPanel.add(adminPanel);
        centerPanel.add(enseignantPanel);
        centerPanel.add(etudiantPanel);

        // Panneau de pied de page
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("© " + java.time.Year.now().getValue() + " EMSI - École Marocaine des Sciences de l'Ingénieur | v2.0", JLabel.CENTER);
        statusLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        statusLabel.setForeground(EMSI_DARK_GREEN);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        footerPanel.add(statusLabel, BorderLayout.CENTER);

        // Ajouter les panneaux au panneau principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Ajouter les écouteurs d'événements aux boutons

        // Bouton Admin
        adminPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Effet visuel au clic
                    adminPanel.setBackground(EMSI_LIGHT_GREEN);
                    // Délai pour l'animation
                    Timer timer = new Timer(150, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Ouvrir la page de connexion admin
                            new AdminLogin();
                            frame.dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } catch (Exception ex) {
                    showErrorDialog(frame, "Erreur lors de l'ouverture de la page admin", ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                adminPanel.setBackground(EMSI_LIGHT_GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                adminPanel.setBackground(EMSI_LIGHT_GRAY);
            }
        });

        enseignantPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    enseignantPanel.setBackground(EMSI_LIGHT_GREEN);

                    // Appeler la méthode main d'EnseignantLogin
                    SwingUtilities.invokeLater(() -> {
                        EnseignantLogin.main(new String[0]);
                        frame.dispose();
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorDialog(frame, "Erreur lors de l'ouverture de la page enseignant", ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                enseignantPanel.setBackground(EMSI_LIGHT_GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                enseignantPanel.setBackground(EMSI_LIGHT_GRAY);
            }
        });

        // Bouton Etudiant
        etudiantPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Effet visuel au clic
                    etudiantPanel.setBackground(EMSI_LIGHT_GREEN);
                    // Délai pour l'animation
                    Timer timer = new Timer(150, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Ouvrir la page de connexion étudiant
                            new LoginEtudiant();
                            frame.dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } catch (Exception ex) {
                    showErrorDialog(frame, "Erreur lors de l'ouverture de la page étudiant", ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                etudiantPanel.setBackground(EMSI_LIGHT_GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                etudiantPanel.setBackground(EMSI_LIGHT_GRAY);
            }
        });

        // Fond avec dégradé subtil
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                // Dégradé subtil du blanc vers un vert très léger
                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_WHITE,
                        0, h, new Color(240, 255, 245)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(mainPanel);

        frame.add(backgroundPanel);

        // Afficher la fenêtre
        frame.setVisible(true);
    }

    // Méthode pour créer un bouton de profil moderne avec icône et effet au survol
    private static JPanel createProfileButton(String text, String iconName, Color textColor) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(EMSI_LIGHT_GRAY);
        buttonPanel.setBorder(new CompoundBorder(
                new LineBorder(EMSI_LIGHT_GREEN, 1, true),
                new EmptyBorder(20, 15, 20, 15)
        ));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icône
        JLabel iconLabel = new JLabel(createImageIcon("/resources/" + iconName, text));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texte
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        textLabel.setForeground(textColor);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(iconLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(textLabel);
        buttonPanel.add(Box.createVerticalGlue());

        return buttonPanel;
    }

    // Méthode pour créer une icône à partir d'un chemin d'image
    private static ImageIcon createImageIcon(String path, String description) {
        URL imgURL = MainApplication.class.getResource(path);

        // Si l'image n'est pas trouvée, on retourne une icône par défaut
        if (imgURL == null) {
            System.err.println("Image introuvable: " + path);

            // Créer un icône générique de 64x64 avec l'initiale du texte
            char initial = description.charAt(0);
            JPanel iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(EMSI_LIGHT_GREEN);
                    g.fillRect(0, 0, 64, 64);
                    g.setColor(Color.WHITE);
                    Font font = new Font("Arial", Font.BOLD, 32);
                    g.setFont(font);
                    FontMetrics metrics = g.getFontMetrics(font);
                    int x = (64 - metrics.charWidth(initial)) / 2;
                    int y = ((64 - metrics.getHeight()) / 2) + metrics.getAscent();
                    g.drawString(String.valueOf(initial), x, y);
                }
            };
            iconPanel.setSize(64, 64);

            BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            iconPanel.paint(image.getGraphics());
            return new ImageIcon(image);
        }

        return new ImageIcon(imgURL, description);
    }

    // Méthode pour afficher une boîte de dialogue d'erreur améliorée
    private static void showErrorDialog(JFrame parent, String message, Exception ex) {
        ex.printStackTrace();

        JPanel errorPanel = new JPanel(new BorderLayout(10, 10));
        errorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        errorPanel.add(iconLabel, BorderLayout.WEST);

        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel errorTitle = new JLabel(message);
        errorTitle.setFont(new Font("Arial", Font.BOLD, 14));
        messagePanel.add(errorTitle, BorderLayout.NORTH);

        JTextArea errorDetails = new JTextArea(ex.getMessage());
        errorDetails.setEditable(false);
        errorDetails.setWrapStyleWord(true);
        errorDetails.setLineWrap(true);
        errorDetails.setFont(new Font("Arial", Font.PLAIN, 12));
        errorDetails.setBackground(errorPanel.getBackground());

        messagePanel.add(errorDetails, BorderLayout.CENTER);
        errorPanel.add(messagePanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(parent, errorPanel, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}