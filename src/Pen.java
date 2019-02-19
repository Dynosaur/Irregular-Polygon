import gpdraw.SketchPad;

import java.awt.Color;

public class Pen extends gpdraw.DrawingTool {

    public void move(Coordinate c) {
        move(c.getX(), c.getY());
    }

    public Pen(SketchPad sp) {
        super(sp);
    }

}
