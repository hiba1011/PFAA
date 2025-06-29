package com.emsi.gestionuniv.view.enseignant;

import com.emsi.gestionuniv.model.academic.Abscence;
import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.service.AbscenceService;
import com.emsi.gestionuniv.service.EtudiantService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AjouterAbsenceDialog extends JDialog {
    // Couleurs du thème EMSI modernisé
    private static final Color EMSI_GREEN_PRIMARY = new Color(34, 139, 34);
    private static final Color EMSI_GREEN_LIGHT = new Color(144, 238, 144);
    private static final Color EMSI_GREEN_DARK = new Color(0, 100, 0);
    private static final Color EMSI_BACKGROUND = new Color(248, 253, 248);
    private static final Color EMSI_CARD_BG = Color.WHITE;
    private static final Color EMSI_TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color EMSI_TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color EMSI_BORDER = new Color(200, 230, 200);
    private static final Color EMSI_ERROR = new Color(220, 53, 69);
    private static final Color EMSI_SUCCESS = new Color(40, 167, 69);
    private static final Color EMSI_HOVER = new Color(76, 175, 80);

    private final cours selectedCours;
    private final Runnable onAbsenceAdded;

    // Composants de l'interface
    private JComboBox<Student> studentComboBox;
    private JTextField dateField;
    private JCheckBox justifiedCheck;
    private JTextField justificationField;
    private java.util.List<JCheckBox> studentCheckBoxes = new java.util.ArrayList<>();

    public AjouterAbsenceDialog(cours selectedCours, Runnable onAbsenceAdded) {
        this.selectedCours = selectedCours;
        this.onAbsenceAdded = onAbsenceAdded;

        // Correction : Vérification défensive de l'objet cours
        if (selectedCours == null || selectedCours.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Erreur : cours non valide (ID=" + (selectedCours != null ? selectedCours.getId() : "null") + ").", "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initializeDialog();
        createModernUI();
        setLocationRelativeTo(getParent());
    }

    private void initializeDialog() {
        setTitle("Ajouter une absence");
        setModal(true);
        setSize(480, 450);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(EMSI_BACKGROUND);
    }

    private void createModernUI() {
        setLayout(new BorderLayout());

        // Panel principal avec fond moderne
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(EMSI_BACKGROUND);

        // Header avec titre stylisé
        JPanel headerPanel = createHeaderPanel();

        // Contenu principal
        JPanel contentPanel = createContentPanel();

        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(EMSI_GREEN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Titre principal
        JLabel titleLabel = new JLabel("Nouvelle Absence");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sous-titre avec info du cours
        JLabel subtitleLabel = new JLabel("Cours: " + selectedCours.getTitre());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(EMSI_CARD_BG);
        contentPanel.setBorder(new EmptyBorder(35, 35, 35, 35));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Récupérer les étudiants
        EtudiantService etudiantService = new EtudiantService();
        System.out.println("ID du cours sélectionné : " + selectedCours.getId());
        List<Student> students = etudiantService.getStudentsByCoursId(selectedCours.getId());
        System.out.println("Nombre d'étudiants trouvés : " + students.size());

        // Champ Étudiant
        contentPanel.add(createStudentField(students));
        contentPanel.add(Box.createVerticalStrut(20));

        // Champ Date
        contentPanel.add(createDateField());
        contentPanel.add(Box.createVerticalStrut(20));

        // Checkbox justifiée
        contentPanel.add(createJustifiedSection());

        return contentPanel;
    }

    private JPanel createStudentField(List<Student> students) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EMSI_CARD_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
    
        JLabel label = createStyledLabel("Sélectionner les étudiants absents");
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    
        studentCheckBoxes.clear();
        for (Student s : students) {
            JCheckBox checkBox = new JCheckBox(s.getMatricule() + " - " + s.getNom() + " " + s.getPrenom());
            checkBox.putClientProperty("student", s);
            checkBox.setBackground(Color.WHITE);
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            studentCheckBoxes.add(checkBox);
            panel.add(checkBox);
        }
    
        if (students.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun étudiant inscrit à ce cours.");
            emptyLabel.setForeground(EMSI_TEXT_SECONDARY);
            panel.add(emptyLabel);
        }
    
        return panel;
    }

    private JPanel createDateField() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EMSI_CARD_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = createStyledLabel("Date de l'absence");

        dateField = new JTextField(Date.valueOf(LocalDate.now()).toString());
        styleTextField(dateField);

        panel.add(label, BorderLayout.NORTH);
        panel.add(dateField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createJustifiedSection() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(EMSI_CARD_BG);
        mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Checkbox
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(EMSI_CARD_BG);

        justifiedCheck = new JCheckBox("Absence justifiée");
        justifiedCheck.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        justifiedCheck.setForeground(EMSI_TEXT_PRIMARY);
        justifiedCheck.setBackground(EMSI_CARD_BG);
        justifiedCheck.setFocusPainted(false);

        // Personnaliser l'apparence du checkbox
        justifiedCheck.setIcon(createCheckboxIcon(false));
        justifiedCheck.setSelectedIcon(createCheckboxIcon(true));

        checkboxPanel.add(justifiedCheck);

        // Champ justification
        JPanel justificationPanel = new JPanel(new BorderLayout(0, 10));
        justificationPanel.setBackground(EMSI_CARD_BG);
        justificationPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel justificationLabel = createStyledLabel("Justification (optionnel)");

        justificationField = new JTextField();
        styleTextField(justificationField);
        justificationField.setEnabled(false);
        justificationField.setBackground(new Color(245, 245, 245));

        // Lier avec le checkbox
        justifiedCheck.addActionListener(e -> {
            justificationField.setEnabled(justifiedCheck.isSelected());
            if (justifiedCheck.isSelected()) {
                justificationField.setBackground(Color.WHITE);
                justificationField.requestFocus();
            } else {
                justificationField.setBackground(new Color(245, 245, 245));
                justificationField.setText("");
            }
        });

        justificationPanel.add(justificationLabel, BorderLayout.NORTH);
        justificationPanel.add(justificationField, BorderLayout.CENTER);

        mainPanel.add(checkboxPanel);
        mainPanel.add(justificationPanel);

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(EMSI_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JButton cancelButton = createStyledButton("Annuler", false);
        JButton addButton = createStyledButton("Confirmer", true);

        // Actions des boutons
        cancelButton.addActionListener(e -> dispose());
        addButton.addActionListener(this::validateAndAddAbsence);

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        return buttonPanel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        label.setForeground(EMSI_TEXT_PRIMARY);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(EMSI_TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(EMSI_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));
        field.setPreferredSize(new Dimension(0, 45));

        // Effets de focus
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.isEnabled()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(EMSI_GREEN_PRIMARY, 2, true),
                            new EmptyBorder(11, 14, 11, 14)
                    ));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(EMSI_BORDER, 1, true),
                        new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(EMSI_TEXT_PRIMARY);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(new LineBorder(EMSI_BORDER, 1, true));
        comboBox.setPreferredSize(new Dimension(0, 45));
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 42));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(EMSI_GREEN_PRIMARY);
            button.setForeground(Color.WHITE);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(EMSI_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(EMSI_GREEN_PRIMARY);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    button.setBackground(EMSI_GREEN_DARK);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    button.setBackground(EMSI_HOVER);
                }
            });
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(EMSI_TEXT_SECONDARY);
            button.setBorder(new LineBorder(EMSI_BORDER, 1, true));
            button.setBorderPainted(true);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(248, 249, 250));
                    button.setBorder(new LineBorder(EMSI_GREEN_PRIMARY, 1, true));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(Color.WHITE);
                    button.setBorder(new LineBorder(EMSI_BORDER, 1, true));
                }
            });
        }

        return button;
    }

    private Icon createCheckboxIcon(boolean selected) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (selected) {
                    g2.setColor(EMSI_GREEN_PRIMARY);
                    g2.fillRoundRect(x, y, 18, 18, 5, 5);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f));
                    // Dessiner la coche
                    g2.drawPolyline(new int[]{x+4, x+8, x+14}, new int[]{y+9, y+13, y+5}, 3);
                } else {
                    g2.setColor(EMSI_BORDER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(x, y, 17, 17, 5, 5);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(x+1, y+1, 16, 16, 4, 4);
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return 18; }

            @Override
            public int getIconHeight() { return 18; }
        };
    }

    private void validateAndAddAbsence(ActionEvent e) {
        boolean atLeastOne = false;
        String dateStr = dateField.getText().trim();
        boolean justified = justifiedCheck.isSelected();
        String justification = (justified && justificationField != null) ? justificationField.getText().trim() : null;

        java.sql.Date absenceDate;
        try {
            absenceDate = java.sql.Date.valueOf(dateStr);
        } catch (Exception ex) {
            showErrorMessage("Veuillez saisir une date valide au format AAAA-MM-JJ.");
            return;
        }

        for (JCheckBox cb : studentCheckBoxes) {
            if (cb.isSelected()) {
                Student s = (Student) cb.getClientProperty("student");
                Abscence absence = new Abscence();
                absence.setEtudiantId(s.getId());
                absence.setCoursId(selectedCours.getId());
                absence.setDate(absenceDate);
                absence.setJustifiee(justified);
                absence.setJustification(null); // ou new byte[0]

                // Correction : Vérifier si l'absence existe déjà avant d'ajouter
                AbscenceService abscenceService = new AbscenceService();
                List<Abscence> absencesExistantes = abscenceService.getAbsencesByEtudiantId(s.getId());
                boolean dejaExiste = absencesExistantes.stream().anyMatch(a ->
                    a.getCoursId() == selectedCours.getId() &&
                    a.getDate().equals(absenceDate)
                );
                if (dejaExiste) {
                    showErrorMessage("L'absence pour " + s.getNom() + " " + s.getPrenom() + " à cette date existe déjà.");
                    continue;
                }

                abscenceService.ajouterAbsence(absence);
                atLeastOne = true;
            }
        }
        if (atLeastOne) {
            showSuccessMessage("Absence(s) ajoutée(s) avec succès !");
            if (onAbsenceAdded != null) onAbsenceAdded.run();
            dispose();
        } else {
            showErrorMessage("Veuillez sélectionner au moins un étudiant.");
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}