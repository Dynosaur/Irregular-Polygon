package gui;

import gpdraw.SketchPadPanel;

import javax.swing.*;

import geometry.LineBuilder;
import geometry.Coordinate;
import geometry.Segment;

import java.awt.*;
import java.awt.event.*;

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
        TODO - Find out how to refresh JTables, they won't change after data is changed
     */

    private Color lightBlue = new Color(0, 150, 250);
    private Color lightGreen = new Color(100, 200, 0);
    private String[] coordinateColumnNames = {"X", "Y"};
    private String[] lineColumnNames = {"Start X", "Start Y", "End X", "End Y"};

    private JFrame window;

    private JTabbedPane panelSwitcher;

    private JPanel stepPanel, infoPanel, buttonPanel, superDrawPanel, flagPanel;

    private JTable availableCoordinatesTable, currentLinesTable, lastStepAvailableCoordinatesTable;

    private SketchPadPanel drawPanel;

    private LineBuilder lineBuilder;

    private JLabel stepLabel, stepLastCoordinates, errorLabel, flagHelpLabel;


    private Pen pen;

    private int numOfSteps;

    private double mouseX, mouseY;

    public static String asString(Object[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
            if (array.length == 1) builder.append("[").append(array[i]).append("]");
            else if (i == 0) builder.append("[").append(array[i]).append(", ");
            else if (i == array.length - 1) builder.append(array[i]).append("]");
            else builder.append(array[i]).append(", ");
        return builder.toString();
    }

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
    }

    private void clearDrawPanel() {
        pen.setColor(Color.WHITE);
        drawPanel.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
    }

    private void update() {
        stepLabel.setText("Steps Completed: " + numOfSteps);
        if (numOfSteps == 0) {
            stepLastCoordinates.setText("Step Coordinates: Not Available");
        } else {
            LineBuilder.Step lastStep = lineBuilder.getSteps().get(lineBuilder.getSteps().size() - 1);
            stepLastCoordinates.setText("Step Coordinates: " + lastStep.getAvailableCoordinates());
        }

        draw();


        availableCoordinatesTable = new JTable(formatCoordinatesForTable(lineBuilder.getAvailableCoordinates()), coordinateColumnNames);
        currentLinesTable = new JTable(formatLinesForTable(lineBuilder.getCreatedLines()), lineColumnNames);

    }

    private void nextButtonClicked() {
        onChange();
        lineBuilder.step();
        switch (lineBuilder.getSteps().get(lineBuilder.getSteps().size() - 1).getStepResult()) {
            case SUCCESSFUL:
                numOfSteps++;
                break;
            case FAILED:
                lineBuilder.getSteps().remove(lineBuilder.getSteps().size() - 1);
                break;
            case NOMORELINES:
                errorLabel.setText("No more lines can be drawn.");
                lineBuilder.getSteps().remove(lineBuilder.getSteps().size() - 1);
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
        flagHelpLabel.setText("Pick a point on the graph to flag.");
        flagHelpLabel.setForeground(lightBlue);
        MouseAdapter mouse = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Coordinate coordinate = new Coordinate(e.getX() - drawPanel.getWidth() / 2.0D, -e.getY() + drawPanel.getHeight() / 2.0D);
                for (Coordinate panelCoordinate : lineBuilder.getAvailableCoordinates()) {
                    if (coordinate.distance(panelCoordinate) < 5) {
                        lineBuilder.getFlags().add(new LineBuilder.Flag(panelCoordinate));
                        flagHelpLabel.setText("Added Flag at " + panelCoordinate);
                        flagHelpLabel.setForeground(lightGreen);
                        draw();
                        drawPanel.removeMouseListener(this);
                        return;
                    }
                }
                flagHelpLabel.setText("No point selected, please try again.");
                flagHelpLabel.setForeground(Color.RED);
            }
        };
        drawPanel.addMouseListener(mouse);
    }

    private Object[][] formatCoordinatesForTable(java.util.ArrayList<Coordinate> coordinateList) {
        Object[][] coordinateArray = new Object[coordinateList.size()][2];
        for (int i = 0; i < coordinateList.size(); i++) {
            coordinateArray[i][0] = coordinateList.get(i).getX();
            coordinateArray[i][1] = coordinateList.get(i).getY();
        }
        return coordinateArray;
    }

    private Object[][] formatLinesForTable(java.util.ArrayList<Segment> lineList) {
        Object[][] lineArray = new Object[lineList.size()][4];
        for (int i = 0; i < lineList.size(); i++) {
            lineArray[i][0] = lineList.get(i).getStart().getX();
            lineArray[i][1] = lineList.get(i).getStart().getY();
            lineArray[i][2] = lineList.get(i).getEnd().getX();
            lineArray[i][3] = lineList.get(i).getEnd().getY();
        }
        return lineArray;
    }

    private void tabbedPane() {
        panelSwitcher = new JTabbedPane();
        panelSwitcher.addTab("Info", infoPanel);
        panelSwitcher.addTab("Step", stepPanel);
    }

    private void stepPanel() {
        stepPanel = new JPanel();
        stepPanel.setBorder(BorderFactory.createTitledBorder("Previous Steps"));

        stepLabel = new JLabel("Steps Completed: " + 0);
        stepLastCoordinates = new JLabel("Step Coordinates: Not Available");

        // Set up table and scroll pane for last step available coordinates
        JLabel lastStepTitle = new JLabel("Last Step's Available Coordinates");
        try {
            lastStepAvailableCoordinatesTable = new JTable(formatCoordinatesForTable(lineBuilder.getSteps().get(lineBuilder.getSteps().size() - 1).getAvailableCoordinates()), coordinateColumnNames);
        } catch(ArrayIndexOutOfBoundsException e) {
            lastStepAvailableCoordinatesTable = new JTable(formatCoordinatesForTable(lineBuilder.getAvailableCoordinates()), coordinateColumnNames);
        }
        lastStepAvailableCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane lastStepAvailableCoordinatesTableScrollPane = new JScrollPane(lastStepAvailableCoordinatesTable);

        GroupLayout layout = new GroupLayout(stepPanel);
        stepPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                            layout.createParallelGroup()
                                .addComponent(stepLabel)
                                .addComponent(lastStepAvailableCoordinatesTableScrollPane, 50, 100, 100)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(stepLabel)
                        .addComponent(lastStepAvailableCoordinatesTableScrollPane, 100, 100, 100)
                        .addGap(10)
        );
    }

    private void infoPanel() {
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        // Set up table and scroll pane for original coordinates
        JLabel originCoordTitle = new JLabel("Original Coordinates");
        JTable originalCoordinatesTable = new JTable(formatCoordinatesForTable(lineBuilder.getOriginalCoordinates()), coordinateColumnNames);
        originalCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane originalCoordinatesTableScrollPane = new JScrollPane(originalCoordinatesTable);

        // Set up table and scroll pane for available coordinates
        JLabel availableCoordTitle = new JLabel("Available Coordinates");
        availableCoordinatesTable = new JTable(formatCoordinatesForTable(lineBuilder.getAvailableCoordinates()), coordinateColumnNames);
        availableCoordinatesTable.setFillsViewportHeight(true);
        JScrollPane availableCoordinatesTableScrollPane = new JScrollPane(availableCoordinatesTable);

        // Set up table and scroll pane for current lines
        JLabel currentLinesTitle = new JLabel("Current Lines");
        currentLinesTable = new JTable(formatLinesForTable(lineBuilder.getCreatedLines()), lineColumnNames);
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
                                        .addComponent(availableCoordTitle)
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
                                                        .addComponent(availableCoordTitle)
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

    private void buttonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createLineBorder(lightBlue));

        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton resetButton = new JButton("Reset");
        backButton.addActionListener(e -> backButtonClicked());
        nextButton.addActionListener(e -> nextButtonClicked());
        resetButton.addActionListener(e -> resetButtonClicked());
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        GroupLayout layout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10, 25, 25)
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
                        .addGap(10, 25, 25)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10, 25, 25)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(backButton)
                                        .addComponent(nextButton)
                        )
                        .addGap(10)
                        .addComponent(resetButton)
                        .addGap(10)
                        .addComponent(errorLabel)
                        .addGap(10, 25, 25)
        );
    }

    private void drawPanel() {
        superDrawPanel = new JPanel();
        superDrawPanel.setBorder(BorderFactory.createLineBorder(lightBlue));

        drawPanel = new SketchPadPanel(0);
        pen = new Pen(drawPanel);

        flagHelpLabel = new JLabel();

        GroupLayout layout = new GroupLayout(superDrawPanel);
        superDrawPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(drawPanel, 100, 200, 300)
                                        .addComponent(flagHelpLabel)
                        )
                        .addGap(10)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(10)
                        .addComponent(drawPanel, 100, 200, 300)
                        .addGap(10)
                        .addComponent(flagHelpLabel)
                        .addGap(10)
        );
    }

    private void flagPanel() {
        flagPanel = new JPanel();
        flagPanel.setBorder(BorderFactory.createLineBorder(lightBlue));

        JButton flagButton = new JButton("Place Flag");
        flagButton.addActionListener(e -> flagButtonClicked());

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

    }

    private void initComponents() {
        window = new JFrame("IrregularPolygon.java");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        stepPanel();
        infoPanel();
        buttonPanel();
        drawPanel();
        flagPanel();
        tabbedPane();

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
                        .addGap(10, 25, Short.MAX_VALUE)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(panelSwitcher)
                                        .addGroup(
                                            layout.createSequentialGroup()
                                                .addComponent(buttonPanel)
                                                .addComponent(flagPanel)
                                        )
                                        .addComponent(superDrawPanel)
                        )
                        .addGap(10, 25, Short.MAX_VALUE)
        );
    }

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