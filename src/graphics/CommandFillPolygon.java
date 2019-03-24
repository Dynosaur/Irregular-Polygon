package graphics;

import gpdraw.DrawingToolCommand;

import java.awt.Graphics2D;

import java.util.ArrayList;

import geometry.Coordinate;

public class CommandFillPolygon extends DrawingToolCommand {

    private int[] xCoordinates, yCoordinates;

    private int size;

    @Override public void doCommand(Graphics2D graphics2D, int i, int i1) {
        graphics2D.fillPolygon(xCoordinates, yCoordinates, size);
    }

    public CommandFillPolygon(ArrayList<Coordinate> coordinates) {
        size = coordinates.size();
        xCoordinates = new int[size];
        yCoordinates = new int[size];
        for(Coordinate coordinate : coordinates) {
            int i = coordinates.indexOf(coordinate);
            xCoordinates[i] = (int) coordinate.getX();
            yCoordinates[i] = (int) coordinate.getY();
        }
    }
}
