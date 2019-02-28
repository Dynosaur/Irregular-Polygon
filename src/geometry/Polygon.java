package geometry;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/26/2019
 *
 * A polygon is a series of coordinates that are connected by line segments. The class is supposed to represent regular
 * polygons and as such you can only control the distance of lines and where the center of the polygon will be, rather
 * than adding in your own coordinates. If you want to add your own coordinates, please use IrregularPolygon.
 */
public class Polygon {

    /** Custom unchecked exception for when there are no coordinates to draw from. */
    class NoCoordinatesException extends RuntimeException {
        NoCoordinatesException(String message) {
            super(message);
        }
    }

    /** Contains all of the coordinates of the polygon. */
    ArrayList<Coordinate> coordinates;
    /** Contains all of the line segments of the polygon. */
    ArrayList<Segment> lineSegments = new ArrayList<>();

    /** Number of vertices for this polygon. Only applies to regular polygons. */
    private int vertices;
    /** Distance between each vertex. */
    private double distance;
    /** Center of the polygon. */
    private Coordinate origin;

    public Polygon() {}
    /**
     * Creates a n-gon with the specified distance between each point, and with its center at the given coordinate. Note
     * that a polygon requires at least 3 vertices. Entering -1 will generate a random number of vertices.
     * @param v Number of vertices
     * @param d Distance between each vertex
     * @param center Location of the center
     */
    public Polygon(int v, double d, Coordinate center) {
        if(v < 3 && v > -1 || v < -1) throw new IllegalArgumentException("Not enough vertices.\nRequired: 3\nv: " + v);
        if(d < 0.0D) throw new IllegalArgumentException("Negative distance. d: " + d);
        if(d == 0.0D) throw new IllegalArgumentException("Distance cannot be zero.");
        if(v == -1) vertices = random(3,12);
        else vertices = v;
        distance = d;
        origin = center;
        coordinates = new ArrayList<>(generatePolygon(vertices, distance, origin));
        for(int i = 0; i < vertices; i++)
            lineSegments.add(new Segment(coordinates.get(i), coordinates.get(i % vertices)));
    }

    public static int random(int min, int max) {
        return IrregularPolygon.random(min, max);
    }

    /**
     * Generates a polygon that is equiangular and whose side lengths are equilateral.
     * @param vertices Number of vertices, must be above 3, however -1 picks a random number
     * @param distance Distance between each vertex
     * @param center Center of the polygon
     * @return An ArrayList with the coordinates of the vertices
     */
    public static ArrayList<Coordinate> generatePolygon(int vertices, double distance, Coordinate center) {
        if(vertices < 3 && vertices > -1 || vertices < -1) throw new IllegalArgumentException("Vertices must be greater than 3.\nvertices: " + vertices);
        if(distance <= 0.0D) throw new IllegalArgumentException("Distance needs to be above 0.");

        // angle of each interior angle
        double angle = 360.0D/vertices;

        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        Coordinate start = new Coordinate(center.getX() - distance / 2,center.getY() - distance / 2);
        coordinateList.add(start);

        gui.Pen pen = new gui.Pen();
        pen.up();
        pen.move(start);
        for(int i = 0; i < vertices; i++) {
            pen.move(distance);
            pen.turn(-angle);
            coordinateList.add(new Coordinate(pen.getXPos(), pen.getYPos()));
        }

        return coordinateList;
    }

    /**
     * Draws the polygon using its line segments using a black pen.
     * @param pen The pen used to draw the polygon.
     */
    public void draw(gui.Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }

    /**
     * Loops through the line segments array and moves to the start and end points.
     * @param pen The pen used to draw the polygon
     * @param color The color of the pen
     */
    public void draw(gui.Pen pen, java.awt.Color color) {
        pen.up();
        pen.move(lineSegments.get(0).getStart());
        pen.down();
        pen.setWidth(1);
        pen.setColor(color);
        for(Segment line : lineSegments) {
            pen.move(line.getStart());
            pen.move(line.getEnd());
        }
        pen.move(lineSegments.get(0).getStart());
    }

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public int getVertices() {
        return vertices;
    }
    public double getDistance() {
        return distance;
    }
    public Coordinate getOrigin() {
        return origin;
    }
    // </editor-fold>

}