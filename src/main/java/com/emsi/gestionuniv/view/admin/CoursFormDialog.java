package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.model.user.Teacher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog pour ajouter ou modifier un cours
 */
public class CoursFormDialog extends JDialog {

    // Couleurs EMSI
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);

    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);

    private CoursService coursService;
    private cours cours;
    private boolean confirmed = false;

    // Form fields
    private JTextField codeField;
    private JTextField intituleField;
    private JTextArea descriptionArea;
    private JTextField creditsField;
    private JComboBox<Teacher> enseignantComboBox;
    private JComboBox<String> filiereComboBox;
    private JComboBox<String> niveauComboBox;
    private JTextField effectifField;
    private JTextField volumeHoraireField;
    private JTextField titreField;
    private java.util.List<Teacher> enseignants;
    private JLabel enseignantNomLabel;

    public CoursFormDialog(JDialog parent, cours cours) {
        super(parent, cours == null ? "Ajouter un Cours" : "Modifier le Cours", true);
        this.coursService = new CoursService();
        this.cours = cours;

        setSize(700, 850);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        if (cours != null) {
            loadCoursData();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);

        // Correction : afficher le nom complet dès l'ouverture
        if (enseignants != null && !enseignants.isEmpty()) {
            Teacher selected = (Teacher) enseignantComboBox.getSelectedItem();
            enseignantNomLabel.setText(selected != null ? selected.getFullName() : "");
        } else {
            enseignantNomLabel.setText("Aucun enseignant disponible");
        }
    }

    private JPanel createHeaderPanel() {
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
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(cours == null ? "Ajouter un Cours" : "Modifier le Cours");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(650, 0)); // Largeur moderne

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 24, 14, 24);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Code
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Code *:"), gbc);
        gbc.gridx = 1;
        codeField = createTextField();
        codeField.setPreferredSize(new Dimension(400, 36));
        panel.add(codeField, gbc);

        // Intitulé
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Intitulé *:"), gbc);
        gbc.gridx = 1;
        intituleField = createTextField();
        intituleField.setPreferredSize(new Dimension(400, 36));
        panel.add(intituleField, gbc);

        // Crédits
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Crédits *:"), gbc);
        gbc.gridx = 1;
        creditsField = createTextField();
        creditsField.setPreferredSize(new Dimension(180, 36));
        panel.add(creditsField, gbc);

        // Enseignant
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Enseignant *:"), gbc);
        gbc.gridx = 1;
        enseignants = new com.emsi.gestionuniv.service.TeacherService().getAllTeachers();
        enseignantComboBox = new JComboBox<>(enseignants.toArray(new Teacher[0]));
        enseignantComboBox.setFont(FIELD_FONT);
        enseignantComboBox.setPreferredSize(new Dimension(400, 36));
        enseignantComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher) {
                    Teacher t = (Teacher) value;
                    setText(t.getFullName());
                }
                return this;
            }
        });
        panel.add(enseignantComboBox, gbc);

        // Affichage du nom complet de l'enseignant sélectionné
        gbc.gridy = ++row;
        gbc.gridx = 0;
        panel.add(createLabel("Nom complet enseignant :"), gbc);
        gbc.gridx = 1;
        enseignantNomLabel = new JLabel();
        enseignantNomLabel.setFont(FIELD_FONT);
        panel.add(enseignantNomLabel, gbc);

        enseignantComboBox.addActionListener(e -> {
            Teacher selected = (Teacher) enseignantComboBox.getSelectedItem();
            enseignantNomLabel.setText(selected != null ? selected.getFullName() : "Aucun enseignant disponible");
        });

        // Filière
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Filière *:"), gbc);
        gbc.gridx = 1;
        filiereComboBox = createComboBox(new String[]{"IIR", "GC", "IFA", "IAII"});
        filiereComboBox.setPreferredSize(new Dimension(400, 36));
        panel.add(filiereComboBox, gbc);

        // Niveau
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Niveau *:"), gbc);
        gbc.gridx = 1;
        niveauComboBox = createComboBox(new String[]{"1ère année", "2ème année", "3ème année", "4ème année", "5ème année"});
        niveauComboBox.setPreferredSize(new Dimension(400, 36));
        panel.add(niveauComboBox, gbc);

        // Effectif
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Effectif *:"), gbc);
        gbc.gridx = 1;
        effectifField = createTextField();
        effectifField.setPreferredSize(new Dimension(180, 36));
        effectifField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        panel.add(effectifField, gbc);

        // Volume Horaire
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Volume Horaire *:"), gbc);
        gbc.gridx = 1;
        volumeHoraireField = createTextField();
        volumeHoraireField.setPreferredSize(new Dimension(180, 36));
        panel.add(volumeHoraireField, gbc);

        // Titre
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(createLabel("Titre :"), gbc);
        gbc.gridx = 1;
        titreField = createTextField();
        titreField.setPreferredSize(new Dimension(400, 36));
        panel.add(titreField, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(EMSI_GRAY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EMSI_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FIELD_FONT);
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBorder(BorderFactory.createLineBorder(EMSI_GRAY, 1));
        return comboBox;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton = createStyledButton("Enregistrer", EMSI_GREEN);
        saveButton.addActionListener(e -> saveCours());

        JButton cancelButton = createStyledButton("Annuler", EMSI_GRAY);
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = hovering ? backgroundColor.darker() : backgroundColor;
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2d.setColor(Color.WHITE);
                g2d.setFont(BUTTON_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(120, 35));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void loadCoursData() {
        if (cours != null) {
            codeField.setText(cours.getCode());
            intituleField.setText(cours.getIntitule());
            creditsField.setText(String.valueOf(cours.getCredits()));
            // Sélectionner l'enseignant dans la comboBox
            for (int i = 0; i < enseignants.size(); i++) {
                if (enseignants.get(i).getId() == cours.getEnseignantId()) {
                    enseignantComboBox.setSelectedIndex(i);
                    break;
                }
            }
            // Correction : mettre à jour le label du nom complet même si aucun enseignant n'est sélectionné
            Teacher selected = (Teacher) enseignantComboBox.getSelectedItem();
            enseignantNomLabel.setText(selected != null ? selected.getFullName() : "Aucun enseignant disponible");
            filiereComboBox.setSelectedItem(cours.getFiliere());
            niveauComboBox.setSelectedItem(cours.getNiveau());
            effectifField.setText(String.valueOf(cours.getEffectif()));
            volumeHoraireField.setText(cours.getVolumeHoraire());
            titreField.setText(cours.getTitre());
        }
    }

    private void saveCours() {
        if (!validateForm()) {
            return;
        }
        try {
            if (cours == null) {
                cours = new cours();
            }
            cours.setCode(codeField.getText().trim());
            cours.setIntitule(intituleField.getText().trim());
            cours.setCredits(Integer.parseInt(creditsField.getText().trim()));
            Teacher selectedTeacher = (Teacher) enseignantComboBox.getSelectedItem();
            cours.setEnseignantId(selectedTeacher != null ? selectedTeacher.getId() : 0);
            cours.setFiliere((String) filiereComboBox.getSelectedItem());
            cours.setNiveau((String) niveauComboBox.getSelectedItem());
            cours.setEffectif(Integer.parseInt(effectifField.getText().trim()));
            cours.setVolumeHoraire(volumeHoraireField.getText().trim());
            cours.setTitre(titreField.getText().trim());
            boolean success;
            if (cours.getId() == 0) {
                success = coursService.addCours(cours);
            } else {
                success = coursService.updateCours(cours);
            }
            if (success) {
                confirmed = true;
                dispose();
            } else {
                showError("Erreur lors de l'enregistrement du cours.");
            }
        } catch (NumberFormatException e) {
            showError("L'effectif et les crédits doivent être des nombres entiers.");
        } catch (Exception e) {
            showError("Erreur inattendue: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (codeField.getText().trim().isEmpty()) {
            showError("Le code du cours est obligatoire.");
            codeField.requestFocus();
            return false;
        }
        if (intituleField.getText().trim().isEmpty()) {
            showError("L'intitulé du cours est obligatoire.");
            intituleField.requestFocus();
            return false;
        }
        if (creditsField.getText().trim().isEmpty()) {
            showError("Le nombre de crédits est obligatoire.");
            creditsField.requestFocus();
            return false;
        }
        if (enseignantComboBox.getSelectedItem() == null) {
            showError("Veuillez sélectionner un enseignant.");
            enseignantComboBox.requestFocus();
            return false;
        }
        if (effectifField.getText().trim().isEmpty()) {
            showError("L'effectif est obligatoire.");
            effectifField.requestFocus();
            return false;
        }
        try {
            int effectif = Integer.parseInt(effectifField.getText().trim());
            if (effectif <= 0) {
                showError("L'effectif doit être un nombre positif.");
                effectifField.requestFocus();
                return false;
            }
            int credits = Integer.parseInt(creditsField.getText().trim());
            if (credits < 0) {
                showError("Le nombre de crédits doit être positif ou nul.");
                creditsField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("L'effectif et les crédits doivent être des nombres entiers valides.");
            return false;
        }
        if (volumeHoraireField.getText().trim().isEmpty()) {
            showError("Le volume horaire est obligatoire.");
            volumeHoraireField.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
} 