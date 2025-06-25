package com.emsi.gestionuniv.view.enseignant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.model.academic.Note;
import com.emsi.gestionuniv.service.TeacherService;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.service.EtudiantService;
import com.emsi.gestionuniv.service.NoteService;

public class SaisieNotesDialog extends JDialog {
    // Palette de couleurs EMSI moderne
    private static final Color EMSI_PRIMARY = new Color(0, 148, 68);
    private static final Color EMSI_SECONDARY = new Color(0, 120, 55);
    private static final Color EMSI_LIGHT = new Color(230, 255, 240);
    private static final Color EMSI_ACCENT = new Color(46, 204, 113);
    // Nouveau vert moderne pour l'en-tête
    private static final Color EMSI_HEADER_GREEN = new Color(16, 185, 129); // Vert moderne élégant
    private static final Color EMSI_HEADER_HOVER = new Color(5, 150, 105); // Vert plus foncé pour effet hover
    private static final Color BACKGROUND_COLOR = new Color(248, 251, 253);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);

    // Polices modernes
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14); // Police spéciale pour l'en-tête

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> classCombo;
    private JComboBox<String> courseCombo;
    private JLabel statisticsLabel;
    private int enseignantId;

    public SaisieNotesDialog(JFrame parent, int enseignantId) {
        super(parent, "Saisie des Notes - EMSI", true);
        this.enseignantId = enseignantId;

        // Configuration de la fenêtre
        setSize(1200, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Créer l'interface
        createHeaderPanel();
        createTablePanel();
        createFooterPanel();

        // Initialiser les données
        initializeData();

        // Appliquer le style moderne
        applyModernStyling();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Titre et sous-titre
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("Gestion des Notes");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Saisissez et gérez les notes de vos étudiants");
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Panel de sélection avec design moderne
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        selectionPanel.setBackground(CARD_COLOR);

        // Classe selector
        JPanel classPanel = createSelectorPanel("Classe", classCombo = new JComboBox<>());
        selectionPanel.add(classPanel);

        // Matière selector
        JPanel coursePanel = createSelectorPanel("Matière", courseCombo = new JComboBox<>());
        selectionPanel.add(coursePanel);

        // Bouton de chargement
        JButton loadButton = createModernButton("Charger les étudiants", EMSI_PRIMARY, true);
        loadButton.addActionListener(e -> loadStudents());
        selectionPanel.add(loadButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(selectionPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createSelectorPanel(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);

        JLabel label = new JLabel(labelText);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setFont(BODY_FONT);
        comboBox.setPreferredSize(new Dimension(180, 35));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(comboBox);

        return panel;
    }

    private void createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setBorder(new EmptyBorder(10, 25, 10, 25));

        // Header avec statistiques
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(CARD_COLOR);
        tableHeaderPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel tableTitle = new JLabel("Liste des Étudiants");
        tableTitle.setFont(SUBTITLE_FONT);
        tableTitle.setForeground(TEXT_PRIMARY);

        statisticsLabel = new JLabel("Aucun étudiant chargé");
        statisticsLabel.setFont(BODY_FONT);
        statisticsLabel.setForeground(TEXT_SECONDARY);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(statisticsLabel, BorderLayout.EAST);

        // Configuration du tableau
        String[] columns = {"ID", "Nom", "Prénom", "Contrôle Continu (/20)", "Examen (/20)", "TP (/20)", "Note Finale", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 3 && col <= 5; // Seules les colonnes de notes sont éditables
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Toutes les colonnes sont traitées comme String pour éviter les erreurs de formatage
                return String.class;
            }
        };

        table = new JTable(model);
        setupModernTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableContentPanel = new JPanel(new BorderLayout());
        tableContentPanel.setBackground(CARD_COLOR);
        tableContentPanel.add(tableHeaderPanel, BorderLayout.NORTH);
        tableContentPanel.add(scrollPane, BorderLayout.CENTER);

        tablePanel.add(tableContentPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void setupModernTable() {
        table.setFont(BODY_FONT);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(238, 238, 238)); // Grille très subtile
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(EMSI_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header styling moderne avec vert EMSI
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(EMSI_HEADER_GREEN);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 48));

        // Bordure moderne pour l'en-tête
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, EMSI_HEADER_HOVER),
                new EmptyBorder(8, 12, 8, 12)
        ));

        // Renderer personnalisé pour l'en-tête avec style moderne
        header.setDefaultRenderer(new ModernHeaderRenderer());

        // Cell renderer personnalisé avec gestion des erreurs
        table.setDefaultRenderer(String.class, new ModernTableCellRenderer());

        // Largeurs des colonnes optimisées
        table.getColumnModel().getColumn(0).setPreferredWidth(100);  // ID
        table.getColumnModel().getColumn(0).setMaxWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);  // Nom
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // Prénom
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // CC
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Examen
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // TP
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Note Finale
        table.getColumnModel().getColumn(7).setPreferredWidth(120);  // Statut

        // Listener pour calcul automatique
        model.addTableModelListener(e -> {
            if (e.getFirstRow() >= 0 && e.getColumn() >= 3 && e.getColumn() <= 5) {
                SwingUtilities.invokeLater(() -> {
                    calculateFinalGrade(e.getFirstRow());
                    updateStatistics();
                });
            }
        });
    }

    // Nouveau renderer pour l'en-tête avec style moderne
    private class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Style moderne pour l'en-tête
            setBackground(EMSI_HEADER_GREEN);
            setForeground(Color.WHITE);
            setFont(HEADER_FONT);

            // Effet de dégradé subtil (simulation avec bordure)
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(255, 255, 255, 30)),
                    new EmptyBorder(12, 8, 12, 8)
            ));

            // Texte centré avec style
            setHorizontalAlignment(SwingConstants.CENTER);
            setText(value != null ? value.toString() : "");

            return c;
        }
    }

    private class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            // Convertir la valeur en String pour éviter les erreurs de formatage
            String displayValue = (value != null) ? value.toString() : "";

            Component c = super.getTableCellRendererComponent(table, displayValue, isSelected, hasFocus, row, column);

            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 249, 250));
                }
                c.setForeground(TEXT_PRIMARY);
            }

            // Alignement des colonnes numériques
            if (column >= 3 && column <= 6) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            // Coloration du statut et des erreurs
            if (column == 7 && displayValue != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Validé".equals(displayValue)) {
                    c.setForeground(SUCCESS_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Non Validé".equals(displayValue)) {
                    c.setForeground(DANGER_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Erreur".equals(displayValue)) {
                    c.setForeground(DANGER_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("En attente".equals(displayValue)) {
                    c.setForeground(TEXT_SECONDARY);
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
            }

            // Coloration des notes finales
            if (column == 6 && displayValue != null && !displayValue.isEmpty() &&
                    !displayValue.equals("Erreur saisie") && !displayValue.equals("Note invalide")) {
                try {
                    double note = Double.parseDouble(displayValue);
                    if (note >= 10) {
                        c.setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(DANGER_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } catch (NumberFormatException e) {
                    c.setForeground(DANGER_COLOR);
                }
            }

            setBorder(new EmptyBorder(8, 10, 8, 10));
            return c;
        }
    }

    private void createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(CARD_COLOR);
        footerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Panel de gauche avec actions rapides
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(CARD_COLOR);

        JButton addRowButton = createModernButton("Ajouter une ligne", EMSI_ACCENT, false);
        addRowButton.addActionListener(e -> addEmptyRow());

        JButton clearAllButton = createModernButton("Effacer tout", WARNING_COLOR, false);
        clearAllButton.addActionListener(e -> clearAllGrades());

        leftPanel.add(addRowButton);
        leftPanel.add(clearAllButton);

        // Panel de droite avec boutons principaux
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(CARD_COLOR);

        JButton previewButton = createModernButton("Aperçu", TEXT_SECONDARY, false);
        previewButton.addActionListener(e -> previewGrades());

        JButton saveButton = createModernButton("Enregistrer les Notes", SUCCESS_COLOR, true);
        saveButton.addActionListener(e -> saveNotes());

        rightPanel.add(previewButton);
        rightPanel.add(saveButton);

        footerPanel.add(leftPanel, BorderLayout.WEST);
        footerPanel.add(rightPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setPreferredSize(new Dimension(200, 40));
        } else {
            button.setPreferredSize(new Dimension(150, 35));
        }

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void initializeData() {
        // Charger les classes
        TeacherService teacherService = new TeacherService();
        List<TeacherService.Classe> classes = teacherService.getClassesByTeacherId(enseignantId);
        for (TeacherService.Classe c : classes) {
            classCombo.addItem(c.getNom());
        }

        // Charger les matières
        CoursService coursService = new CoursService();
        List<String> matieres = coursService.getMatieresByTeacherId(enseignantId);
        for (String m : matieres) {
            courseCombo.addItem(m);
        }

        // Charger les étudiants si une classe est sélectionnée
        if (classCombo.getItemCount() > 0) {
            classCombo.setSelectedIndex(0);
            loadStudents();
        }
    }

    private void loadStudents() {
        model.setRowCount(0);
        String selectedClass = (String) classCombo.getSelectedItem();
        if (selectedClass == null) return;

        EtudiantService etudiantService = new EtudiantService();
        List<Student> students = etudiantService.getStudentsByGroupName(selectedClass);

        for (Student s : students) {
            model.addRow(new Object[]{
                    s.getMatricule(),
                    s.getNom(),
                    s.getPrenom(),
                    null, null, null, null, "En attente"
            });
        }

        updateStatistics();
    }

    private void calculateFinalGrade(int row) {
        try {
            Object ccObj = model.getValueAt(row, 3);
            Object examObj = model.getValueAt(row, 4);
            Object tpObj = model.getValueAt(row, 5);

            // Vérifier si au moins CC et Examen sont renseignés
            if (ccObj == null || ccObj.toString().trim().isEmpty() ||
                    examObj == null || examObj.toString().trim().isEmpty()) {
                model.setValueAt("", row, 6);
                model.setValueAt("En attente", row, 7);
                return;
            }

            double cc = Double.parseDouble(ccObj.toString().trim());
            double exam = Double.parseDouble(examObj.toString().trim());
            double tp = 0.0;
            boolean hasTp = false;

            if (tpObj != null && !tpObj.toString().trim().isEmpty()) {
                tp = Double.parseDouble(tpObj.toString().trim());
                hasTp = true;
            }

            // Validation des notes (0-20)
            if (cc < 0 || cc > 20 || exam < 0 || exam > 20 || (hasTp && (tp < 0 || tp > 20))) {
                model.setValueAt("Note invalide", row, 6);
                model.setValueAt("Erreur", row, 7);
                return;
            }

            double noteFinale;
            if (!hasTp) {
                noteFinale = (cc + exam) / 2;
            } else {
                noteFinale = (cc + exam + tp) / 3;
            }

            String status = noteFinale >= 10 ? "Validé" : "Non Validé";

            model.setValueAt(String.format("%.2f", noteFinale), row, 6);
            model.setValueAt(status, row, 7);

        } catch (NumberFormatException ex) {
            model.setValueAt("Erreur saisie", row, 6);
            model.setValueAt("Erreur", row, 7);
        }
    }

    private void updateStatistics() {
        int totalStudents = model.getRowCount();
        int validatedCount = 0;
        int pendingCount = 0;

        for (int i = 0; i < totalStudents; i++) {
            String status = (String) model.getValueAt(i, 7);
            if ("Validé".equals(status)) {
                validatedCount++;
            } else if ("En attente".equals(status)) {
                pendingCount++;
            }
        }

        String stats = String.format("%d étudiants • %d validés • %d en attente",
                totalStudents, validatedCount, pendingCount);
        statisticsLabel.setText(stats);
    }

    private void addEmptyRow() {
        String[] studentInfo = {"", "Nouveau", "Étudiant", "", "", "", "", "En attente"};
        model.addRow(studentInfo);

        // Sélectionner la nouvelle ligne pour faciliter la saisie
        int newRow = model.getRowCount() - 1;
        table.setRowSelectionInterval(newRow, newRow);
        table.scrollRectToVisible(table.getCellRect(newRow, 0, true));

        updateStatistics();
    }

    private void clearAllGrades() {
        int result = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir effacer toutes les notes ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(null, i, 3);
                model.setValueAt(null, i, 4);
                model.setValueAt(null, i, 5);
                model.setValueAt(null, i, 6);
                model.setValueAt("En attente", i, 7);
            }
            updateStatistics();
        }
    }

    private void previewGrades() {
        StringBuilder preview = new StringBuilder();
        preview.append("Aperçu des notes:\n\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            String nom = (String) model.getValueAt(i, 1);
            String prenom = (String) model.getValueAt(i, 2);
            String noteFinale = (String) model.getValueAt(i, 6);
            String status = (String) model.getValueAt(i, 7);

            if (noteFinale != null && !noteFinale.isEmpty()) {
                preview.append(String.format("%s %s: %s (%s)\n",
                        prenom, nom, noteFinale, status));
            }
        }

        JTextArea textArea = new JTextArea(preview.toString());
        textArea.setEditable(false);
        textArea.setFont(BODY_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Aperçu des Notes",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveNotes() {
        String selectedMatiere = (String) courseCombo.getSelectedItem();
        if (selectedMatiere == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une matière.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmation avant sauvegarde
        int result = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir enregistrer ces notes ?",
                "Confirmation de sauvegarde", JOptionPane.YES_NO_OPTION);

        if (result != JOptionPane.YES_OPTION) return;

        try {
            CoursService coursService = new CoursService();
            int coursId = coursService.getCoursIdByIntitule(selectedMatiere);
            NoteService noteService = new NoteService();

            int savedCount = 0;

            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    Object idObj = model.getValueAt(i, 0);
                    Object ccObj = model.getValueAt(i, 3);
                    Object examObj = model.getValueAt(i, 4);

                    if (idObj == null || ccObj == null || examObj == null) continue;

                    String id = idObj.toString();
                    double cc = Double.parseDouble(ccObj.toString());
                    double exam = Double.parseDouble(examObj.toString());

                    Object tpObj = model.getValueAt(i, 5);
                    double tp = (tpObj != null && !tpObj.toString().trim().isEmpty()) ?
                            Double.parseDouble(tpObj.toString()) : 0.0;

                    double noteFinale = (tpObj == null || tpObj.toString().trim().isEmpty()) ?
                            (cc + exam) / 2 : (cc + exam + tp) / 3;

                    String validation = noteFinale >= 10 ? "Validé" : "Non Validé";

                    Note note = new Note();
                    note.setEtudiantId(Integer.parseInt(id));
                    note.setCoursId(coursId);
                    note.setControleContinu(cc);
                    note.setExamen(exam);
                    note.setTp(tp);
                    note.setNoteFinale(noteFinale);
                    note.setValidation(validation);

                    noteService.saveOrUpdateNote(note);
                    savedCount++;

                } catch (NumberFormatException ex) {
                    // Ignorer les lignes avec des données invalides
                }
            }

            // Message de succès avec statistiques
            String message = String.format(
                    "✅ Sauvegarde réussie!\n\n" +
                            "• %d notes enregistrées\n" +
                            "• Matière: %s\n" +
                            "• Classe: %s",
                    savedCount, selectedMatiere, classCombo.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this, message, "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la sauvegarde: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyModernStyling() {
        // Configuration générale de l'interface
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Utiliser le look and feel par défaut
        }

        // Personnalisation des tooltips
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(5000);

        // Ajout de tooltips
        classCombo.setToolTipText("Sélectionnez la classe pour charger les étudiants");
        courseCombo.setToolTipText("Sélectionnez la matière pour laquelle saisir les notes");
        table.setToolTipText("Double-cliquez sur une cellule pour modifier une note");
    }
}