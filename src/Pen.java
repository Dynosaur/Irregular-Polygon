import gpdraw.SketchPad;

import java.awt.Color;

/**
 * @author Alejandro Doberenz
 * @version 2/20/2019
 *
 * The Pen operates in the same fashion as a DrawingTool, however it includes a method to allow
 * moving to a given coordinate.
 */
public class Pen extends gpdraw.DrawingTool {

    public void move(Coordinate c) {
        move(c.getX(), c.getY());
    }

    public Pen(SketchPad sp) {
        super(sp);
    }

}
