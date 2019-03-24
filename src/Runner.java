import geometry.Coordinate;
import geometry.IrregularPolygon;
import geometry.LineBuilder;
import geometry.Segment;
import gui.GUI;
import gpdraw.SketchPad;

import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) {
        ArrayList<Coordinate> list = new ArrayList<>(IrregularPolygon.generateIrregularPolygon(4));
        LineBuilder lb = new LineBuilder(list);
        java.awt.EventQueue.invokeLater(new Thread(() -> new GUI(lb)));
    }

}
