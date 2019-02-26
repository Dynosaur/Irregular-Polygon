import gpdraw.SketchPad;
import gpdraw.SketchPadPanel;

import java.awt.Color;

/**
 * @author Alejandro Doberenz
 * @version 2/20/2019
 *
 * The Pen operates in the same fashion as a DrawingTool, however it includes a method to allow
 * moving to a given coordinate.
 */
public class Pen extends gpdraw.DrawingTool implements java.io.Serializable {

    public void move(Coordinate c) {
        move(c.getX(), c.getY());
    }

    public Pen(SketchPad sp) {
        super(sp);
    }
    public Pen(SketchPadPanel sp) {
        super(sp);
    }

}
