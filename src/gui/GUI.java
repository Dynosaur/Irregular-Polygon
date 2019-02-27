package gui;

import gpdraw.SketchPadPanel;

import javax.swing.*;

import geometry.LineBuilder;
import geometry.Coordinate;
import geometry.Segment;

public class GUI {

    private JFrame window;

    private JPanel stepPanel, infoPanel, middlePanel, rightPanel;

    private SketchPadPanel drawPanel;

    private LineBuilder lineBuilder;

    private JLabel stepCoordinateLabel, stepLabel, stepLastCoordinates;
    private JTextArea currentLinesField;

    private Pen pen;

    private int numOfSteps;

    public static String asString(Object[] array) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < array.length; i++)
            if(array.length==1) builder.append("[").append(array[i]).append("]");
            else if(i == 0) builder.append("[").append(array[i]).append(", ");
            else if (i == array.length - 1) builder.append(array[i]).append("]");
            else builder.append(array[i]).append(", ");
        return builder.toString();
    }

    private void draw() {
        Thread drawThread = new Thread(() -> {
            for(Coordinate coordinate : lineBuilder.getAvailableCoordinates())
                coordinate.draw(pen);
            for(Segment line : lineBuilder.getCreatedLines())
                line.draw(pen);
        });
        drawThread.start();
    }

    private void update() {
        stepLabel.setText("Steps Completed: " + numOfSteps);
        if(numOfSteps == 0) {
            stepLastCoordinates.setText("Step Coordinates: Not Available");
        } else {
            LineBuilder.Step lastStep = lineBuilder.getSteps().get(lineBuilder.getSteps().size()-1);
            stepLastCoordinates.setText("Step Coordinates: " + asString(lastStep.getAvailableCoordinates().toArray()));
        }

        draw();

        stepCoordinateLabel.setText("Available Coordinates: " + asString(lineBuilder.getAvailableCoordinates().toArray()));

        StringBuilder currentLinesText = new StringBuilder();
        for(Segment line : lineBuilder.getCreatedLines()) {
            currentLinesText.append(line);
            currentLinesText.append("\n");
        }
        currentLinesField.setText(currentLinesText.toString());
        currentLinesField.setText("");

    }

    private void nextButtonClicked() {
        try {
            lineBuilder.step();
            numOfSteps++;
        } catch(LineBuilder.CannotGoBackException e) {}
        update();
    }
    private void backButtonClicked() {
        try {
            lineBuilder.back();
            numOfSteps--;
        } catch(LineBuilder.CannotGoBackException e) {}
        update();
    }
    private void resetButtonClicked() {
        numOfSteps = 0;
        drawPanel.getParent().paint(drawPanel.getGraphics());
        drawPanel.paint(drawPanel.getGraphics());
        drawPanel = null;
        drawPanel = new SketchPadPanel();
        lineBuilder = new LineBuilder(lineBuilder.getVerbose(), lineBuilder.getOriginalCoordinates());
        update();
    }

    private void leftPanel() {
        stepPanel = new JPanel();
        stepPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        stepLabel = new JLabel("Steps Completed: " + 0);
        stepLastCoordinates = new JLabel("Step Coordinates: Not Available");

        GroupLayout layout = new GroupLayout(stepPanel);
        stepPanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addGroup(
                    layout.createParallelGroup()
                        .addComponent(stepLabel)
                        .addComponent(stepLastCoordinates)
                )
                .addGap(10,25,25)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addComponent(stepLabel)
                .addGap(10)
                .addComponent(stepLastCoordinates)
                .addGap(10,25,25)
        );
    }
    private void infoPanel() {
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        JLabel originalCoordinateLabel = new JLabel("Original Coordinates:   " + asString(lineBuilder.getAvailableCoordinates().toArray()));
        stepCoordinateLabel = new JLabel("Available Coordinates: " + asString(lineBuilder.getAvailableCoordinates().toArray()));
        currentLinesField = new JTextArea();
        currentLinesField.setEditable(false);
        currentLinesField.setAutoscrolls(true);
        JScrollPane lineScrollPane = new JScrollPane(currentLinesField, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GroupLayout layout = new GroupLayout(infoPanel);
        infoPanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addGroup(layout.createParallelGroup()
                    .addComponent(originalCoordinateLabel)
                    .addComponent(stepCoordinateLabel)
                    .addComponent(lineScrollPane, 25, 250, 250)
                )
                .addGap(10,25,25)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addComponent(originalCoordinateLabel)
                .addComponent(stepCoordinateLabel)
                .addGap(10)
                .addComponent(lineScrollPane, 50, 50, 100)
                .addGap(10,25,25)
        );

    }
    private void middlePanel() {
        middlePanel = new JPanel();
        middlePanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton resetButton = new JButton("Reset");
        backButton.addActionListener(e -> backButtonClicked());
        nextButton.addActionListener(e -> nextButtonClicked());
        resetButton.addActionListener(e -> resetButtonClicked());

        GroupLayout layout = new GroupLayout(middlePanel);
        middlePanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(
                            layout.createSequentialGroup()
                                .addComponent(backButton)
                                .addGap(10)
                                .addComponent(nextButton)
                        )
                        .addComponent(resetButton)
                )
                .addGap(10,25,25)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,25)
                .addGroup(
                    layout.createParallelGroup()
                        .addComponent(backButton)
                        .addComponent(nextButton)
                )
                .addGap(10)
                .addComponent(resetButton)
                .addGap(10,25,25)
        );
    }
    private void rightPanel() {
        rightPanel = new JPanel();
        rightPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        drawPanel = new SketchPadPanel(0);
        JScrollPane lineScrollPane = new JScrollPane(drawPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        pen = new Pen(drawPanel);

        GroupLayout layout = new GroupLayout(rightPanel);
        rightPanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(lineScrollPane, 100, 200, 300)
                .addGap(10)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(lineScrollPane, 100, 200, 300)
                .addGap(10)
        );
    }
    private void initComponents() {
        window = new JFrame("IrregularPolygon.java");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        leftPanel();
        infoPanel();
        middlePanel();
        rightPanel();

        GroupLayout layout = new GroupLayout(window.getContentPane());
        window.getContentPane().setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,Short.MAX_VALUE)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(
                            layout.createSequentialGroup()
                                .addComponent(infoPanel)
                                .addGap(10,25,25)
                                .addComponent(middlePanel)
                                .addGap(10,25,25)
                                .addComponent(rightPanel)
                        )
                    .addComponent(stepPanel)
                )
                .addGap(10,25,Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,Short.MAX_VALUE)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(infoPanel)
                        .addComponent(middlePanel)
                        .addComponent(rightPanel)
                )
                .addComponent(stepPanel)
                .addGap(10,25,Short.MAX_VALUE)
        );
    }

    public GUI(LineBuilder lb) {
        lineBuilder = lb;

        initComponents();

        draw();

        window.pack();
        window.setVisible(true);
    }

}
