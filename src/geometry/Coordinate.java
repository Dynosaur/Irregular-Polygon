package geometry;

import gpdraw.DrawingTool;

/**
 * @author Alejandro Doberenz
 * @version 3/24/2019
 *
 * A coordinate represents a position on an X and Y graph. It contains methods to compare it to other coordinates,
 * convert it to a String, and draw it with a Pen object.
 */
public class Coordinate implements java.io.Serializable {

    // The X and Y position of the Coordinate
    private final double X, Y;

    /**
     * Finds the distance between two coordinates by using the distance formula. Wow, I finally found a practical
     * use for of what I learned in high school.
     *
     * @param otherCoordinate The coordinate to find the distance between
     * @return The distance from this coordinate to the given coordinate
     */
    public double distance(Coordinate otherCoordinate) {
        return Math.sqrt(Math.pow((otherCoordinate.X - X), 2.0D) + Math.pow((otherCoordinate.Y - Y), 2.0D));
    }

    /**
     * Finds the slope of a line between this point and another.
     *
     * @param otherCoordinate Any coordinate
     * @return The slope of a line between this and another coordinate
     */
    public double slope(Coordinate otherCoordinate) {
        return (otherCoordinate.Y - Y) / (otherCoordinate.X - X);
    }

    public double angle(Coordinate otherCoordinate) {
        return Math.toDegrees(Math.atan(this.slope(otherCoordinate)));
    }

    public static Coordinate random(double range) {
        if(range <= 0) throw new IllegalArgumentException("Argument 'range' cannot be less than or equal to 0: " + range);
        return random(-range/2, range/2);
    }

    public static Coordinate random(double min, double max) {
        if(min >= max) throw new IllegalArgumentException("Argument 'min' must be less than 'max': " + min + " > " + max);
        return new Coordinate((1-Math.random())*(max-min)+min,(1-Math.random())*(max-min)+min);
    }

    /**
     * Draws this coordinate with the color black.
     *
     * @param pen The DrawingTool used to draw this coordinate
     */
    public void draw(DrawingTool pen) {
        draw(pen, java.awt.Color.BLACK);
    }

    /**
     * Draws this point with the specified color.
     *
     * @param pen The DrawingTool used to draw this coordinate
     * @param color The color used to draw this coordinate
     */
    public void draw(DrawingTool pen, java.awt.Color color) {
        pen.up();               // Lifts the pen up so no lines are drawn toward the coordinate
        pen.move(X, Y);
        pen.down();
        pen.setWidth(5);
        pen.setColor(color);
        pen.move(X, Y);
        pen.setWidth(1);
    }

    /**
     * Compares this coordinate with another object. It is specialized to deal with comparing coordinates. Coordinates
     * are considered to be equal if their distance is less than 0.0001.
     *
     * @param point Any object, preferably a coordinate
     * @return If the two objects are equal or not
     */
    public boolean equals(Coordinate point) {
        return distance(point) < 0.0001;
    }

    @Override public String toString() {
        return "Point[X=" + X + ",Y=" + Y + "]";
    }

    @Override public boolean equals(Object obj) {
        if(!(obj instanceof Coordinate)) return false;
        Coordinate point = (Coordinate) obj;
        return distance(point) < 0.0001;
    }

    public Coordinate(double x, double y) {
        X = x;
        Y = y;
    }

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public double getX() {
        return X;
    }
    public double getY() {
        return Y;
    }
    // </editor-fold>
    
}