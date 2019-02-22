/**
 * @author Alejandro Doberenz
 * @version 2/15/2019
 *
 * A Coordinate represents a point on the GPDraw sketchpad.
 */
public class Coordinate implements java.io.Serializable {

    private double x; // x location of the point
    private double y; // y location of the point

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    // Get a point that is translated a given x and y points over
    public Coordinate translate(double x, double y) {
        return new Coordinate(this.x +x, this.y + y);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public void draw(Pen pen, java.awt.Color color) {
        pen.up();
        pen.move(this);
        pen.down();
        pen.setWidth(7);
        pen.setColor(color);
        pen.move(this);
        pen.setWidth(1);
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}