/**
 * @author Alejandro Doberenz
 * @version 2/25/2019
 *
 * A coordinate represents a position on an X and Y graph. It contains methods to compare it to other coordinates,
 * convert it to a String, and draw it with a Pen object.
 */
public class Coordinate implements java.io.Serializable {

    // The X and Y position
    private final double X, Y;
    
    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public double getX() {
        return X;
    }
    public double getY() {
        return Y;
    }
    // </editor-fold>
    
    public void draw(Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }
    public void draw(Pen pen, java.awt.Color color) {
        pen.up();
        pen.move(this);
        pen.down();
        pen.setWidth(5);
        pen.setColor(color);
        pen.move(this);
        pen.setWidth(1);
    }
    
    /**
    * Creates a new coordinate at the modified coordinates.
    *
    * @param    x Modifies the X position by this amount.
    * @param    y Y position to add to the current coordinate
    * @return   New coordinate with the modified coordinates.
    */
    public Coordinate translate(double x, double y) {
        return new Coordinate(X + x, Y + y);
    }
    /**
    * Finds the distance between this coordinate and another.
    *
    * @param    otherCoordinate The coordinate to find the distance to
    * @return   The distance from this point to otherCoordinate.
    */
    public double distance(Coordinate otherCoordinate) {
        Coordinate third = new Coordinate(X, otherCoordinate.Y);
        double side1 = third.X - otherLine.X;
        double side2 = third.Y - otherLine.Y;
        return Math.sqrt((side1*side1) + (side2*side2));
    }
    /**
    * Finds the slope between this coordinate and another.
    *
    * @param    otherCoordinate The coordinate to find the slope to
    * @return   The slope between this point and otherCoordinate
    */
    public static double slope(Coordinate otherCoordinate) {
        double xChg = X - otherCoordinate.X;
        double yChg = Y - otherCoordinate.Y;
        return yChg / xChg;
    }

    // <editor-fold defaultstate="collapsed" desc="Object Methods">
    @Override public String toString() {
        return "(" + X + ", " + Y + ")";
    }
    @Override public boolean equals(Object obj) {
        if(!(obj instanceof Coordinate)) return false;
        Coordinate coordinate = (Coordinate) obj;
        return Math.abs((coordinate.X - X) - 1.0D) <= 1.0D && Math.abs((coordinate.Y - Y) - 1.0D) <= 1.0D;
    }
    // </editor-fold>
    
    public Coordinate(double x, double y) {
        X = x;
        Y = y;
    }
    
}
