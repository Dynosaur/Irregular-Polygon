/**
 * @author Alejandro Doberenz
 * @version 2/20/2019
 *
 * A line segment is a line that begins at a point and ends at another.
 */
public class Segment {

    private Coordinate start,end;

    private double distance, slope;

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

    public static double distance(Coordinate a, Coordinate b) {
        Coordinate c = new Coordinate(a.getX(), b.getY());
        double ac = c.getY() - a.getY();
        double bc = c.getX() - b.getX();
        return Math.sqrt((ac * ac) + (bc * bc));
    }

    public static double slope(Coordinate a, Coordinate b) {
        double xChg = a.getX() - b.getX();
        double yChg = a.getY() - b.getY();
        return yChg / xChg;
    }

    public Segment(Coordinate a, Coordinate b) {
        start = a;
        end = b;
        distance = distance(start, end);
        slope = slope(start, end);
    }

}
