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
 * @version 2/28/2019
 *
 * This is the GUI for the program.
 */
public class GUI {

    /*
        TODO - Add more info to infoPanel
        TODO - Create origin point changer/selector
        FIXME - Weird bug when clicking next a bunch and then back a bunch will result in a ArrayIndexOutOfBounds
        ^ May have to do with step() successfully completing when there are no lines to draw
     */

    private JFrame window;

    private JPanel stepPanel, infoPanel, buttonPanel, superDrawPanel;

    private SketchPadPanel drawPanel;

    private LineBuilder lineBuilder;

    private JLabel stepCoordinateLabel, stepLabel, stepLastCoordinates, errorLabel;
    private JTextArea currentLinesField;

    private Pen pen;

    private int numOfSteps;

    private double mouseX, mouseY;

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
                coordinate.draw(pen, Color.BLACK);
            for(Segment line : lineBuilder.getCreatedLines())
                line.draw(pen);
        });
        drawThread.start();
    }

    private void clearDrawPanel() {
        pen.setColor(Color.WHITE);
        drawPanel.fillRect(0,0, drawPanel.getWidth(), drawPanel.getHeight());
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

    }
    private void nextButtonClicked() {
        lineBuilder.step();
        switch(lineBuilder.getSteps().get(lineBuilder.getSteps().size()-1).getStepResult()) {
            case SUCCESSFUL:
                numOfSteps++;
                break;
            case FAILED:
                break;
        }
        update();
    }
    private void backButtonClicked() {
        try {
            lineBuilder.back();
            numOfSteps--;
            clearDrawPanel();
        } catch(LineBuilder.CannotGoBackException e) {
            errorLabel.setText("Cannot go back.");
        }
        update();
    }
    private void resetButtonClicked() {
        errorLabel.setText("");
        numOfSteps = 0;
        clearDrawPanel();
        lineBuilder = new LineBuilder(lineBuilder.getOriginalCoordinates());
        update();
    }
    // FIXME - NullPointerException when there is no last line
    private void flagButtonClicked() {

        lineBuilder.getFlags().add(new LineBuilder.Flag(lineBuilder.getLastLine().getEnd()));
    }

    private void stepPanel() {
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
                    .addComponent(lineScrollPane, 25, 250, 500)
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
    private void buttonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton resetButton = new JButton("Reset");
        JButton flagButton = new JButton("Place Flag");
        backButton.addActionListener(e -> backButtonClicked());
        nextButton.addActionListener(e -> nextButtonClicked());
        resetButton.addActionListener(e -> resetButtonClicked());
        flagButton.addActionListener(e -> flagButtonClicked());
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        GroupLayout layout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(layout);

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
                        .addGroup(
                            layout.createSequentialGroup()
                                .addComponent(resetButton)
                                .addGap(10)
                                .addComponent(flagButton)
                        )
                        .addComponent(errorLabel)
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
                .addGroup(
                    layout.createParallelGroup()
                        .addComponent(resetButton)
                        .addComponent(flagButton)
                )
                .addComponent(errorLabel)
                .addGap(10,25,25)
        );
    }
    private void drawPanel() {
        superDrawPanel = new JPanel();
        superDrawPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        drawPanel = new SketchPadPanel(0);
        pen = new Pen(drawPanel);

        // TODO - Move this to when selecting the origin point or whenever needed, useless as is right now. But it looks cool.
        drawPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println(new Coordinate(-drawPanel.getWidth()/2 + e.getX(), drawPanel.getHeight()/2 - e.getY()));
            }
        });

        GroupLayout layout = new GroupLayout(superDrawPanel);
        superDrawPanel.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(drawPanel, 100, 200, 300)
                .addGap(10)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(drawPanel, 100, 200, 300)
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
                                .addComponent(buttonPanel)
                                .addGap(10,25,25)
                                .addComponent(superDrawPanel)
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
                        .addComponent(buttonPanel)
                        .addComponent(superDrawPanel)
                )
                .addGap(10)
                .addComponent(stepPanel)
                .addGap(10,25,Short.MAX_VALUE)
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
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
                System.err.println("Look and Feel not found.");
            }


            initComponents();

        draw();

        window.pack();
        window.setVisible(true);
    }

}
