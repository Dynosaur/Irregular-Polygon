import gpdraw.*;

import java.awt.Color;

import java.util.ArrayList;

public class IrregularPolygon {

    private ArrayList<Segment> lines;
    private ArrayList<Coordinate> points;

    public void add(Coordinate point) {
        points.add(point);
    }
    public void add(Segment segment) {
        lines.add(segment);
    }

    public void draw(Pen p) {
        p.up();
        p.move(points.get(0).getX(), points.get(0).getY());
        p.down();
        for(Coordinate c : points) {
            p.move(c);
        }
        p.move(points.get(0).getX(), points.get(0).getY());
    }
    public void draw(Pen p, Color color) {
        p.up();
        p.move(points.get(0).getX(), points.get(0).getY());
        p.down();
        p.setColor(color);
        for(Coordinate c : points) {
            p.move(c);
        }
        p.move(points.get(0).getX(), points.get(0).getY());
        p.setColor(Color.BLACK);
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
        lines = new ArrayList<>();
        for(int i = 0; i < points.size(); i++)
            lines.add(new Segment(points.get(i%points.size()), points.get((i+1)%points.size())));
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
        Pen pen = new Pen(sp);
        Coordinate a = new Coordinate(10,30);
        Coordinate b = new Coordinate(40,70);
        Coordinate c = new Coordinate(40,30);
        IrregularPolygon shape0 = new IrregularPolygon();
        shape0.add(a);
        shape0.add(b);
        shape0.add(c);
        shape0.draw(pen);
        IrregularPolygon shape1 = new IrregularPolygon(a, b, c);
        shape1.draw(pen, Color.RED);
        System.out.println(shape1.perimeter());
        Segment ab = new Segment(a, b);
        Segment bc = new Segment(b, c);
        Segment ca = new Segment(c, a);
        IrregularPolygon shape2 = new IrregularPolygon();
        shape2.add(ab);
        shape2.add(bc);
        shape2.add(ca);
        shape2.draw(pen, Color.BLUE);
        IrregularPolygon shape3 = new IrregularPolygon(ab, bc, ca);
        shape3.draw(pen, Color.GREEN);
    }

}
