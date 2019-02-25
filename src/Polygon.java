import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
 *
 * A polygon is a series of coordinates that are connected by line segments. The class is supposed to represent regular
 * polygons and as such you can only control the distance of lines and where the center of the polygon will be, rather
 * than adding in your own coordinates. If you want to add your own coordinates, please use IrregularPolygon.
 */
public class Polygon {

    class NoCoordinatesException extends RuntimeException {
        NoCoordinatesException(String message) {
            super(message);
        }
    }

    ArrayList<Coordinate> coordinates = new ArrayList<>();
    ArrayList<Segment> lineSegments = new ArrayList<>();

    private int vertices;

    private double distance;

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

    /*
    public static ArrayList<Coordinate> generatePolygon(int vertices, double distance, Coordinate center) {
        if(vertices < -1) throw new IllegalArgumentException("Vertices cannot be less than -1.");
        if(vertices > -1 && vertices < 3) throw new IllegalArgumentException("Need at least 3 vertices to be a polygon.");
        if(distance <= 0) throw new IllegalArgumentException("Distance needs to be above 0.");

        double angle = 360.0D/vertices;

        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        Coordinate start = new Coordinate(center.getX()-distance/2,center.getY()-distance/2);
        coordinateList.add(start);

        for(int i = 0; i < vertices; i++)
            coordinateList.add(new Coordinate(Helper.random(minLength, maxLength), Helper.random(minWidth, maxWidth)));

        return coordinateList;
    }
    */

    public Polygon() {}
    public Polygon(int v, double d) {
        vertices = v;
        distance = d;
    }

}