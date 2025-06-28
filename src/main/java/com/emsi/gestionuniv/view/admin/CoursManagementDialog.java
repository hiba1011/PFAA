package com.emsi.gestionuniv.view.admin;

import com.emsi.gestionuniv.model.academic.cours;
import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.service.CoursService;
import com.emsi.gestionuniv.service.TeacherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Dialog de gestion des cours pour l'administrateur
 * Permet d'ajouter, modifier, supprimer et rechercher des cours
 * Version modernisée avec thème EMSI
 */
public class CoursManagementDialog extends JDialog {
    
    // Couleurs EMSI modernisées
    private static final Color EMSI_GREEN_PRIMARY = new Color(34, 139, 34);
    private static final Color EMSI_GREEN_LIGHT = new Color(144, 238, 144);
    private static final Color EMSI_GREEN_DARK = new Color(0, 100, 0);
    private static final Color EMSI_GREEN_ACCENT = new Color(50, 205, 50);
    private static final Color EMSI_BACKGROUND = new Color(248, 253, 248);
    private static final Color EMSI_CARD_BG = Color.WHITE;
    private static final Color EMSI_TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color EMSI_TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color EMSI_BORDER = new Color(200, 230, 200);
    private static final Color EMSI_ERROR = new Color(220, 53, 69);
    private static final Color EMSI_WARNING = new Color(255, 193, 7);
    private static final Color EMSI_SUCCESS = new Color(40, 167, 69);
    private static final Color EMSI_INFO = new Color(23, 162, 184);

    // Polices modernisées
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private CoursService coursService;
    private JTable coursTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private JLabel totalCoursLabel;
    private List<com.emsi.gestionuniv.service.CoursService.CoursWithEnseignant> currentCours;
    
    public CoursManagementDialog(JFrame parent) {
        super(parent, "Gestion des Cours - EMSI", true);
        this.coursService = new CoursService();
        
        initializeDialog();
        initComponents();
        loadCours();
    }
    
    private void initializeDialog() {
        setSize(1200, 800);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setMinimumSize(new Dimension(1000, 600));

        // Fond dégradé pour la fenêtre
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_BACKGROUND,
                        0, getHeight(), new Color(240, 248, 240)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });
        getContentPane().setLayout(new BorderLayout());
    }

    private void initComponents() {
        // Header avec titre et statistiques
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel principal avec recherche et tableau
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Panel de boutons d'action
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);

        // Barre de statut
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));

        // Fond dégradé
        headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, EMSI_GREEN_PRIMARY,
                        getWidth(), 0, EMSI_GREEN_DARK
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effet de brillance
                GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 30),
                        0, getHeight() / 2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Titre principal
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestion des Cours");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Administration des cours et assignation des enseignants");
        subtitleLabel.setFont(TEXT_FONT);
        subtitleLabel.setForeground(new Color(240, 255, 240));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setOpaque(false);

        totalCoursLabel = new JLabel("Total: 0 cours");
        totalCoursLabel.setFont(SUBTITLE_FONT);
        totalCoursLabel.setForeground(Color.WHITE);

        statsPanel.add(totalCoursLabel);

        // --- Ajout du bouton Ajouter Cours en haut à droite ---
        JButton addButton = createStyledButton("Ajouter Cours", EMSI_SUCCESS, true);
        addButton.addActionListener(e -> addNewCours());
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(addButton);
        // --- Ajout du bouton Supprimer Cours en haut à droite ---
        JButton deleteButton = createStyledButton(" Supprimer", EMSI_ERROR, true);
        deleteButton.addActionListener(e -> deleteSelectedCours());
        statsPanel.add(Box.createHorizontalStrut(10));
        statsPanel.add(deleteButton);
        // --- Ajout du bouton Exporter Cours en haut à droite ---
        JButton exportButton = createStyledButton("Exporter", EMSI_GREEN_PRIMARY, false);
        exportButton.addActionListener(e -> exportData());
        statsPanel.add(Box.createHorizontalStrut(10));
        statsPanel.add(exportButton);
        // -----------------------------------------------------

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        // Panel de recherche et filtres
        JPanel searchPanel = createSearchPanel();

        // Panel du tableau
        JPanel tablePanel = createTablePanel();

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(EMSI_CARD_BG);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(EMSI_BORDER, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

        // Ombre de la carte
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 15)),
                BorderFactory.createCompoundBorder(
                        new LineBorder(EMSI_BORDER, 1, true),
                        new EmptyBorder(15, 20, 15, 20)
                )
        ));

        // Icône de recherche
        JLabel searchIcon = new JLabel("");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(TEXT_FONT);
        searchLabel.setForeground(EMSI_TEXT_PRIMARY);

        searchField = createStyledTextField("Tapez pour rechercher...", 25);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        
        // Effet placeholder
        searchField.setForeground(EMSI_TEXT_SECONDARY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Tapez pour rechercher...")) {
                    searchField.setText("");
                    searchField.setForeground(EMSI_TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tapez pour rechercher...");
                    searchField.setForeground(EMSI_TEXT_SECONDARY);
                }
            }
        });

        JButton clearButton = createIconButton("✕", "Effacer");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            searchField.setForeground(EMSI_TEXT_PRIMARY);
            loadCours();
        });

        JButton refreshButton = createStyledButton("Actualiser", EMSI_INFO, false);
        refreshButton.addActionListener(e -> {
            loadCours();
            showStatusMessage("Liste actualisée", false);
        });

        searchPanel.add(searchIcon);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(refreshButton);

        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(EMSI_CARD_BG);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 20)),
                BorderFactory.createCompoundBorder(
                        new LineBorder(EMSI_BORDER, 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                )
        ));

        // Titre du tableau
        JLabel tableTitle = new JLabel("Liste des Cours");
        tableTitle.setFont(SUBTITLE_FONT);
        tableTitle.setForeground(EMSI_GREEN_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Configuration du tableau
        String[] columns = {"ID", "Code", "Intitulé", "Nom enseignant", "Filière", "Niveau", "Effectif", "Volume Horaire"};
        DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) return Integer.class;
                return String.class;
            }
        };

        coursTable = new JTable(newModel);
        tableModel = newModel;

        // Style du tableau
        coursTable.setRowHeight(35);
        coursTable.setFont(TABLE_FONT);
        coursTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursTable.setGridColor(new Color(230, 230, 230));
        coursTable.setShowGrid(true);
        coursTable.setIntercellSpacing(new Dimension(1, 1));

        // Header du tableau
        JTableHeader header = coursTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(EMSI_GREEN_LIGHT);
        header.setForeground(EMSI_GREEN_DARK);
        header.setBorder(new LineBorder(EMSI_GREEN_PRIMARY, 1));
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer personnalisé pour les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        coursTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        coursTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Code
        coursTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Filière
        coursTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Niveau
        coursTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Effectif
        coursTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Vol. Horaire

        // Renderer pour la colonne statut
        coursTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String status = (String) value;
                if ("Assigné".equals(status)) {
                    setForeground(EMSI_SUCCESS);
                    setText("Assigné");
                } else {
                    setForeground(EMSI_WARNING);
                    setText("Non assigné");
                }

                if (isSelected) {
                    setBackground(EMSI_GREEN_LIGHT);
                } else {
                    setBackground(Color.WHITE);
                }

                return this;
            }
        });

        // Largeurs des colonnes
        coursTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        coursTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Code
        coursTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Intitulé
        coursTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Nom enseignant
        coursTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Filière
        coursTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Niveau
        coursTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Effectif
        coursTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Volume Horaire

        // Double clic pour modifier
        coursTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedCours();
                }
            }
        });

        // Sélection de ligne
        coursTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = coursTable.getSelectedRow();
                if (selectedRow != -1) {
                    String coursName = (String) tableModel.getValueAt(selectedRow, 2);
                    showStatusMessage("Cours sélectionné: " + coursName, false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(coursTable);
        scrollPane.setBorder(new LineBorder(EMSI_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 30, 20, 30));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton addButton = createStyledButton("Ajouter Cours", EMSI_SUCCESS, true);
        addButton.addActionListener(e -> addNewCours());
        
        JButton editButton = createStyledButton("Modifier", EMSI_INFO, true);
        editButton.addActionListener(e -> editSelectedCours());
        
        JButton deleteButton = createStyledButton("Supprimer", EMSI_ERROR, true);
        deleteButton.addActionListener(e -> deleteSelectedCours());
        
        JButton exportButton = createStyledButton("Exporter", EMSI_GREEN_PRIMARY, false);
        exportButton.addActionListener(e -> exportData());

        JButton closeButton = createStyledButton("✕ Fermer", new Color(108, 117, 125), false);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EMSI_BORDER),
                new EmptyBorder(8, 15, 8, 15)
        ));

        statusLabel = new JLabel("Prêt");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(EMSI_TEXT_SECONDARY);

        JLabel timestampLabel = new JLabel("Dernière mise à jour: " +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timestampLabel.setForeground(EMSI_TEXT_SECONDARY);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timestampLabel, BorderLayout.EAST);

        return statusPanel;
    }

    // Méthodes utilitaires pour créer les composants stylisés
    private JTextField createStyledTextField(String placeholder, int columns) {
        JTextField field = new JTextField(placeholder, columns);
        field.setFont(TEXT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(EMSI_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);

        // Effet focus
        field.addFocusListener(new FocusAdapter() {
                    @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(EMSI_GREEN_PRIMARY, 2, true),
                        new EmptyBorder(7, 11, 7, 11)
                ));
                    }
                    
                    @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(EMSI_BORDER, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private static JButton createStyledButton(String text, Color backgroundColor, boolean primary) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (primary) {
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
            button.setBorder(new EmptyBorder(12, 20, 12, 20));
        } else {
            button.setBackground(new Color(248, 249, 250));
            button.setForeground(backgroundColor);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(backgroundColor, 1, true),
                    new EmptyBorder(11, 19, 11, 19)
            ));
        }

        // Effets hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (primary) {
                    button.setBackground(backgroundColor.darker());
                } else {
                    button.setBackground(backgroundColor);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (primary) {
                    button.setBackground(backgroundColor);
                } else {
                    button.setBackground(new Color(248, 249, 250));
                    button.setForeground(backgroundColor);
                }
            }
        });

        return button;
    }

    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(30, 30));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setForeground(EMSI_TEXT_SECONDARY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(EMSI_ERROR);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
                button.setForeground(EMSI_TEXT_SECONDARY);
            }
        });
        
        return button;
    }
    
    // Méthodes de gestion des données
    private void loadCours() {
        SwingUtilities.invokeLater(() -> {
            try {
                showStatusMessage("Chargement des cours...", false);

        currentCours = coursService.getAllCoursWithEnseignant();
        tableModel.setRowCount(0);

                int assignedCount = 0;

        for (com.emsi.gestionuniv.service.CoursService.CoursWithEnseignant c : currentCours) {
                    // Log pour debug
                    System.out.println("DEBUG: " + c.getIntitule() + " | Enseignant: '" + c.getEnseignantNom() + "'");

                    if (c.getEnseignantNom() != null) {
                        assignedCount++;
                    }

                    String enseignantNom = ((c.getEnseignantPrenom() != null ? c.getEnseignantPrenom() : "")
                        + " " + (c.getEnseignantNom() != null ? c.getEnseignantNom() : "")).trim();
                    String status = !enseignantNom.isEmpty() ? "Assigné" : "Non assigné";
                    String volumeHoraire = c.getVolumeHoraire() != null ? c.getVolumeHoraire() : "N/A";

            tableModel.addRow(new Object[] {
                c.getId(),
                            c.getCode() != null ? c.getCode() : "N/A",
                            c.getIntitule() != null ? c.getIntitule() : "N/A",
                            enseignantNom,
                            c.getFiliere() != null ? c.getFiliere() : "N/A",
                            c.getNiveau() != null ? c.getNiveau() : "N/A",
                c.getEffectif(),
                            volumeHoraire,
                            status
                    });
                }

                // Mise à jour des statistiques
                int totalCours = currentCours.size();
                totalCoursLabel.setText(String.format("Total: %d cours (%d assignés)", totalCours, assignedCount));

                showStatusMessage(String.format("✓ %d cours chargés avec succès", totalCours), false);

            } catch (Exception e) {
                showStatusMessage("Erreur lors du chargement: " + e.getMessage(), true);
                System.err.println("Erreur lors du chargement des cours: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty() || searchTerm.equals("tapez pour rechercher...")) {
            loadCours();
            return;
        }
        
        try {
        List<com.emsi.gestionuniv.service.CoursService.CoursWithEnseignant> filteredCours = currentCours.stream()
            .filter(c -> 
                (c.getCode() != null && c.getCode().toLowerCase().contains(searchTerm)) ||
                (c.getIntitule() != null && c.getIntitule().toLowerCase().contains(searchTerm)) ||
                (c.getFiliere() != null && c.getFiliere().toLowerCase().contains(searchTerm)) ||
                                    (c.getNiveau() != null && c.getNiveau().toLowerCase().contains(searchTerm)) ||
                                    (c.getEnseignantNom() != null && c.getEnseignantNom().toLowerCase().contains(searchTerm)) ||
                                    (c.getEnseignantPrenom() != null && c.getEnseignantPrenom().toLowerCase().contains(searchTerm))
            )
            .toList();
        
        tableModel.setRowCount(0);
            int assignedCount = 0;

        for (com.emsi.gestionuniv.service.CoursService.CoursWithEnseignant c : filteredCours) {
                String enseignantNom = ((c.getEnseignantPrenom() != null ? c.getEnseignantPrenom() : "")
                    + " " + (c.getEnseignantNom() != null ? c.getEnseignantNom() : "")).trim();
                String status = !enseignantNom.isEmpty() ? "Assigné" : "Non assigné";
                String volumeHoraire = c.getVolumeHoraire() != null ? c.getVolumeHoraire() : "N/A";

            tableModel.addRow(new Object[] {
                c.getId(),
                        c.getCode() != null ? c.getCode() : "N/A",
                        c.getIntitule() != null ? c.getIntitule() : "N/A",
                        enseignantNom,
                        c.getFiliere() != null ? c.getFiliere() : "N/A",
                        c.getNiveau() != null ? c.getNiveau() : "N/A",
                c.getEffectif(),
                        volumeHoraire,
                        status
                });
            }

            totalCoursLabel.setText(String.format("Résultats: %d cours trouvés (%d assignés)",
                    filteredCours.size(), assignedCount));

            showStatusMessage(String.format("%d résultat(s) trouvé(s) pour \"%s\"",
                    filteredCours.size(), searchTerm), false);

        } catch (Exception e) {
            showStatusMessage(" Erreur lors de la recherche: " + e.getMessage(), true);
            System.err.println("Erreur lors de la recherche des cours: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addNewCours() {
        CoursFormDialog dialog = new CoursFormDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadCours();
            showStatusMessage("✓ Nouveau cours ajouté avec succès", false);
        }
    }
    
    private void editSelectedCours() {
        int selectedRow = coursTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Veuillez sélectionner un cours à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
        int coursId = (Integer) tableModel.getValueAt(selectedRow, 0);
            cours selectedCours = coursService.getCoursById(coursId);
        
        if (selectedCours != null) {
            CoursFormDialog dialog = new CoursFormDialog(this, selectedCours);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                loadCours();
                    showStatusMessage("✓ Cours modifié avec succès", false);
                }
            } else {
                showMessage("Cours introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            showStatusMessage("Erreur lors de la modification: " + e.getMessage(), true);
            System.err.println("Erreur lors de l'édition du cours: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void deleteSelectedCours() {
        int selectedRow = coursTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Veuillez sélectionner un cours à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
        int coursId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String coursName = (String) tableModel.getValueAt(selectedRow, 2);
        
            // Dialogue de confirmation avec style personnalisé
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer le cours :\n\n\"" + coursName + "\" ?\n\n" +
                            "Cette action est irréversible et supprimera également :\n" +
                            "• Toutes les assignations d'enseignants\n" +
                            "• Tous les étudiants inscrits\n" +
                            "• Toutes les notes associées",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                boolean success = coursService.deleteCours(coursId);

                if (success) {
                    loadCours();
                    showStatusMessage("✓ Cours \"" + coursName + "\" supprimé avec succès", false);
                } else {
                    showMessage("Échec de la suppression du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            showStatusMessage(" Erreur lors de la suppression: " + e.getMessage(), true);
            System.err.println("Erreur lors de la suppression du cours: " + e.getMessage());
            e.printStackTrace();

            showMessage(
                    "Une erreur est survenue lors de la suppression :\n" + e.getMessage(),
                    "Erreur de suppression",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportData() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exporter la liste des cours");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // Filtres de fichier
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Fichiers CSV (*.csv)", "csv"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Fichiers Excel (*.xlsx)", "xlsx"));

            // Nom de fichier par défaut
            String defaultFileName = "cours_export_" +
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            fileChooser.setSelectedFile(new java.io.File(defaultFileName + ".csv"));

            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getAbsolutePath();

                // Déterminer le format d'export
                if (fileName.toLowerCase().endsWith(".xlsx")) {
                    exportToExcel(fileName);
                } else {
                    // Par défaut, export CSV
                    if (!fileName.toLowerCase().endsWith(".csv")) {
                        fileName += ".csv";
                    }
                    exportToCSV(fileName);
                }

                showStatusMessage("✓ Export réussi vers: " + selectedFile.getName(), false);

                // Demander si l'utilisateur veut ouvrir le fichier
                int openOption = JOptionPane.showConfirmDialog(
                        this,
                        "Export terminé avec succès !\n\nVoulez-vous ouvrir le fichier ?",
                        "Export réussi",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (openOption == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(selectedFile);
                }
            }

        } catch (Exception e) {
            showStatusMessage(" Erreur lors de l'export: " + e.getMessage(), true);
            System.err.println("Erreur lors de l'export: " + e.getMessage());
            e.printStackTrace();

            showMessage(
                    "Une erreur est survenue lors de l'export :\n" + e.getMessage(),
                    "Erreur d'export",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportToCSV(String fileName) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(
                new java.io.FileWriter(fileName, java.nio.charset.StandardCharsets.UTF_8))) {

            // En-tête UTF-8 BOM pour Excel
            writer.print('\ufeff');

            // En-têtes des colonnes
            writer.println("ID,Code,Intitulé,Nom enseignant,Filière,Niveau,Effectif,Volume Horaire,Statut");

            // Données
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object value = tableModel.getValueAt(i, j);
                    String cellValue = value != null ? value.toString() : "";

                    // Échapper les guillemets et virgules
                    if (cellValue.contains(",") || cellValue.contains("\"") || cellValue.contains("\n")) {
                        cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                    }

                    line.append(cellValue);
                    if (j < tableModel.getColumnCount() - 1) {
                        line.append(",");
                    }
                }
                writer.println(line.toString());
            }
        }
    }

    private void exportToExcel(String fileName) throws Exception {
        // Cette méthode nécessiterait Apache POI pour créer des fichiers Excel
        // Pour l'instant, on fait un export CSV avec extension xlsx
        exportToCSV(fileName.replace(".xlsx", ".csv"));
    }

    private void showMessage(String message, String title, int messageType) {
        // Dialogue personnalisé avec le thème EMSI
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);

        // Personnalisation du dialogue
        dialog.getContentPane().setBackground(EMSI_CARD_BG);

        dialog.setVisible(true);
    }

    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? EMSI_ERROR : EMSI_SUCCESS);

        // Animation du message de statut
        Timer timer = new Timer(3000, e -> {
            statusLabel.setText("Prêt");
            statusLabel.setForeground(EMSI_TEXT_SECONDARY);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Getters pour l'accès depuis les dialogues enfants
    public CoursService getCoursService() {
        return coursService;
    }

    public void refreshData() {
        loadCours();
    }

    // Classe interne pour le dialogue de formulaire de cours
    private static class CoursFormDialog extends JDialog {
        private cours coursToEdit;
        private boolean confirmed = false;
        private CoursService coursService;
        private TeacherService teacherService;

        // Composants du formulaire
        private JTextField codeField;
        private JTextField intituleField;
        private JComboBox<String> filiereCombo;
        private JComboBox<String> niveauCombo;
        private JSpinner effectifSpinner;
        private JTextField volumeHoraireField;
        private JComboBox<Teacher> enseignantCombo;
        private JTextArea descriptionArea;

        public CoursFormDialog(JDialog parent, cours coursToEdit) {
            super(parent, coursToEdit == null ? "Ajouter un Cours" : "Modifier le Cours", true);
            this.coursToEdit = coursToEdit;
            this.coursService = new CoursService();
            this.teacherService = new TeacherService();

            initializeDialog();
            initComponents();
            loadData();

            if (coursToEdit != null) {
                populateFields();
            }
        }

        private void initializeDialog() {
            setSize(600, 700);
            setLocationRelativeTo(getParent());
            setResizable(false);

            // Fond dégradé
            setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    GradientPaint gradient = new GradientPaint(
                            0, 0, EMSI_BACKGROUND,
                            0, getHeight(), Color.WHITE
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            });
            getContentPane().setLayout(new BorderLayout());
        }

        private void initComponents() {
            // Header
            JPanel headerPanel = createFormHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);

            // Formulaire principal
            JPanel formPanel = createFormPanel();
            add(formPanel, BorderLayout.CENTER);

            // Boutons
            JPanel buttonPanel = createFormButtonPanel();
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private JPanel createFormHeaderPanel() {
            JPanel headerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gradient = new GradientPaint(
                            0, 0, EMSI_GREEN_PRIMARY,
                            getWidth(), 0, EMSI_GREEN_DARK
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setPreferredSize(new Dimension(0, 80));
            headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

            JLabel titleLabel = new JLabel(coursToEdit == null ? " Nouveau Cours" : " Modifier le Cours");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(Color.WHITE);

            JLabel subtitleLabel = new JLabel("Remplissez les informations du cours");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitleLabel.setForeground(new Color(240, 255, 240));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setOpaque(false);
            titlePanel.add(titleLabel, BorderLayout.NORTH);
            titlePanel.add(subtitleLabel, BorderLayout.CENTER);

            headerPanel.add(titlePanel, BorderLayout.WEST);

            return headerPanel;
        }

        private JPanel createFormPanel() {
            JPanel formPanel = new JPanel();
            formPanel.setOpaque(false);
            formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
            formPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;

            // Code du cours
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(createFieldLabel("Code du cours *:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            codeField = createStyledFormField(20);
            formPanel.add(codeField, gbc);

            // Intitulé
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Intitulé *:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            intituleField = createStyledFormField(30);
            formPanel.add(intituleField, gbc);

            // Filière
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Filière *:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            String[] filieres = {"IIR", "IFA", "GC", "IAII"};
            filiereCombo = new JComboBox<>(filieres);
            styleComboBox(filiereCombo);
            formPanel.add(filiereCombo, gbc);

            // Niveau
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Niveau *:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            String[] niveaux = {"1ère année préparatoire", "2ème année préparatoire", "3ème année", "4ème année", "5ème année"};
            niveauCombo = new JComboBox<>(niveaux);
            styleComboBox(niveauCombo);
            formPanel.add(niveauCombo, gbc);

            // Effectif
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Effectif:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            effectifSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 200, 1));
            styleSpinner(effectifSpinner);
            formPanel.add(effectifSpinner, gbc);

            // Volume horaire
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Volume horaire:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            volumeHoraireField = createStyledFormField(15);
            volumeHoraireField.setToolTipText("Ex: 30h, 2h/semaine, etc.");
            formPanel.add(volumeHoraireField, gbc);

            // Enseignant
            gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Enseignant:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            enseignantCombo = new JComboBox<>();
            styleComboBox(enseignantCombo);
            formPanel.add(enseignantCombo, gbc);

            // Description
            gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(createFieldLabel("Description:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
            descriptionArea = new JTextArea(4, 30);
            descriptionArea.setFont(TEXT_FONT);
            descriptionArea.setBorder(new EmptyBorder(8, 8, 8, 8));
            descriptionArea.setBackground(Color.WHITE);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);

            JScrollPane descScrollPane = new JScrollPane(descriptionArea);
            descScrollPane.setBorder(new LineBorder(EMSI_BORDER, 1, true));
            formPanel.add(descScrollPane, gbc);

            return formPanel;
        }

        private JPanel createFormButtonPanel() {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(new EmptyBorder(10, 30, 20, 30));

            JButton saveButton = createStyledButton(
                    coursToEdit == null ? " Ajouter" : " Modifier",
                    EMSI_SUCCESS, true
            );
            saveButton.addActionListener(e -> saveCours());

            JButton cancelButton = createStyledButton(" Annuler", new Color(108, 117, 125), false);
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            return buttonPanel;
        }

        private JLabel createFieldLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(EMSI_TEXT_PRIMARY);
            return label;
        }

        private JTextField createStyledFormField(int columns) {
            JTextField field = new JTextField(columns);
            field.setFont(TEXT_FONT);
            field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(EMSI_BORDER, 1, true),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            field.setBackground(Color.WHITE);

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(EMSI_GREEN_PRIMARY, 2, true),
                            new EmptyBorder(7, 11, 7, 11)
                    ));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(EMSI_BORDER, 1, true),
                            new EmptyBorder(8, 12, 8, 12)
                    ));
                }
            });

            return field;
        }

        private void styleComboBox(JComboBox<?> combo) {
            combo.setFont(TEXT_FONT);
            combo.setBackground(Color.WHITE);
            combo.setBorder(new LineBorder(EMSI_BORDER, 1, true));
            combo.setPreferredSize(new Dimension(0, 35));
        }

        private void styleSpinner(JSpinner spinner) {
            spinner.setFont(TEXT_FONT);
            spinner.setBorder(new LineBorder(EMSI_BORDER, 1, true));
            spinner.setPreferredSize(new Dimension(0, 35));

            // Style de l'éditeur
            JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
            editor.getTextField().setBorder(new EmptyBorder(8, 12, 8, 12));
        }

        private void loadData() {
            try {
                // Charger les enseignants
                enseignantCombo.removeAllItems();
                enseignantCombo.addItem(null); // Option "Aucun enseignant"

                List<Teacher> teachers = teacherService.getAllTeachers();
                for (Teacher teacher : teachers) {
                    enseignantCombo.addItem(teacher);
                }

                // Renderer personnalisé pour l'affichage des enseignants
                enseignantCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                  int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        if (value == null) {
                            setText("-- Aucun enseignant --");
                            setForeground(EMSI_TEXT_SECONDARY);
                        } else if (value instanceof Teacher) {
                            Teacher teacher = (Teacher) value;
                            setText(teacher.getPrenom() + " " + teacher.getNom());
                        }

                        return this;
                    }
                });

            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void populateFields() {
            if (coursToEdit != null) {
                codeField.setText(coursToEdit.getCode());
                intituleField.setText(coursToEdit.getIntitule());

                if (coursToEdit.getFiliere() != null) {
                    filiereCombo.setSelectedItem(coursToEdit.getFiliere());
                }

                if (coursToEdit.getNiveau() != null) {
                    niveauCombo.setSelectedItem(coursToEdit.getNiveau());
                }

                if (coursToEdit.getEnseignantId() != 0) {
                    effectifSpinner.setValue(coursToEdit.getEffectif());
                }

                volumeHoraireField.setText(coursToEdit.getVolumeHoraire());

                // Sélectionner l'enseignant si assigné
                if (coursToEdit.getEnseignantId() != 0) {
                    for (int i = 0; i < enseignantCombo.getItemCount(); i++) {
                        Teacher teacher = enseignantCombo.getItemAt(i);
                        if (teacher != null && teacher.getId() == coursToEdit.getEnseignantId()) {
                            enseignantCombo.setSelectedItem(teacher);
                            break;
                        }
                    }
                }

                descriptionArea.setText(coursToEdit.getDescription());
            }
        }

        private void saveCours() {
            if (!validateForm()) {
                return;
            }

            try {
                cours cours = coursToEdit != null ? coursToEdit : new cours();

                cours.setCode(codeField.getText().trim());
                cours.setIntitule(intituleField.getText().trim());
                cours.setFiliere((String) filiereCombo.getSelectedItem());
                cours.setNiveau((String) niveauCombo.getSelectedItem());
                cours.setEffectif(effectifSpinner.getValue() != null ? (Integer) effectifSpinner.getValue() : 0);
                cours.setVolumeHoraire(volumeHoraireField.getText().trim());
                cours.setDescription(descriptionArea.getText().trim());

                // Enseignant sélectionné
                Teacher selectedTeacher = (Teacher) enseignantCombo.getSelectedItem();
                if (selectedTeacher != null) {
                    cours.setEnseignantId(selectedTeacher.getId());
                } else {
                    cours.setEnseignantId(0);
                }

                boolean success;
                if (coursToEdit == null) {
                    success = coursService.addCours(cours);
                } else {
                    success = coursService.updateCours(cours);
                }

                if (success) {
                    confirmed = true;
                    dispose();
                } else {
                    showMessage("Échec de la sauvegarde du cours", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
                e.printStackTrace();
                showMessage("Une erreur est survenue lors de la sauvegarde :\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean validateForm() {
            StringBuilder errors = new StringBuilder();

            if (codeField.getText().trim().isEmpty()) {
                errors.append("• Le code du cours est obligatoire\n");
            }

            if (intituleField.getText().trim().isEmpty()) {
                errors.append("• L'intitulé du cours est obligatoire\n");
            }

            if (filiereCombo.getSelectedItem() == null) {
                errors.append("• La filière est obligatoire\n");
            }

            if (niveauCombo.getSelectedItem() == null) {
                errors.append("• Le niveau est obligatoire\n");
            }

            if (errors.length() > 0) {
                showMessage("Veuillez corriger les erreurs suivantes :\n\n" + errors.toString(),
                        "Erreurs de validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;
        }

        private void showMessage(String message, String title, int messageType) {
            JOptionPane.showMessageDialog(this, message, title, messageType);
        }

        public boolean isConfirmed() {
            return confirmed;
        }
    }
} 