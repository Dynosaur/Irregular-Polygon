import gpdraw.*;

import java.awt.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Alejandro Doberenz
 * @version 2/19/2019
 *
 * This class allows you to insert points into an array list which will then be drawn.
 */
public class IrregularPolygon implements java.io.Serializable {

    private boolean stickToLines;

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

    public void draw(Pen p, Color lineColor, Color pointColor, boolean drawPoints) {
        update();
        p.up();
        p.move(points.get(0));
        p.down();
        p.setColor(lineColor);
        for(Coordinate c : points) {
            p.move(c);
            if(drawPoints) c.draw(p, pointColor);
            p.setColor(lineColor);
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

    private void redrawLines() {
        update();
        ArrayList<Coordinate> points = this.points;
        ArrayList<Segment> lines = new ArrayList<>();
        Coordinate start = points.get(0);
        System.out.println("Start:\t\t" + start);
        while(!points.isEmpty()) {
            System.out.print("Initial[" + points.size() + "]:\t");
            print(points.toArray());
            if(points.size() == 1) {
                System.out.println("Added:\t" + start);
                points.add(start);
                System.out.print("Initial[" + points.size() + "]:\t");
                print(points.toArray());
            }
            Segment line = findBestSegment(points);
            System.out.println("Best line:\t" + line);
            for(Segment s : lines) {
                if(line.doesIntersect(s)) {
                    System.out.println("INTERSECT");
                }
            }
            lines.add(line);
            System.out.print("Lines[" + lines.size() + "]:\t");
            print(lines.toArray());
            System.out.println("Removed:\t" + line.getStart());
            System.out.println("Removed:\t" + line.getEnd());
            points.remove(line.getStart());
            points.remove(line.getEnd());
            System.out.print("After[" + points.size() + "]:\t");
            print(points.toArray());
            System.out.println();
        }
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
    public IrregularPolygon(boolean sticky, Segment... l) {
        stickToLines = sticky;
        points = new ArrayList<>();
        lines = new ArrayList<>(java.util.Arrays.asList(l));
        generatePointsFromSegments();
    }

    public static Color randomColor() {
        Random rng = new Random();
        return new Color(rng.nextInt(255),rng.nextInt(255),rng.nextInt(255));
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

    public static double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();
        java.math.BigDecimal temp = new java.math.BigDecimal(Double.toString(value));
        temp = temp.setScale(places, java.math.RoundingMode.HALF_UP);
        return temp.doubleValue();
    }
    public static double random(double min, double max) {
        return round((Math.random() * (max - min) + min), 3);
    }
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
    public static void print(Object[] array) {
        for(int i = 0; i < array.length; i++) {
            if(array.length==1) System.out.println("[" + array[i] + "]");
            else if(i == 0) System.out.print("[" + array[i] + ", ");
            else if (i == array.length - 1) System.out.println(array[i] + "]");
            else System.out.print(array[i] + ", ");
        }
    }

    private static Segment findBestSegment(ArrayList<Coordinate> coordinates) {
        System.out.println("------------------------------------------------");
        System.out.println("\t\t\tBEST SEGMENT");
        Coordinate a = coordinates.get(0);
        System.out.println("Origin = " + a);
        coordinates.remove(0);
        ArrayList<Double> angles = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();
        ArrayList<Double> combined = new ArrayList<>();
        for(Coordinate b : coordinates) {
            Segment hyp = new Segment(a, b);
            Coordinate c = new Coordinate(a.getX(), b.getY());
            Segment adj = new Segment(a, c);
            Double angle = Math.toDegrees(Math.acos(adj.getDistance() / hyp.getDistance()));
            System.out.println("Angle from a to " + b + ":\t" + angle);
            System.out.println("Hyp length: " + hyp.getDistance());
            System.out.println("Combined: " + angle * hyp.getDistance());
            System.out.println();
            angles.add(angle);
            distances.add(hyp.getDistance());
            combined.add(angle*hyp.getDistance());
        }

        System.out.println();

        double smallestAngle = angles.get(0);
        int idAngle = 0;
        for(Double angle : angles) {
            if(angle < smallestAngle) {
                smallestAngle = angle;
                idAngle = angles.indexOf(angle);
            }
        }
        double smallestDistance = angles.get(0);
        int idDistance = 0;
        for(Double distance : distances) {
            if(distance < smallestDistance) {
                smallestDistance = distance;
                idDistance = distances.indexOf(distance);
            }
        }
        double smallestCombined = combined.get(0);
        int idCombined = 0;
        for(Double combine : combined) {
            if(combine < smallestCombined) {
                smallestCombined = combine;
                idCombined = combined.indexOf(combine);
            }
        }
        System.out.println("Smallest angle: " + smallestAngle);
        System.out.println("Smallest distance: " + smallestDistance);
        System.out.println("Smallest combined: " + smallestCombined);
        System.out.println("Coordinate with smallest angle:\t"+coordinates.get(idAngle));
        System.out.println("Coordinate with the smallest distance:\t"+coordinates.get(idDistance));

        System.out.println("Coordinate with the smallest combined distance: " + coordinates.get(idCombined));

        System.out.println("------------------------------------------------");
        return new Segment(a, coordinates.get(idAngle));
    }
    private static ArrayList<Coordinate> generatePolygon(int vertices) {
        if(vertices < -1)
            throw new IllegalArgumentException("Vertices cannot be less than -1.");
        if(vertices > -1 && vertices < 3)
            throw new IllegalArgumentException("Need at least 3 vertices to be a polygon.");
        int maxLength = 100;
        int minLength = -100;
        int maxWidth = 100;
        int minWidth = -100;

        if(vertices == -1)
            vertices = random(3, 10);

        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        for(int i = 0; i < vertices; i++)
            coordinateList.add(new Coordinate(random(minLength, maxLength), random(minWidth, maxWidth)));

        return coordinateList;
    }

    public static void main(String[] args) {
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
            shape5 = new IrregularPolygon(generatePolygon(-1));
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
        /*
        Coordinate a = new Coordinate(-84,-18);
        Coordinate b = new Coordinate(66,-60);
        Coordinate c = new Coordinate(-46,-74);
        Coordinate d = new Coordinate(-33,44);
        Segment hyp = new Segment(a,b);
        Coordinate third = new Coordinate(a.getX(), b.getY());
        Segment adj = new Segment(a,third);
        Segment opp = new Segment(third,b);
        System.out.println("hyp distance: " + hyp.getDistance());
        System.out.println("adj distance: " + adj.getDistance());
        System.out.println("opp distance: " + opp.getDistance());
        System.out.println("Theta: " + Math.toDegrees(Math.acos(adj.getDistance() / hyp.getDistance())));
        */
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
