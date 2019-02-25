/**
 * @author Alejandro Doberenz
 * @version 2/25/2019
 *
 * A line segment is a line that begins at a coordinate and ends at another. Although drawing methods do not require it,
 * the slope is calculated for the reason of calculating intersecting lines.
 */
public class Segment implements java.io.Serializable {

    private static class IsOutsideLineException extends Exception {
        public IsOutsideLineException(String message) {
            super(message);
        }
    }
    
    // The coordinate the line segment starts and ends at
    private final Coordinate START, END;

    private final double DISTANCE,  // The distance between the starting and ending coordinate
                        SLOPE,      // The slope of the line
                        INTERCEPT,  // The y-intercept of the line
                        MINIMUM,    // The smallest X value of the line
                        MAXIMUM;    // The largest X value of the line

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public Coordinate getStart() {
        return START;
    }
    public Coordinate getEnd() {
        return END;
    }
    public double getDistance() {
        return DISTANCE;
    }
    public double getSlope() {
        return SLOPE;
    }
    // </editor-fold>

    public void draw(Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }
    public void draw(Pen pen, java.awt.Color color) {
        pen.up();
        pen.move(START);
        pen.down();
        
        pen.setColor(color);
        start.draw(pen);
        pen.move(END);
        end.draw(pen);
    }

    /**
    * Gets the Y value at the X value. It will throw an IllegalArgumentException if the x value is outside of the line segment.
    *
    * @param    x The X value to obtain the Y value at
    * @return   The Y value of this line at the X value
    */
    public double getY(double x) throws IsOutsideLineException {
        if(X < MINIMUM || X > MAXIMUM)
            throw new IsOutsideLineException(x + " is outside of this line segment:\nMINIMUM: " + MINIMUM + "\nMAXIMUM: " + MAXIMUM);
        return SLOPE * X + INTERCEPT;
    }
    
    /**
    * Checks if this line and otherLine intersect.
    *
    * @param    otherLine Another Segment
    * @return   If these lines intersect or not
    */
    public boolean doesIntersect(Segment otherLine) {
        double x = (INTERCEPT - otherLine.INTERCEPT) / (otherLine.SLOPE - SLOPE);
        try {
            Coordinate intersect = new Coordinate(x, getY(x));
            if(intersect.equals(START) || intersect.equals(END) || intersect.equals(otherLine.START) || intersect.equals(otherLine.END)) return false;
            return Math.abs(intersect.getY() - b.getY(x)) <= 0.0001;
        } catch(IsOutsideLineException e) {
            return false;
        }
    }
    /**
    * Finds the coordinate where two lines intersect.
    *
    * @param    otherLine The other line to find the intersection at
    * @return  The coordinate the lines intersect at
    */
    public Coordinate intersect(Segment otherLine) {
        if(doesIntersect(otherLine)) {
            double x = (INTERCEPT - otherLine.INTERCEPT) / (otherLine.SLOPE - SLOPE);
            return new Coordinate(x, getY(x));
        } else throw new IllegalArgumentException(otherLine + " does not intersect with " + this + ".");
    }

    @Override public String toString() {
        return "Segment (" + start + ", " + end + ")";
    }

    public Segment(Coordinate start, Coordinate end) {
        START = start;
        END = end;
        distance = START.distance(END);                     // pythagorean theorem
        slope = START.slope(END);                           // yChg/xChg
        intercept = START.getY() - SLOPE * START.getX();    // b = y-mx
        if(start.getX() == end.getX()) {
            MINIMUM = start.getX();
            MAXIMUM = start.getX();
        } else {
            if(START.getX() > END.getX()) {
                MINIMUM = end.getX();
                MAXIMUM = start.getX();
            } else {
                MINIMUM = start.getX();
                MAXIMUM = end.getX();
            }
        }
    }

}
