import gpdraw.*;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

public class IrregularPolygon {

    private ArrayList<Segment> lines;
    private ArrayList<Coordinate> points;

    public void add(Double point) {

    }
    public void draw(DrawingTool p) {
        p.up();
        p.move(points.get(0).getX(), points.get(0).getY());
        p.down();
        for(Coordinate c : points) {
            p.move(c.getX(), c.getY());
        }
        p.move(points.get(0).getX(), points.get(0).getY());
    }
    public double distance(Coordinate a, Coordinate b) {
        Coordinate c = new Coordinate(a.getX(), b.getY());
        double ac = c.getY() - a.getY();
        double bc = c.getX() - b.getX();
        return Math.sqrt((ac * ac) + (bc * bc));
    }
    public double perimeter() {
        double perimeter = 0;
        for(int i = 0; i < points.size(); i++) {
            Coordinate a = points.get(i);
            Coordinate b = points.get((i+1)%points.size());
            perimeter += distance(a, b);
        }
        return perimeter;
    }
    public double area() {
        return 0;
    }

    public IrregularPolygon() {
        points = new ArrayList<>();
    }
    public IrregularPolygon(Coordinate... p) {
        points = new ArrayList<>(java.util.Arrays.asList(p));
        for(int i = 0; i < points.size(); i++) {
        }
    }
    public IrregularPolygon(Segment... l) {
        lines = new ArrayList<>(java.util.Arrays.asList(l));
        points = new ArrayList<>();
        for(Segment s : lines) {
            points.add(s.getStart());
        }
    }

    public static void main(String[] args) {
        SketchPad sp = new SketchPad(500,500);
        DrawingTool pen = new DrawingTool(sp);
        Coordinate a = new Coordinate(10,30);
        Coordinate b = new Coordinate(40,70);
        Coordinate c = new Coordinate(40,30);
        IrregularPolygon shape1 = new IrregularPolygon(a, b, c);
        shape1.draw(pen);
        System.out.println(shape1.perimeter());
        Segment ab = new Segment(a, b);
        Segment bc = new Segment(b, c);
        Segment ca = new Segment(c, a);
        pen.setColor(java.awt.Color.RED);
        IrregularPolygon shape2 = new IrregularPolygon(ab, bc, ca);
        shape2.draw(pen);
    }

}
