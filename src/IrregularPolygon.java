import gpdraw.*;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Alejandro Doberenz
 * @version 2/19/2019
 *
 * This class allows you to insert points into an array list which will then be drawn.
 */
public class IrregularPolygon {

    private ArrayList<Segment> lines;
    private ArrayList<Coordinate> points;

    public void add(Coordinate point) {
        points.add(point);
    }
    public void add(Segment segment) {
        lines.add(segment);
    }

    private void update() {
        if(points.size() == 0) {
            if(lines.size() == 0) {
                throw new IllegalArgumentException("No possible points to draw from.");
            }
            else {
                generatePointsFromSegments();
            }
        }
    }

    public void draw(Pen p) {
        update();
        p.up();
        p.move(points.get(0));
        p.down();
        for(Coordinate c : points) {
            p.move(c);
        }
        p.move(points.get(0));
    }
    public void draw(Pen p, Color color) {
        update();
        p.up();
        p.move(points.get(0));
        p.down();
        p.setColor(color);
        for(Coordinate c : points) {
            p.move(c);
        }
        p.move(points.get(0));
        p.setColor(Color.BLACK);
    }

    public double perimeter() {
        update();
        double perimeter = 0;
        for(Segment s : lines)
            perimeter += s.getDistance();
        return perimeter;
    }
    public double area() {
        return 0;
    }

    private void generatePointsFromSegments() {
        if(lines.size() == 0)
            throw new IllegalArgumentException("There are no lines to generate points from.");
        if(points.size() != 0)
            throw new IllegalArgumentException("There are already some points in the array.");
        for(Segment s : lines)
            points.add(s.getStart());
    }
    private void generateSegmentsFromPoints() {
        if(points.size() == 0)
            throw new IllegalArgumentException("There are no points to generate lines from.");
        if(lines.size() != 0)
            throw new IllegalArgumentException("There are already some lines in the array.");
        for(Coordinate c : points)
            lines.add(new Segment(c, points.get((points.indexOf(c)+1)%points.size())));
    }

    public IrregularPolygon() {
        points = new ArrayList<>();
        lines = new ArrayList<>();
    }
    public IrregularPolygon(Coordinate... p) {
        points = new ArrayList<>(java.util.Arrays.asList(p));   // Initializes the coordinate array list to the vararg
        lines = new ArrayList<>();                              // Initializes the lines array list
        generateSegmentsFromPoints();                           // Populate the lines array list using the points array list
        /*
        for(int i = 0; i < points.size(); i++)
            lines.add(new Segment(points.get(i%points.size()), points.get((i+1)%points.size())));
        */
    }
    public IrregularPolygon(Segment... l) {
        points = new ArrayList<>();
        lines = new ArrayList<>(java.util.Arrays.asList(l));
        generatePointsFromSegments();
    }

    /*
    private static ArrayList<Coordinate> generatePolygon() {
        Random rng = new Random();
        int vertices = rng.nextInt(11);
        for(int i = 0; i < vertices; i++) {

        }
    }
    */

    public static void main(String[] args) {
        // Initialize sketchpad and pen
        int padX = 500;
        int padY = 500;
        SketchPad sp = new SketchPad(padX,padY);
        Pen pen = new Pen(sp);

        // Create coordinates
        Coordinate a = new Coordinate(0,0);
        Coordinate b = new Coordinate(0,100);
        Coordinate c = new Coordinate(100,100);
        Coordinate d = new Coordinate(100,0);

        // Create first shape by manually adding coordinates
        IrregularPolygon shape0 = new IrregularPolygon();
        shape0.add(a);
        shape0.add(b);
        shape0.add(c);
        shape0.add(d);

        shape0.draw(pen);

        // Create second shape by using vararg coordinate constructor
        IrregularPolygon shape1 = new IrregularPolygon(a,b,c,d);

        shape1.draw(pen, Color.RED);

        // Create line segments
        Segment ab = new Segment(a,b);
        Segment bc = new Segment(b,c);
        Segment cd = new Segment(c,d);
        Segment da = new Segment(d,a);

        System.out.println(ab.getSlope());

        // Create third shape by manually adding lines
        IrregularPolygon shape2 = new IrregularPolygon();
        shape2.add(ab);
        shape2.add(bc);
        shape2.add(cd);
        shape2.add(da);

        shape2.draw(pen, Color.BLUE);

        // Create fourth shape by using vararg segment constructor
        IrregularPolygon shape3 = new IrregularPolygon(ab,bc,cd,da);

        shape3.draw(pen, Color.GREEN);

        System.out.println("Complete.");
    }

}
