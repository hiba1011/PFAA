package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.service.EtudiantService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TeacherFormDialog extends JDialog {
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);

    private TeacherService teacherService;
    private Teacher teacher;
    private boolean confirmed = false;
    private EtudiantService etudiantService;

    private JTextField prenomField;
    private JTextField nomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField departementField;
    private JTextField specialiteField;
    private JTextField telephoneField;
    private JList<String> groupesList;
    private DefaultListModel<String> groupesListModel;
    private Set<String> selectedGroupes;

    public TeacherFormDialog(JDialog parent, Teacher teacher) {
        super(parent, teacher == null ? "Ajouter un Enseignant" : "Modifier l'Enseignant", true);
        this.teacherService = new TeacherService();
        this.etudiantService = new EtudiantService();
        this.teacher = teacher;
        this.selectedGroupes = new HashSet<>();
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        if (teacher != null) {
            loadTeacherData();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
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
        JLabel titleLabel = new JLabel(teacher == null ? "Ajouter un Enseignant" : "Modifier l'Enseignant");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        // Prénom
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Prénom *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        prenomField = createTextField();
        panel.add(prenomField, gbc);
        // Nom
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Nom *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        nomField = createTextField();
        panel.add(nomField, gbc);
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Email *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = createTextField();
        panel.add(emailField, gbc);
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Mot de passe *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        passwordField.setFont(FIELD_FONT);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        passwordField.setEnabled(true);
        passwordField.setEditable(true);
        panel.add(passwordField, gbc);
        // Département
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Département *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        departementField = createTextField();
        panel.add(departementField, gbc);
        // Spécialité
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Spécialité *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        specialiteField = createTextField();
        panel.add(specialiteField, gbc);
        // Téléphone
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Téléphone *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        telephoneField = createTextField();
        panel.add(telephoneField, gbc);
        
        // Groupes
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Groupes affectés:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.BOTH;
        panel.add(createGroupesPanel(), gbc);
        
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(EMSI_GRAY);
        return label;
    }
    private JTextField createTextField() {
        System.out.println("Création d'un JTextField");
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setEnabled(true);
        field.setEditable(true);
        return field;
    }
    
    private JPanel createGroupesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EMSI_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        // Titre du panneau
        JLabel titleLabel = new JLabel("Sélectionnez les groupes à affecter à cet enseignant:");
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(EMSI_GRAY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panneau principal avec scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_WHITE);
        
        // Récupérer tous les groupes disponibles
        List<String> allGroupes = etudiantService.getAllGroupes();
        groupesListModel = new DefaultListModel<>();
        for (String groupe : allGroupes) {
            groupesListModel.addElement(groupe);
        }
        
        // Créer la liste avec sélection multiple
        groupesList = new JList<>(groupesListModel);
        groupesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupesList.setFont(FIELD_FONT);
        groupesList.setBackground(Color.WHITE);
        groupesList.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Rendre la liste plus moderne avec des couleurs personnalisées
        groupesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(EMSI_GREEN);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(EMSI_GRAY);
                }
                
                setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return this;
            }
        });
        
        // Ajouter un écouteur pour gérer la sélection
        groupesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedGroupes.clear();
                for (String selected : groupesList.getSelectedValuesList()) {
                    selectedGroupes.add(selected);
                }
            }
        });
        
        // Scroll pane pour la liste
        JScrollPane scrollPane = new JScrollPane(groupesList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GRAY, 1));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panneau des boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonsPanel.setBackground(BACKGROUND_WHITE);
        
        JButton selectAllButton = createSmallButton("Tout sélectionner", EMSI_GREEN);
        selectAllButton.addActionListener(e -> {
            groupesList.setSelectionInterval(0, groupesListModel.getSize() - 1);
        });
        
        JButton clearAllButton = createSmallButton("Tout désélectionner", EMSI_GRAY);
        clearAllButton.addActionListener(e -> {
            groupesList.clearSelection();
        });
        
        buttonsPanel.add(selectAllButton);
        buttonsPanel.add(clearAllButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createSmallButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) { hovering = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = hovering ? backgroundColor.darker() : backgroundColor;
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(120, 25));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        JButton saveButton = createStyledButton("Enregistrer", EMSI_GREEN);
        saveButton.addActionListener(e -> saveTeacher());
        JButton cancelButton = createStyledButton("Fermer", EMSI_GRAY);
        cancelButton.addActionListener(e -> dispose());
        panel.add(saveButton);
        panel.add(cancelButton);
        return panel;
    }
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) { hovering = false; repaint(); }
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
    private void loadTeacherData() {
        if (teacher != null) {
            prenomField.setText(teacher.getPrenom());
            nomField.setText(teacher.getNom());
            emailField.setText(teacher.getEmail());
            passwordField.setText(teacher.getPassword());
            departementField.setText(teacher.getDepartement());
            specialiteField.setText(teacher.getSpecialite());
            telephoneField.setText(teacher.getTelephone());
            
            // Charger les groupes déjà affectés à cet enseignant
            loadTeacherGroupes();
        }
    }
    
    private void loadTeacherGroupes() {
        if (teacher != null && teacher.getId() > 0) {
            List<TeacherService.Classe> teacherGroupes = teacherService.getClassesByTeacherId(teacher.getId());
            selectedGroupes.clear();
            
            // Pré-sélectionner les groupes dans la liste
            for (int i = 0; i < groupesListModel.getSize(); i++) {
                String groupe = groupesListModel.getElementAt(i);
                for (TeacherService.Classe teacherClasse : teacherGroupes) {
                    if (groupe.equals(teacherClasse.getNom())) {
                        groupesList.addSelectionInterval(i, i);
                        selectedGroupes.add(groupe);
                        break;
                    }
                }
            }
        }
    }

    private void saveTeacher() {
        if (!validateForm()) {
            return;
        }
        Teacher teacherToSave = teacher == null ? new Teacher() : teacher;
        teacherToSave.setPrenom(prenomField.getText().trim());
        teacherToSave.setNom(nomField.getText().trim());
        teacherToSave.setEmail(emailField.getText().trim());
        teacherToSave.setPassword(new String(passwordField.getPassword()));
        teacherToSave.setDepartement(departementField.getText().trim());
        teacherToSave.setSpecialite(specialiteField.getText().trim());
        teacherToSave.setTelephone(telephoneField.getText().trim());
        
        boolean success = false;
        boolean groupesSuccess = false;
        
        if (teacher == null) {
            // Ajouter un nouvel enseignant
            success = teacherService.addTeacher(teacherToSave);
            if (success) {
                // Récupérer l'ID de l'enseignant nouvellement créé
                Teacher savedTeacher = teacherService.findTeacherByEmail(teacherToSave.getEmail(), teacherToSave.getPassword());
                if (savedTeacher != null) {
                    // Ajouter les groupes sélectionnés
                    List<String> groupesToAdd = new ArrayList<>(selectedGroupes);
                    groupesSuccess = teacherService.addGroupesToTeacher(savedTeacher.getId(), groupesToAdd);
                }
            }
        } else {
            // Modifier un enseignant existant
            success = teacherService.updateTeacher(teacherToSave);
            if (success) {
                // Mettre à jour les groupes
                List<String> nouveauxGroupes = new ArrayList<>(selectedGroupes);
                groupesSuccess = teacherService.updateTeacherGroupes(teacher.getId(), nouveauxGroupes);
            }
        }
        
        if (success) {
            String message = teacher == null ? "Enseignant ajouté avec succès" : "Enseignant modifié avec succès";
            if (!groupesSuccess) {
                message += "\nAttention : Erreur lors de l'affectation des groupes";
            }
            JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
            confirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement de l'enseignant",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean validateForm() {
        if (prenomField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            prenomField.requestFocus();
            return false;
        }
        if (nomField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            nomField.requestFocus();
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire");
            emailField.requestFocus();
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            showError("Le mot de passe est obligatoire");
            passwordField.requestFocus();
            return false;
        }
        if (departementField.getText().trim().isEmpty()) {
            showError("Le département est obligatoire");
            departementField.requestFocus();
            return false;
        }
        if (specialiteField.getText().trim().isEmpty()) {
            showError("La spécialité est obligatoire");
            specialiteField.requestFocus();
            return false;
        }
        if (telephoneField.getText().trim().isEmpty()) {
            showError("Le téléphone est obligatoire");
            telephoneField.requestFocus();
            return false;
        }
        // Vérifier le format de l'email
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Format d'email invalide");
            emailField.requestFocus();
            return false;
        }
        return true;
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Erreur de validation",
            JOptionPane.ERROR_MESSAGE);
    }
    public boolean isConfirmed() {
        return confirmed;
    }
} 