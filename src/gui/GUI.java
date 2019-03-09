package gui;

import gpdraw.SketchPadPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import geometry.LineBuilder;
import geometry.Coordinate;
import geometry.Segment;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 3/7/2019
 *
 * This is the GUI for the program. It can display all the current lines drawn, original given coordinates, available
 * coordinates, and place flags for the editor.
 */
public class GUI {

    /*
        TODO - Create origin point changer/selector
        TODO - Clean up looks
     */

    private JFrame window;

    private JTabbedPane panelSwitcher;

    private JPanel stepPanel, infoPanel, buttonPanel, superDrawPanel, flagPanel;

    private JTable availableCoordinatesTable, currentLinesTable, lastStepAvailableCoordinatesTable;

    private LineTableModel currentLinesModel;

    private SketchPadPanel drawPanel;

    private LineBuilder lineBuilder;

    private JLabel stepLabel, stepLastCoordinates, errorLabel, flagHelpLabel, mouseNearestCoordinate;

    private Pen pen;

    private int numOfSteps;

    private boolean canClickDrawPanel = true;

    private void draw() {
        Thread drawThread = new Thread(() -> {
            for (Coordinate coordinate : lineBuilder.getAvailableCoordinates())
                coordinate.draw(pen, Color.BLACK);
            for (Segment line : lineBuilder.getCreatedLines())
                line.draw(pen);
            for (LineBuilder.Flag flag : lineBuilder.getFlags()) {
                flag.getCoordinate().draw(pen, Color.RED);
            }
        });
        drawThread.start();
    }

    private void onChange() {
        errorLabel.setText("");
        flagHelpLabel.setText("");
        mouseNearestCoordinate.setText("");
    }

    private void clearDrawPanel() {
        pen.setColor(Color.WHITE);
        drawPanel.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
    }

    private void update() {
        stepLabel.setText("Steps Completed: " + numOfSteps);
        if (numOfSteps == 0)
            stepLastCoordinates.setText("Step Coordinates: Not Available");
        else {
            try {
                stepLastCoordinates.setText("Step Coordinates: " + lineBuilder.getLastStep().getAvailableCoordinates());
            } catch(NullPointerException e) {
                stepLastCoordinates.setText("Step Coordinates: " + 0);
            }
        }

        draw();

        availableCoordinatesTable.setModel(new CoordinateTableModel(lineBuilder.getAvailableCoordinates()));
        availableCoordinatesTable.repaint();
        currentLinesTable.setModel(new LineTableModel(lineBuilder.getCreatedLines()));
        currentLinesTable.repaint();
        try {
            lastStepAvailableCoordinatesTable.setModel(new CoordinateTableModel(lineBuilder.getLastStep().getAvailableCoordinates()));
        } catch(NullPointerException e) {
            lastStepAvailableCoordinatesTable.setModel(new CoordinateTableModel(new ArrayList<>()));
        }
    }

    private void nextButtonClicked() {
        onChange();
        lineBuilder.step();
        switch (lineBuilder.getLastStep().getStepResult()) {
            case SUCCESSFUL:
                numOfSteps++;
                break;
            case FAILED:
                lineBuilder.getSteps().remove(lineBuilder.getLastStep());
                errorLabel.setText("Step failed.");
                break;
            case NOMORELINES:
                errorLabel.setText("No more lines can be drawn.");
                lineBuilder.getSteps().remove(lineBuilder.getLastStep());
                break;
        }
        update();
    }

    private void backButtonClicked() {
        onChange();
        try {
            lineBuilder.back();
            numOfSteps--;
            clearDrawPanel();
        } catch (LineBuilder.CannotGoBackException e) {
            errorLabel.setText("Cannot go back.");
        }
        update();
    }

    private void resetButtonClicked() {
        onChange();
        numOfSteps = 0;
        clearDrawPanel();
        lineBuilder = new LineBuilder(lineBuilder.getOriginalCoordinates());
        update();
    }

    private void flagButtonClicked() {
        canClickDrawPanel = false;
        flagHelpLabel.setText("Pick a point on the graph to flag.");
        flagHelpLabel.setForeground(new Color(0, 150, 250));
        MouseAdapter mouse = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Coordinate coordinate = new Coordinate(e.getX() - drawPanel.getWidth() / 2.0D, -e.getY() + drawPanel.getHeight() / 2.0D);
                for (Coordinate panelCoordinate : lineBuilder.getAvailableCoordinates()) {
                    if (coordinate.distance(panelCoordinate) < 5) {
                        lineBuilder.getFlags().add(new LineBuilder.Flag(panelCoordinate));
                        flagHelpLabel.setText("Added Flag at " + panelCoordinate);
                        flagHelpLabel.setForeground(new Color(100, 200, 0));
                        draw();
                        drawPanel.removeMouseListener(this);
                        canClickDrawPanel = true;
                        return;
                    }
                }
                flagHelpLabel.setText("No point selected, please try again.");
                flagHelpLabel.setForeground(Color.RED);
            }
        };
        drawPanel.addMouseListener(mouse);
    }

    private void drawPanelClicked(MouseEvent e) {
        Coordinate coordinate = new Coordinate(e.getX() - drawPanel.getWidth() / 2.0D, -e.getY() + drawPanel.getHeight() / 2.0D);
        for(Coordinate panelCoordinate : lineBuilder.getAvailableCoordinates()) {
            if (coordinate.distance(panelCoordinate) < 5) {
                mouseNearestCoordinate.setForeground(Color.BLACK);
                mouseNearestCoordinate.setText("" + panelCoordinate);
                return;
            }
        }
        mouseNearestCoordinate.setText("" + coordinate);
    }

    // <editor-fold defaultstate="collapsed" desc="Panels and Layout">
    private void initComponents() {
        window = new JFrame("IrregularPolygon.java");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        infoPanel();
        stepPanel();
        tabbedPane();
        buttonPanel();
        flagPanel();
        drawPanel();

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(window.getContentPane());
        window.getContentPane().setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(5, 25, Short.MAX_VALUE)
                        .addComponent(panelSwitcher)
                        .addGap(5, 25, 25)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(buttonPanel)
                                        .addComponent(flagPanel)
                        )
                        .addGap(5, 25, 25)
                        .addComponent(superDrawPanel)
                        .addGap(5, 25, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(5, 25, Short.MAX_VALUE)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(panelSwitcher)
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(buttonPanel)
                                                        .addGap(5,25,25)
                                                        .addComponent(flagPanel)
                                        )
                                        .addComponent(superDrawPanel)
                        )
                        .addGap(5, 25, Short.MAX_VALUE)
        );
        // </editor-fold>
    }

    private void infoPanel() {
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        // Set up table and scroll pane for original coordinates
        JLabel originCoordTitle = new JLabel("Original Coordinates");
        JTable originalCoordinatesTable = new JTable(new CoordinateTableModel(lineBuilder.getOriginalCoordinates()));
        originalCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane originalCoordinatesTableScrollPane = new JScrollPane(originalCoordinatesTable);

        // Set up table and scroll pane for available coordinates
        CoordinateTableModel availableCoordinatesModel = new CoordinateTableModel(lineBuilder.getAvailableCoordinates());
        JLabel availableCoordinateTitle = new JLabel("Available Coordinates");
        availableCoordinatesTable = new JTable(availableCoordinatesModel);
        availableCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane availableCoordinatesTableScrollPane = new JScrollPane(availableCoordinatesTable);

        // Set up table and scroll pane for current lines
        currentLinesModel = new LineTableModel(lineBuilder.getCreatedLines());
        JLabel currentLinesTitle = new JLabel("Current Lines");
        currentLinesTable = new JTable(currentLinesModel);
        currentLinesTable.setFillsViewportHeight(true);
        JScrollPane currentLinesTableScrollPane = new JScrollPane(currentLinesTable);

        GroupLayout layout = new GroupLayout(infoPanel);
        infoPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(originCoordTitle)
                                        .addComponent(originalCoordinatesTableScrollPane, 50, 100, 100)
                        )
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(availableCoordinateTitle)
                                        .addComponent(availableCoordinatesTableScrollPane, 50, 100, 100)
                        )
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(currentLinesTitle)
                                        .addComponent(currentLinesTableScrollPane, 100, 200, 200)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(originCoordTitle)
                                                        .addComponent(originalCoordinatesTableScrollPane, 100, 100, 100)
                                        )
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(availableCoordinateTitle)
                                                        .addComponent(availableCoordinatesTableScrollPane, 100, 100, 100)
                                        )
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(currentLinesTitle)
                                                        .addComponent(currentLinesTableScrollPane, 100, 100, 100)
                                        )
                        )
                        .addGap(10)
        );

    }

    private void stepPanel() {
        stepPanel = new JPanel();
        stepPanel.setBorder(BorderFactory.createTitledBorder("Previous Steps"));

        stepLabel = new JLabel("Steps Completed: " + 0);
        stepLastCoordinates = new JLabel("Step Coordinates: Not Available");

        // Set up table and scroll pane for last step available coordinates
        CoordinateTableModel lastStepModel = new CoordinateTableModel(new ArrayList<>());
        JLabel lastStepCoordinatesTitle = new JLabel("Last Step's Coordinates");
        lastStepAvailableCoordinatesTable = new JTable(lastStepModel);
        lastStepAvailableCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane lastStepAvailableCoordinatesTableScrollPane = new JScrollPane(lastStepAvailableCoordinatesTable);

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(stepPanel);
        stepPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(stepLabel)
                                        .addGap(10)
                                        .addGroup(
                                            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                .addComponent(lastStepCoordinatesTitle)
                                                .addComponent(lastStepAvailableCoordinatesTableScrollPane, 50, 100, 100)
                                        )
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(stepLabel)
                        .addGap(10)
                        .addComponent(lastStepCoordinatesTitle)
                        .addComponent(lastStepAvailableCoordinatesTableScrollPane, 100, 100, 100)
                        .addGap(10)
        );
        // </editor-fold>
    }

    private void tabbedPane() {
        panelSwitcher = new JTabbedPane();
        panelSwitcher.addTab("Info", infoPanel);
        panelSwitcher.addTab("Step", stepPanel);
    }

    private void buttonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton resetButton = new JButton("Reset");
        backButton.addActionListener(e -> backButtonClicked());
        nextButton.addActionListener(e -> nextButtonClicked());
        resetButton.addActionListener(e -> resetButtonClicked());

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(layout);
        layout.setHonorsVisibility(false);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(backButton)
                                                        .addGap(10)
                                                        .addComponent(nextButton)
                                        )
                                        .addComponent(resetButton)
                                        .addComponent(errorLabel)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(backButton)
                                        .addComponent(nextButton)
                        )
                        .addGap(10)
                        .addComponent(resetButton)
                        .addComponent(errorLabel)
                        .addGap(10)
        );
        // </editor-fold>
    }

    private void flagPanel() {
        flagPanel = new JPanel();
        flagPanel.setBorder(BorderFactory.createTitledBorder("Flag Options"));

        JButton flagButton = new JButton("Place Flag");
        flagButton.addActionListener(e -> flagButtonClicked());

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(flagPanel);
        flagPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(flagButton)
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(flagButton)
                        .addGap(10)
        );
        // </editor-fold>
    }

    private void drawPanel() {
        superDrawPanel = new JPanel();
        superDrawPanel.setBorder(BorderFactory.createTitledBorder("Draw"));

        drawPanel = new SketchPadPanel(0);
        pen = new Pen(drawPanel);

        drawPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if(canClickDrawPanel) drawPanelClicked(e);
            }
        });

        flagHelpLabel = new JLabel();
        mouseNearestCoordinate = new JLabel();

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(superDrawPanel);
        superDrawPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(drawPanel, 100, 300, 300)
                                        .addComponent(flagHelpLabel)
                                        .addComponent(mouseNearestCoordinate)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(drawPanel, 100, 300, 300)
                        .addComponent(flagHelpLabel)
                        .addComponent(mouseNearestCoordinate)
                        .addGap(10)
        );
        // </editor-fold>
    }
    // </editor-fold>

    public GUI(LineBuilder lb) {
        lineBuilder = lb;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Windows Classic".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            System.err.println("Look and Feel not found.");
        }
        initComponents();
        draw();

        window.pack();
        window.setVisible(true);
    }

}