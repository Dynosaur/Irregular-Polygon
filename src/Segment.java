/**
 * @author Alejandro Doberenz
 * @version 2/16/2019
 */
public class Segment {

    private Coordinate start,end;

    private double distance;

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public Coordinate getStart() {
        return start;
    }
    public Coordinate getEnd() {
        return end;
    }
    // </editor-fold>

    public static double distance(Coordinate a, Coordinate b) {
        Coordinate c = new Coordinate(a.getX(), b.getY());
        double ac = c.getY() - a.getY();
        double bc = c.getX() - b.getX();
        return Math.sqrt((ac * ac) + (bc * bc));
    }

    public Segment(Coordinate a, Coordinate b) {
        start = a;
        end = b;
        distance = distance(start, end);
    }

}
