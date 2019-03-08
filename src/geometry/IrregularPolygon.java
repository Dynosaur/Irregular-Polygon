package geometry;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/26/2019
 *
 * An irregular polygon is different from a regular polygon (Polygon.java) because it does not have to be
 * equiangular or equilateral. This allows for the insertion of random points and the drawing of segments between
 * them.
 */
public class IrregularPolygon extends Polygon implements java.io.Serializable {

    private boolean stickToLines;

    public void addCoordinate(Coordinate coordinate) {
        for(Coordinate point : coordinates)
            if(coordinate.equals(point)) { System.out.println(coordinate + " is already in Polygon at coordinates[" + coordinates.indexOf(point) + "]."); return; }
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
            if(lineSegments.size() == 0) throw new Polygon.NoCoordinatesException("There are no coordinates for this Polygon.");
            else for(Segment line : lineSegments) coordinates.add(line.getStart());
        if(lineSegments.size() == 0) createLines();

    }

    public void draw(gui.Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }
    public void draw(gui.Pen pen, java.awt.Color color) {
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
        int maxLength = 100;
        int minLength = -100;
        int maxWidth = 100;
        int minWidth = -100;

        if(vertices == -1) vertices = random(3, 10);

        ArrayList<Coordinate> coordinateList = new ArrayList<>();

        for(int i = 0; i < vertices; i++)
            coordinateList.add(new Coordinate(random(minLength, maxLength), random(minWidth, maxWidth)));

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

}