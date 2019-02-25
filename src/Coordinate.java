/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
 *
 * A coordinate represents a position on an X and Y graph. It contains methods to compare it to other coordinates,
 * convert it to a String, and draw it with a Pen object.
 */
public class Coordinate implements java.io.Serializable {

    private double x, y;    // The X and Y position
    
    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
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
    
    // Get a point that is a distance away from this point
    public Coordinate translate(double x, double y) {
        return new Coordinate(this.x +x, this.y + y);
    }

    // <editor-fold defaultstate="collapsed" desc="Object Methods">
    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }
    @Override public boolean equals(Object obj) {
        if(!(obj instanceof Coordinate)) return false;
        Coordinate coordinate = (Coordinate) obj;
        return Math.abs((coordinate.x - x) - 1.0D) <= 1.0D && Math.abs((coordinate.y - y) - 1.0D) <= 1.0D;
    }
    // </editor-fold>
    
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
