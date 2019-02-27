package geometry;

import gui.Pen;

/**
 * @author Alejandro Doberenz
 * @version 2/26/2019
 *
 * A coordinate represents a position on an X and Y graph. It contains methods to compare it to other coordinates,
 * convert it to a String, and draw it with a Pen object.
 */
public class Coordinate implements java.io.Serializable {

    /** The X and Y position of the Coordinate */
    private final double X, Y;

    public Coordinate(double x, double y) {
        X = x;
        Y = y;
    }

    /**
     * Translates this coordinate by the X and Y values. Note that this does not give you an existing coordinate
     * at that location, but makes a new coordinate at the location.
     * @param x Modifies the X value
     * @param y Modifies the Y value
     * @return A new coordinate at the translated position
     */
    public Coordinate translate(double x, double y) {
        return new Coordinate(X + x, Y + y);
    }

    /**
     * Finds the distance between two coordinates by using the distance formula. Wow, I finally found a practical
     * use for of what I learned in high school.
     * @param otherCoordinate The coordinate to find the distance to
     * @return The distance from this coordinate to the given coordinate
     */
    public double distance(Coordinate otherCoordinate) {
        return Math.sqrt(Math.pow((otherCoordinate.X - X), 2.0D) + Math.pow((otherCoordinate.Y - Y), 2.0D));
    }

    /**
     * Finds the gradient of a line between this point and another. Simple yChg / xChg.
     * @param otherCoordinate A coordinate to find the slope between
     * @return The slope of a line between this point and the parameter
     */
    public double slope(Coordinate otherCoordinate) {
        return (otherCoordinate.Y - Y) / (otherCoordinate.X - X);
    }

    /**
     * Draws this coordinate with the specified pen. Refers to draw(Pen, Color) while passing in Color.BLACK.
     * @param pen The pen used to draw this coordinate
     */
    public void draw(Pen pen) {
        draw(pen, java.awt.Color.BLACK);
    }

    /**
     * Draws this point without drawing any lines towards it. It is given a modified width so it is more visible. The
     * width is set back to default at the end of the method.
     * @param pen The pen used to draw this coordinate
     * @param color The color used to draw this coordinate
     */
    public void draw(Pen pen, java.awt.Color color) {
        pen.up();               // Lifts the pen up so no lines are drawn toward the coordinate
        pen.move(this);
        pen.down();
        pen.setWidth(5);
        pen.setColor(color);
        pen.move(this);
        pen.setWidth(1);        // Defaulting the width
    }

    // <editor-fold defaultstate="collapsed" desc="Object Methods">
    @Override public String toString() {
        return "Point[X=" + X + ",Y=" + Y + "]";
    }

    /**
     * Compares this coordinate with another object. It is specialized to deal with comparing coordinates. Coordinates
     * are considered to be equal if their distance is less than 0.0001.
     * @param obj Any object, preferably a coordinate
     * @return If the two objects are equal or not
     */
    @Override public boolean equals(Object obj) {
        if(!(obj instanceof Coordinate)) return false;
        Coordinate coordinate = (Coordinate) obj;
        return distance(coordinate) < 0.0001;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public double getX() {
        return X;
    }
    public double getY() {
        return Y;
    }
    // </editor-fold>
    
}