/**
 * @author Alejandro Doberenz
 * @version 2/20/2019
 *
 * A line segment is a line that begins at a point and ends at another.
 */
public class Segment implements java.io.Serializable {

    private Coordinate start,end;

    private double distance, slope, intercept, minX, maxX;

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public Coordinate getStart() {
        return start;
    }
    public Coordinate getEnd() {
        return end;
    }
    public double getDistance() {
        return distance;
    }
    public double getSlope() {
        return slope;
    }
    // </editor-fold>

    public void draw(Pen pen) {
        pen.up();
        pen.move(start);
        pen.down();
        pen.move(end);
    }

    public double getY(double x) {
        if(x < minX || x > maxX)
            throw new IllegalArgumentException("Given x value outside of line segment");
        return slope * x + intercept;
    }

    public boolean doesIntersect(Segment b) {
        double x = (b.intercept - intercept) / (slope - b.slope);
        return x >= start.getX() && x <= end.getX();
    }
    public Coordinate intersect(Segment b) {
        if(doesIntersect(b)) {
            double x = (b.intercept - intercept) / (slope - b.slope);
            return new Coordinate(x, getY(x));
        } else
            throw new IllegalArgumentException("Lines do not intersect.");
    }

    public static double distance(Coordinate a, Coordinate b) {
        Coordinate third = new Coordinate(a.getX(), b.getY());
        double s1 = third.getY() - a.getY();
        double s2 = third.getX() - b.getX();
        return Math.sqrt((s1*s1) + (s2*s2));
    }
    public static double slope(Coordinate a, Coordinate b) {
        double xChg = a.getX() - b.getX();
        double yChg = a.getY() - b.getY();
        return yChg / xChg;
    }

    @Override public String toString() {
        return "Segment (" + start + ", " + end + ")";
    }

    public Segment(Coordinate a, Coordinate b) {
        if(a.getX() < b.getX()) {
            start = a;
            end = b;
        } else {
            start = b;
            end = a;
        }
        distance = distance(start, end);
        intercept = -slope * start.getX() + start.getY();
        if(start.getX() == end.getX()) {
            minX = start.getX();
            maxX = start.getX();
        } else {
            if(start.getX() > end.getX()) {
                minX = end.getX();
                maxX = start.getX();
            } else {
                minX = start.getX();
                maxX = end.getX();
            }
        }
        slope = slope(start, end);
    }

}
