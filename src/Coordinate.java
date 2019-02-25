import java.awt.*;

/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
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

    // Get a point that is translated a given x and y coordinates over
    public Coordinate translate(double x, double y) {
        return new Coordinate(this.x +x, this.y + y);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }
    @Override public boolean equals(Object obj) {
        if(!(obj instanceof Coordinate)) return false;
        Coordinate coordinate = (Coordinate) obj;
        return Math.abs((coordinate.x - x) - 1.0D) <= 1.0D && Math.abs((coordinate.y - y) - 1.0D) <= 1.0D;
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
    public void draw(Pen pen) {
        draw(pen, Color.BLACK);
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}