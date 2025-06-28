package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.service.TeacherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TeacherManagementDialog extends JDialog {
    private static final Color EMSI_GREEN = new Color(0, 148, 68);
    private static final Color EMSI_DARK_GREEN = new Color(0, 104, 56);
    private static final Color EMSI_GRAY = new Color(88, 88, 90);
    private static final Color EMSI_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND_WHITE = new Color(252, 252, 252);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);

    private TeacherService teacherService;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Teacher> currentTeachers;

    public TeacherManagementDialog(JFrame parent) {
        super(parent, "Gestion des Enseignants", true);
        this.teacherService = new TeacherService();
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        loadTeachers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.CENTER);
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
        JLabel titleLabel = new JLabel("Gestion des Enseignants");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
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
        refreshButton.addActionListener(e -> loadTeachers());
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(refreshButton);
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
            "Liste des Enseignants",
            0, 0,
            SUBTITLE_FONT,
            EMSI_GREEN
        ));
        String[] columns = {"ID", "Prénom", "Nom", "Email", "Département", "Spécialité", "Téléphone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(30);
        teacherTable.setFont(SUBTITLE_FONT);
        teacherTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherTable.getTableHeader().setBackground(EMSI_LIGHT_GRAY);
        teacherTable.getTableHeader().setForeground(EMSI_GREEN);
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.setGridColor(new Color(220, 220, 220));
        teacherTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTeacher();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(EMSI_GREEN, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        JButton addButton = createStyledButton("Ajouter Enseignant", EMSI_GREEN);
        addButton.addActionListener(e -> addNewTeacher());
        JButton editButton = createStyledButton("Modifier", EMSI_DARK_GREEN);
        editButton.addActionListener(e -> editSelectedTeacher());
        JButton deleteButton = createStyledButton("Supprimer", new Color(220, 53, 69));
        deleteButton.addActionListener(e -> deleteSelectedTeacher());
        JButton exportExcelButton = createStyledButton("Exporter Excel", EMSI_GREEN);
        exportExcelButton.addActionListener(e -> exportTeachersToExcel());
        JButton exportPdfButton = createStyledButton("Exporter PDF", EMSI_DARK_GREEN);
        exportPdfButton.addActionListener(e -> exportTeachersToPDF());

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
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
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

    private void loadTeachers() {
        currentTeachers = teacherService.getAllTeachers();
        updateTableData(currentTeachers);
    }

    private void updateTableData(List<Teacher> teachers) {
        tableModel.setRowCount(0);
        for (Teacher teacher : teachers) {
            Object[] row = {
                teacher.getId(),
                teacher.getPrenom(),
                teacher.getNom(),
                teacher.getEmail(),
                teacher.getDepartement(),
                teacher.getSpecialite(),
                teacher.getTelephone()
            };
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadTeachers();
        } else {
            List<Teacher> searchResults = teacherService.searchTeachers(searchTerm);
            updateTableData(searchResults);
        }
    }

    private void addNewTeacher() {
        TeacherFormDialog dialog = new TeacherFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadTeachers();
        }
    }

    private void editSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un enseignant à modifier",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int teacherId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        if (teacher != null) {
            TeacherFormDialog dialog = new TeacherFormDialog(this, teacher);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadTeachers();
            }
        }
    }

    private void deleteSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un enseignant à supprimer",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int teacherId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String teacherName = (String) tableModel.getValueAt(selectedRow, 2) + " " +
                           (String) tableModel.getValueAt(selectedRow, 1);
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'enseignant " + teacherName + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            if (teacherService.deleteTeacher(teacherId)) {
                JOptionPane.showMessageDialog(this,
                    "Enseignant supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                loadTeachers();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'enseignant",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportTeachersToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les enseignants (Excel)");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }
            try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Enseignants");
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

    private void exportTeachersToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les enseignants (PDF)");
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
                com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Liste des Enseignants", titleFont);
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