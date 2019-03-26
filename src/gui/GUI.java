package gui;

import graphics.CustomPanel;

import javax.swing.*;

import geometry.LineBuilder;

import geometry.Coordinate;

import geometry.Segment;

import java.awt.Color;

import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;

import java.io.File;

import java.io.IOException;

import java.io.PrintWriter;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 3/17/2019
 *
 * This is the GUI for the program. It can display all the current lines drawn, original given coordinates, available
 * coordinates, and place flags for the editor.
 */
public class GUI {

    /*
        TODO - Create origin point changer/selector
        TODO - Draw projections where the line builder will go
     */

    // <editor-fold defaultstate="collapsed" desc="Variables">
    private LineBuilder lineBuilder;

    private JFrame window;

    private JTabbedPane panelSwitcher;

    private JPanel stepPanel, infoPanel, buttonPanel, superDrawPanel, createFlagPanel, currentFlagPanel;

    private JTable availableCoordinatesTable, currentLinesTable, lastStepAvailableCoordinatesTable, currentFlagsTable;

    private JLabel stepLabel, errorLabel, flagHelpLabel, mouseCoordinateLabel;

    private CustomPanel drawPanel;

    private Pen pen;

    private int numOfSteps;
    // </editor-fold>

    private void draw() {
        clearDrawPanel();
        try {
            if(lineBuilder.getLastStep().getStepResult() != LineBuilder.Step.StepResult.COMPLETE && lineBuilder.getLastStep().getStepResult() != LineBuilder.Step.StepResult.NO_MORE_LINES) {
                Segment futureLine = LineBuilder.findNextBestLine(lineBuilder.getAvailableCoordinates());
                futureLine.draw(pen, new Color(0,0,0, 100));
            }
        } catch(LineBuilder.CannotGoBackException e) {
            Segment futureLine = LineBuilder.findNextBestLine(lineBuilder.getAvailableCoordinates());
            futureLine.draw(pen, new Color(0,0,0,100));
        }
        for(Coordinate coordinate : lineBuilder.getAvailableCoordinates())
            coordinate.draw(pen, Color.BLACK);
        for(Segment line : lineBuilder.getLinesList())
            line.draw(pen, Color.BLACK);
        for(LineBuilder.Flag flag : lineBuilder.getFlags())
            flag.getCoordinate().draw(pen, Color.RED);
        lineBuilder.getOriginPoint().draw(pen, new Color(255,200,0));
    }

    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    private void clearHelpText() {
        errorLabel.setText("");
        errorLabel.setForeground(Color.RED);
        flagHelpLabel.setText("");
        mouseCoordinateLabel.setText("");
    }

    private void clearDrawPanel() {
        drawPanel.setColor(Color.WHITE);
        drawPanel.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
    }

    private void update() {
        stepLabel.setText("Steps Completed: " + lineBuilder.getSteps().size());

        draw();

        availableCoordinatesTable.setModel(new CoordinateTableModel(lineBuilder.getAvailableCoordinates()));
        availableCoordinatesTable.repaint();
        currentLinesTable.setModel(new LineTableModel(lineBuilder.getLinesList()));
        currentLinesTable.repaint();
        currentFlagsTable.setModel(new CoordinateTableModel(lineBuilder.getFlaggedCoordinates()));
        currentFlagsTable.repaint();
        try {
            lastStepAvailableCoordinatesTable.setModel(new CoordinateTableModel(lineBuilder.getLastStep().getAvailableCoordinates()));
        } catch(LineBuilder.CannotGoBackException e) {
            lastStepAvailableCoordinatesTable.setModel(new CoordinateTableModel(new ArrayList<>()));
        }
    }

    private void nextButtonClicked() {
        clearHelpText();
        lineBuilder.improvedStep();
        try {
            switch(lineBuilder.getLastStep().getStepResult()) {
                case SUCCESSFUL:
                    numOfSteps++;
                    for(LineBuilder.Flag f : lineBuilder.getFlags())
                        lineBuilder.getAvailableCoordinates().add(f.getCoordinate());
                    break;
                case FAILED:
                    System.out.println(lineBuilder.getAvailableCoordinates());
                    lineBuilder.getSteps().remove(lineBuilder.getLastStep());
                    System.out.println(lineBuilder.getAvailableCoordinates());
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setText("Step failed.");
                    break;
                case NO_MORE_LINES:
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setText("No more lines can be drawn.");
                    //lineBuilder.getSteps().remove(lineBuilder.getLastStep());
                    break;
                case COMPLETE:
                    numOfSteps++;
                    errorLabel.setForeground(new Color(100,200,0));
                    errorLabel.setText("Polygon complete!");
                    break;
            }
        } catch(LineBuilder.CannotGoBackException e) {}
        update();
    }

    private void backButtonClicked() {
        clearHelpText();
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
        clearHelpText();
        numOfSteps = 0;
        clearDrawPanel();
        lineBuilder = new LineBuilder(lineBuilder.getOriginalCoordinates());
        update();
    }

    private void flagButtonClicked() {
        flagHelpLabel.setText("Pick a point on the graph to flag.");
        flagHelpLabel.setForeground(new Color(0, 150, 250));
        MouseAdapter mouse = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Coordinate coordinate = new Coordinate(e.getX() - drawPanel.getWidth() / 2.0D, -e.getY() + drawPanel.getHeight() / 2.0D);
                for(Coordinate panelCoordinate : lineBuilder.getAvailableCoordinates()) {
                    if(coordinate.distance(panelCoordinate) < 5) {
                        if(panelCoordinate.equals(lineBuilder.getOriginPoint())) {
                            flagHelpLabel.setForeground(Color.RED);
                            flagHelpLabel.setText("Cannot flag the origin point.");
                            drawPanel.removeMouseListener(this);
                            return;
                        }
                        lineBuilder.getFlags().add(new LineBuilder.Flag(panelCoordinate));
                        flagHelpLabel.setText("Added Flag at " + panelCoordinate);
                        flagHelpLabel.setForeground(new Color(100, 200, 0));
                        update();
                        drawPanel.removeMouseListener(this);
                        return;
                    }
                }
                flagHelpLabel.setText("No point selected, please try again.");
                flagHelpLabel.setForeground(Color.RED);
                drawPanel.removeMouseListener(this);
            }
        };
        drawPanel.addMouseListener(mouse);
    }

    private void exportButtonClicked() {
        File file = new File("system" + ".txt");
        try {
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            for(Coordinate coordinate : lineBuilder.getOriginalCoordinates())
                writer.println(coordinate);
            writer.close();
        } catch (IOException e) {}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Panels and Layout">
    private void initComponents() {
        window = new JFrame("IrregularPolygon.java");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        infoPanel();
        stepPanel();
        flagDisplayPanel();
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
                                        .addComponent(createFlagPanel)
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
                                                        .addComponent(createFlagPanel)
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
        LineTableModel currentLinesModel = new LineTableModel(lineBuilder.getLinesList());
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

    private void flagDisplayPanel() {
        currentFlagPanel = new JPanel();
        currentFlagPanel.setBorder(BorderFactory.createTitledBorder("Current Flagged Coordinates"));

        // Set up table and scroll pane for current flags
        CoordinateTableModel currentFlagModel = new CoordinateTableModel(new ArrayList<>());
        JLabel currentFlagsCoordinatesTitle = new JLabel("Current Flags");
        currentFlagsTable = new JTable(currentFlagModel);
        currentFlagsTable.setFillsViewportHeight(true);
        JScrollPane currentFlagsTableScrollPane = new JScrollPane(currentFlagsTable);

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(currentFlagPanel);
        currentFlagPanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(currentFlagsCoordinatesTitle)
                        .addComponent(currentFlagsTableScrollPane)
                )
                .addGap(10)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(currentFlagsCoordinatesTitle)
                .addComponent(currentFlagsTableScrollPane)
                .addGap(10)
        );
        // </editor-fold>
    }

    private void tabbedPane() {
        panelSwitcher = new JTabbedPane();
        panelSwitcher.addTab("Info", infoPanel);
        panelSwitcher.addTab("Step", stepPanel);
        panelSwitcher.addTab("Flags", currentFlagPanel);
    }

    private void buttonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton resetButton = new JButton("Reset");
        JButton exportButton = new JButton("Export");
        backButton.addActionListener(e -> backButtonClicked());
        nextButton.addActionListener(e -> nextButtonClicked());
        resetButton.addActionListener(e -> resetButtonClicked());
        exportButton.addActionListener(e -> exportButtonClicked());

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
                                        .addGroup(
                                            layout.createSequentialGroup()
                                                .addComponent(resetButton)
                                                .addGap(10)
                                                .addComponent(exportButton)
                                        )
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
                        .addGroup(
                            layout.createParallelGroup()
                                .addComponent(resetButton)
                                .addComponent(exportButton)
                        )
                        .addComponent(errorLabel)
                        .addGap(10)
        );
        // </editor-fold>
    }

    private void flagPanel() {
        createFlagPanel = new JPanel();
        createFlagPanel.setBorder(BorderFactory.createTitledBorder("Flag Options"));

        JButton flagButton = new JButton("Place Flag");
        flagButton.addActionListener(e -> flagButtonClicked());

        // <editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(createFlagPanel);
        createFlagPanel.setLayout(layout);

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

        drawPanel = new CustomPanel(1);
        pen = new Pen(drawPanel);

        flagHelpLabel = new JLabel();
        mouseCoordinateLabel = new JLabel();

        drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                Coordinate mousePosition = new Coordinate(e.getX() - drawPanel.getWidth() / 2.0D, -e.getY() + drawPanel.getHeight() / 2.0D);
                for(Coordinate realCoordinate : lineBuilder.getAvailableCoordinates()) {
                    if (mousePosition.distance(realCoordinate) < 7) {
                        mouseCoordinateLabel.setText("X: " + realCoordinate.getX() + " Y: " + realCoordinate.getY());
                        return;
                    }
                }
                mouseCoordinateLabel.setText("X: " + mousePosition.getX() + " Y: " + mousePosition.getY());
            }
        });

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
                                        .addComponent(mouseCoordinateLabel)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(drawPanel, 100, 300, 300)
                        .addComponent(flagHelpLabel)
                        .addComponent(mouseCoordinateLabel)
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