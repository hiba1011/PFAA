package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.service.EtudiantService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Dialog pour ajouter ou modifier un étudiant
 */
public class StudentFormDialog extends JDialog {
    
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
    
    private EtudiantService etudiantService;
    private Student student;
    private boolean confirmed = false;
    
    // Form fields
    private JTextField matriculeField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> filiereComboBox;
    private JComboBox<String> promotionComboBox;
    private JComboBox<String> groupeComboBox;
    private JTextField photoField;
    private JButton choosePhotoButton;
    private String selectedPhotoPath = "";
    
    public StudentFormDialog(JDialog parent, Student student) {
        super(parent, student == null ? "Ajouter un Étudiant" : "Modifier l'Étudiant", true);
        this.etudiantService = new EtudiantService();
        this.student = student;
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
        if (student != null) {
            loadStudentData();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
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
        
        JLabel titleLabel = new JLabel(student == null ? "Ajouter un Étudiant" : "Modifier l'Étudiant");
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Matricule
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Matricule *:"), gbc);
        gbc.gridx = 1;
        matriculeField = createTextField();
        panel.add(matriculeField, gbc);
        
        // Nom
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Nom *:"), gbc);
        gbc.gridx = 1;
        nomField = createTextField();
        panel.add(nomField, gbc);
        
        // Prénom
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Prénom *:"), gbc);
        gbc.gridx = 1;
        prenomField = createTextField();
        panel.add(prenomField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Email *:"), gbc);
        gbc.gridx = 1;
        emailField = createTextField();
        panel.add(emailField, gbc);
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Mot de passe *:"), gbc);
        gbc.gridx = 1;
        passwordField = createPasswordField();
        panel.add(passwordField, gbc);
        
        // Filière
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Filière *:"), gbc);
        gbc.gridx = 1;
        filiereComboBox = createComboBox(new String[]{"AP","IIR", "GC", "IFA", "IAII"});
        panel.add(filiereComboBox, gbc);
        
        // Promotion
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(createLabel("Promotion *:"), gbc);
        gbc.gridx = 1;
        // Générer la liste des années (ex: 2025 à 2000)
        int currentYear = java.time.Year.now().getValue();
        java.util.List<String> annees = new java.util.ArrayList<>();
        for (int y = currentYear + 1; y >= 2000; y--) {
            annees.add(String.valueOf(y));
        }
        promotionComboBox = createComboBox(annees.toArray(new String[0]));
        panel.add(promotionComboBox, gbc);
        
        // Groupe
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(createLabel("Groupe *:"), gbc);
        gbc.gridx = 1;
        groupeComboBox = createComboBox(new String[]{"G1", "G2", "G3", "G4", "G5", "G6","G7","G8","G9","G10","G11", "G12", "G13", "G14", "G15", "G16","G17","G18","G19","G20"});
        panel.add(groupeComboBox, gbc);
        
        // Photo
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(createLabel("Photo :"), gbc);
        gbc.gridx = 1;
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoField = createTextField();
        photoField.setEditable(false);
        choosePhotoButton = new JButton("Choisir...");
        choosePhotoButton.addActionListener(e -> choosePhoto());
        photoPanel.add(photoField, BorderLayout.CENTER);
        photoPanel.add(choosePhotoButton, BorderLayout.EAST);
        panel.add(photoPanel, gbc);
        
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
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
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
        return comboBox;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton saveButton = createStyledButton("Enregistrer", EMSI_GREEN);
        saveButton.addActionListener(e -> saveStudent());
        
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
                addMouseListener(new java.awt.event.MouseAdapter() {
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
    
    private void loadStudentData() {
        if (student != null) {
            matriculeField.setText(student.getMatricule());
            nomField.setText(student.getNom());
            prenomField.setText(student.getPrenom());
            emailField.setText(student.getEmail());
            passwordField.setText(student.getPassword());
            filiereComboBox.setSelectedItem(student.getFiliere());
            promotionComboBox.setSelectedItem(student.getPromotion());
            groupeComboBox.setSelectedItem(student.getGroupe());
            if (student.getPhoto() != null) {
                selectedPhotoPath = student.getPhoto();
                photoField.setText(selectedPhotoPath);
            }
        }
    }
    
    private void saveStudent() {
        // Validation
        if (!validateForm()) {
            return;
        }
        
        // Créer ou mettre à jour l'étudiant
        Student studentToSave = student == null ? new Student() : student;
        studentToSave.setMatricule(matriculeField.getText().trim());
        studentToSave.setNom(nomField.getText().trim());
        studentToSave.setPrenom(prenomField.getText().trim());
        studentToSave.setEmail(emailField.getText().trim());
        studentToSave.setPassword(new String(passwordField.getPassword()));
        studentToSave.setFiliere((String) filiereComboBox.getSelectedItem());
        studentToSave.setPromotion((String) promotionComboBox.getSelectedItem());
        studentToSave.setGroupe((String) groupeComboBox.getSelectedItem());
        studentToSave.setPhoto(selectedPhotoPath);
        
        boolean success = false;
        if (student == null) {
            // Ajouter un nouvel étudiant
            success = etudiantService.addStudent(studentToSave);
        } else {
            // Mettre à jour l'étudiant existant
            success = etudiantService.updateStudent(studentToSave);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                student == null ? "Étudiant ajouté avec succès" : "Étudiant modifié avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            confirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement de l'étudiant",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        // Vérifier les champs obligatoires
        if (matriculeField.getText().trim().isEmpty()) {
            showError("Le matricule est obligatoire");
            matriculeField.requestFocus();
            return false;
        }
        
        if (nomField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            nomField.requestFocus();
            return false;
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            prenomField.requestFocus();
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
        
        // Vérifier le format de l'email
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Format d'email invalide");
            emailField.requestFocus();
            return false;
        }
        
        // Vérifier si le matricule existe déjà (sauf pour la modification)
        if (student == null && etudiantService.matriculeExists(matriculeField.getText().trim())) {
            showError("Ce matricule existe déjà");
            matriculeField.requestFocus();
            return false;
        }
        
        // Vérifier si l'email existe déjà (sauf pour la modification)
        if (student == null && etudiantService.emailExists(email)) {
            showError("Cet email existe déjà");
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
    
    private void choosePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choisir une photo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPhotoPath = fileChooser.getSelectedFile().getAbsolutePath();
            photoField.setText(selectedPhotoPath);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
} 