package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.user.Student;
import com.emsi.gestionuniv.service.EtudiantService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Dialog de gestion des étudiants pour l'administrateur
 * Permet d'ajouter, modifier, supprimer et rechercher des étudiants
 */
public class StudentManagementDialog extends JDialog {
    
    // Couleurs EMSI
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    private EtudiantService etudiantService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private List<Student> currentStudents;
    
    public StudentManagementDialog(JFrame parent) {
        super(parent, "Gestion des Étudiants", true);
        this.etudiantService = new EtudiantService();
        
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
        loadStudents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.CENTER);
        
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
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search controls panel
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchControlsPanel.setBackground(BACKGROUND_WHITE);
        
        JLabel searchLabel = new JLabel("Rechercher :");
        searchLabel.setFont(SUBTITLE_FONT);
        
        searchField = new JTextField(20);
        searchField.setFont(SUBTITLE_FONT);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        
        JButton searchButton = createStyledButton("Rechercher", EMSI_GREEN);
        searchButton.addActionListener(e -> performSearch());
        
        JButton refreshButton = createStyledButton("Actualiser", EMSI_DARK_GREEN);
        refreshButton.addActionListener(e -> loadStudents());
        
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(refreshButton);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        mainPanel.add(searchControlsPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(EMSI_GREEN, 2),
            "Liste des Étudiants",
            0, 0,
            SUBTITLE_FONT,
            EMSI_GREEN
        ));
        
        // Table model
        String[] columns = {"ID", "Matricule", "Nom", "Prénom", "Email", "Filière", "Promotion", "Groupe"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.setFont(SUBTITLE_FONT);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(EMSI_LIGHT_GRAY);
        studentTable.getTableHeader().setForeground(EMSI_GREEN);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setGridColor(new Color(220, 220, 220));
        
        // Double click to edit
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton addButton = createStyledButton("Ajouter Étudiant", EMSI_GREEN);
        addButton.addActionListener(e -> addNewStudent());
        
        JButton editButton = createStyledButton("Modifier", EMSI_DARK_GREEN);
        editButton.addActionListener(e -> editSelectedStudent());
        
        JButton deleteButton = createStyledButton("Supprimer", new Color(220, 53, 69));
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        
        JButton exportExcelButton = createStyledButton("Exporter Excel", EMSI_GREEN);
        exportExcelButton.addActionListener(e -> exportStudentsToExcel());
        JButton exportPdfButton = createStyledButton("Exporter PDF", EMSI_DARK_GREEN);
        exportPdfButton.addActionListener(e -> exportStudentsToPDF());
        

        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(exportExcelButton);
        panel.add(exportPdfButton);


        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
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
        
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void loadStudents() {
        currentStudents = etudiantService.getAllStudents();
        updateTableData(currentStudents);
    }
    
    private void updateTableData(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            Object[] row = {
                student.getId(),
                student.getMatricule(),
                student.getNom(),
                student.getPrenom(),
                student.getEmail(),
                student.getFiliere(),
                student.getPromotion(),
                student.getGroupe()
            };
            tableModel.addRow(row);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudents();
        } else {
            List<Student> searchResults = etudiantService.searchStudents(searchTerm);
            updateTableData(searchResults);
        }
    }
    
    private void addNewStudent() {
        StudentFormDialog dialog = new StudentFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadStudents();
        }
    }
    
    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un étudiant à modifier",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Student student = etudiantService.findStudentById(studentId);
        
        if (student != null) {
            StudentFormDialog dialog = new StudentFormDialog(this, student);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadStudents();
            }
        }
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un étudiant à supprimer",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2) + " " + 
                           (String) tableModel.getValueAt(selectedRow, 3);
        
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'étudiant " + studentName + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            if (etudiantService.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this,
                    "Étudiant supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'étudiant",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportStudents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les étudiants");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.FileWriter fw = new java.io.FileWriter(fileToSave)) {
                // En-têtes
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.write(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
                // Données
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        fw.write(value != null ? value.toString() : "");
                        if (col < tableModel.getColumnCount() - 1) fw.write(",");
                    }
                    fw.write("\n");
                }
                fw.flush();
                JOptionPane.showMessageDialog(this, "Exportation réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'exportation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportStudentsToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les étudiants (Excel)");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }
            try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Etudiants");
                // En-têtes
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    headerRow.createCell(i).setCellValue(tableModel.getColumnName(i));
                }
                // Données
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    org.apache.poi.ss.usermodel.Row excelRow = sheet.createRow(row + 1);
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        excelRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }
                // Autosize columns
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                    workbook.write(fos);
                }
                JOptionPane.showMessageDialog(this, "Exportation Excel réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'exportation Excel : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportStudentsToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les étudiants (PDF)");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
            }
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                com.lowagie.text.Document document = new com.lowagie.text.Document();
                com.lowagie.text.pdf.PdfWriter.getInstance(document, fos);
                document.open();
                // Titre
                com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Liste des Étudiants", titleFont);
                title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                document.add(title);
                document.add(new com.lowagie.text.Paragraph(" "));
                // Tableau
                com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(tableModel.getColumnCount());
                table.setWidthPercentage(100);
                // En-têtes
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(tableModel.getColumnName(i)));
                    cell.setBackgroundColor(new java.awt.Color(0, 148, 68));
                    cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    cell.setPhrase(new com.lowagie.text.Phrase(tableModel.getColumnName(i), new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD, new java.awt.Color(255,255,255))));
                    table.addCell(cell);
                }
                // Données
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        table.addCell(new com.lowagie.text.Phrase(value != null ? value.toString() : ""));
                    }
                }
                document.add(table);
                document.close();
                JOptionPane.showMessageDialog(this, "Exportation PDF réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'exportation PDF : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 