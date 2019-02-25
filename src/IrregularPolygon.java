import java.awt.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Alejandro Doberenz
 * @version 2/19/2019
 *
 * An irregular polygon is different from a regular polygon (Polygon.java) because it does not have to be
 * equiangular or equilateral. This allows for the insertion of random points and the drawing of segments between
 * them.
 */
public class IrregularPolygon extends Polygon implements java.io.Serializable {

    private boolean stickToLines;

    public void addCoordinate(Coordinate coordinate) {
        for(Coordinate point : coordinates)
            if(coordinate.equals(point)) { System.out.println("Coordinate " + coordinate + " is already in Polygon at coordinates[" + coordinates.indexOf(point) + "]."); return; }
        coordinates.add(coordinate);
    }

    private void createLines() {
        for(Coordinate coordinate : coordinates) {
            Segment line = new Segment(coordinate, coordinates.get((coordinates.indexOf(coordinate) + 1) % coordinates.size()));
            lineSegments.add(line);
        }
    }
    private void update() {
        if(coordinates.size() == 0)
            if(lineSegments.size() == 0) throw new NoCoordinatesException("There are no coordinates for this Polygon.");
            else for(Segment line : lineSegments) coordinates.add(line.getStart());
        if(lineSegments.size() == 0) createLines();

    }

    public void draw(Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }
    public void draw(Pen pen, java.awt.Color color) {
        update();
        pen.up();
        pen.move(lineSegments.get(0).getStart());
        pen.down();
        pen.setWidth(1);
        pen.setColor(color);

        for(Segment line : lineSegments) {
            pen.move(line.getStart());
            pen.move(line.getEnd());
        }
    }
    public void drawPoints(Pen pen) {
        Color[] colorWheel = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
        for(Coordinate point : coordinates) {
            Color newColor = colorWheel[coordinates.indexOf(point) % colorWheel.length];
            point.draw(pen,newColor);
        }
    }

    public double perimeter() {
        update();
        double perimeter = 0;
        for(Segment s : lineSegments)
            perimeter += s.getDistance();
        return perimeter;
    }
    public double area() {
        return 0;
    }

    public static ArrayList<Coordinate> generateIrregularPolygon(int vertices) {
        if(vertices < -1)
            throw new IllegalArgumentException("Vertices cannot be less than -1.");
        if(vertices > -1 && vertices < 3)
            throw new IllegalArgumentException("Need at least 3 vertices to be a polygon.");
        int maxLength = 250;
        int minLength = -250;
        int maxWidth = 250;
        int minWidth = -250;

        if(vertices == -1) vertices = Helper.random(3, 10);

        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        for(int i = 0; i < vertices; i++)
            coordinateList.add(new Coordinate(Helper.random(minLength, maxLength), Helper.random(minWidth, maxWidth)));

        return coordinateList;
    }

    public IrregularPolygon() {
        super();
    }
    public IrregularPolygon(Coordinate... coords) {
        for(Coordinate coordinate : coords)
            addCoordinate(coordinate);
        update();
    }
    public IrregularPolygon(Segment[] lines) {
        lineSegments = new ArrayList<>(java.util.Arrays.asList(lines));
        update();
    }

    public static void main(String[] args) {
        /*
        File data = new File(".settings");
        if(!data.exists()) {
            try {
                PrintWriter writer = new PrintWriter(data);
                writer.println("serial = 0;");
                writer.close();
            } catch(FileNotFoundException x) {}
        }
        try {
            Scanner scanner = new Scanner(data);
        } catch(FileNotFoundException x) {}
        */
        /*
        // Attempt to load polygon, otherwise create a new one
        File file = new File("last.polygon");
        File file2 = new File("polygon/last.polygon");
        if(file2.exists()) {
            System.out.println("file2 exists");
        } else System.out.println("file2 does not exist");
        PolygonBuilder shape5;
        try {
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream input = new ObjectInputStream(fi);
            shape5 = (PolygonBuilder) input.readObject();
        } catch(Exception ex) {
            System.err.println(ex);
            System.err.println("Failed to load polygon.");
        }
        */

        /*
        // Initialize sketchpad and pen
        int padX = 500;
        int padY = 500;
        SketchPad sp = new SketchPad(padX,padY);
        Pen pen = new Pen(sp);

        // Attempt to load polygon, otherwise create a new one
        File file = new File("last.polygon");
        IrregularPolygon shape5;
        try {
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream input = new ObjectInputStream(fi);
            shape5 = (IrregularPolygon) input.readObject();
        } catch(Exception ex) {
            System.err.println(ex);
            System.err.println("Failed to load polygon.");
            shape5 = new IrregularPolygon(generateIrregularPolygon(4));
        }

        // Serialize
        try {
            FileOutputStream fo = new FileOutputStream(file);
            ObjectOutputStream output = new ObjectOutputStream(fo);
            output.writeObject(shape5);
            output.close();
            fo.close();
        } catch(Exception e) {}

        shape5.draw(pen, Color.BLACK, Color.BLACK,true);
        shape5.redrawLines();
        shape5.draw(pen, Color.RED, randomColor(),true);
        System.out.println("Complete");
        */
    }

}