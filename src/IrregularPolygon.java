import gpdraw.*;

import java.awt.Color;

import java.util.ArrayList;

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
    }
    public IrregularPolygon(ArrayList<Coordinate> c) {
        points = c;
        lines = new ArrayList<>();
        generateSegmentsFromPoints();
    }
    public IrregularPolygon(Segment... l) {
        points = new ArrayList<>();
        lines = new ArrayList<>(java.util.Arrays.asList(l));
        generatePointsFromSegments();
    }

    @Deprecated private static void recurse(int remaining, ArrayList<Coordinate> coordinateList) {
        if(remaining == 0) {
            //return coordinateList;
        }
        for(Coordinate start : coordinateList) {
            Coordinate next = coordinateList.get(1);
            coordinateList.remove(0);
            Segment temp = new Segment(next, start);
        }
    }

    private static ArrayList<Coordinate> generatePolygon() {
        double maxLength = 100;
        double minLength = -100;
        double maxWidth = 100;
        double minWidth = -100;

        int vertices = (int) (Math.random() * (10 - 3) + 3);
        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        for(int i = 0; i < vertices; i++)
            coordinateList.add(new Coordinate(Math.random() * (maxLength - minLength) + minLength, Math.random() * (maxWidth - minWidth) + minWidth));

        Coordinate start = coordinateList.get(0);

        return new ArrayList<>();
    }

    public static void main(String[] args) {
        // Initialize sketchpad and pen
        int padX = 500;
        int padY = 500;
        SketchPad sp = new SketchPad(padX,padY);
        Pen pen = new Pen(sp);

        Coordinate a = new Coordinate(0,-4);
        Coordinate b = new Coordinate(6,8);
        Segment ab = new Segment(a,b);
        Coordinate c = new Coordinate(0,0);
        Coordinate d = new Coordinate(6,6);
        Segment cd = new Segment(c,d);
        ab.draw(pen);
        cd.draw(pen);
        System.out.println(ab.intersect(cd));
        /*

        // Create coordinates
        Coordinate a = new Coordinate(0,0);
        Coordinate b = new Coordinate(30,41);
        Coordinate c = new Coordinate(15,87);
        Coordinate d = new Coordinate(60,83);
        Coordinate e = new Coordinate(67,41);

        // Create first shape by manually adding coordinates
        IrregularPolygon shape0 = new IrregularPolygon();
        shape0.add(a);
        shape0.add(b);
        shape0.add(c);
        shape0.add(d);
        shape0.add(e);

        shape0.draw(pen);

        // Create second shape by using vararg coordinate constructor
        IrregularPolygon shape1 = new IrregularPolygon(a,b,c,d,e);

        shape1.draw(pen, Color.RED);

        // Create line segments
        Segment ab = new Segment(a,b);
        Segment bc = new Segment(b,c);
        Segment cd = new Segment(c,d);
        Segment de = new Segment(d,e);
        Segment ea = new Segment(e,a);

        // Create third shape by manually adding lines
        IrregularPolygon shape2 = new IrregularPolygon();
        shape2.add(ab);
        shape2.add(bc);
        shape2.add(cd);
        shape2.add(de);
        shape2.add(ea);

        shape2.draw(pen, Color.BLUE);

        // Create fourth shape by using vararg segment constructor
        IrregularPolygon shape3 = new IrregularPolygon(ab,bc,cd,de,ea);

        shape3.draw(pen, Color.GREEN);

        IrregularPolygon shape4 = new IrregularPolygon(generatePolygon());
        shape4.draw(pen, Color.CYAN);

        System.out.println("Complete.");
        */
    }

}
