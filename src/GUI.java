import gpdraw.SketchPadPanel;

import javax.swing.*;

public class GUI {

    private JFrame window;

    private JPanel leftPanel, middlePanel, rightPanel;

    private SketchPadPanel drawPanel;

    private LineBuilder lineBuilder;

    private JLabel stepCoordinateLabel;
    private JTextArea currentLinesField;

    private Pen pen;

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
        draw();

        stepCoordinateLabel.setText("Available Coordinates: " + Helper.asString(lineBuilder.getAvailableCoordinates().toArray()));

        StringBuilder currentLinesText = new StringBuilder();
        for(Segment line : lineBuilder.getCreatedLines()) {
            currentLinesText.append(line);
            currentLinesText.append("\n");
        }
        currentLinesField.setText(currentLinesText.toString());

    }

    private void nextButtonClicked() {
        lineBuilder.step();
        update();
    }
    private void backButtonClicked() {
        drawPanel = new SketchPadPanel();
        lineBuilder.back();
        update();
    }
    private void resetButtonClicked() {
        drawPanel = new SketchPadPanel();
        lineBuilder = new LineBuilder(lineBuilder.getVerbose(), lineBuilder.getOriginalCoordinates());
        update();
    }

    private void leftPanel() {
        leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0,150,250)));

        JLabel originalCoordinateLabel = new JLabel("Original Coordinates:   " + Helper.asString(lineBuilder.getAvailableCoordinates().toArray()));
        stepCoordinateLabel = new JLabel("Available Coordinates: " + Helper.asString(lineBuilder.getAvailableCoordinates().toArray()));
        currentLinesField = new JTextArea();
        currentLinesField.setEditable(false);
        currentLinesField.setAutoscrolls(true);
        JScrollPane lineScrollPane = new JScrollPane(currentLinesField, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GroupLayout layout = new GroupLayout(leftPanel);
        leftPanel.setLayout(layout);

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

        drawPanel = new SketchPadPanel();
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
        middlePanel();
        rightPanel();

        GroupLayout layout = new GroupLayout(window.getContentPane());
        window.getContentPane().setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,Short.MAX_VALUE)
                .addComponent(leftPanel)
                .addGap(10,25,25)
                .addComponent(middlePanel)
                .addGap(10,25,25)
                .addComponent(rightPanel)
                .addGap(10,25,Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10,25,Short.MAX_VALUE)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(leftPanel)
                        .addComponent(middlePanel)
                        .addComponent(rightPanel)
                )
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
