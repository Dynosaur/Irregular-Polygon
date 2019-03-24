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
        /*
        Coordinate[] array = {  new Coordinate(-0.89,2.19), new Coordinate(-1.21,1.19),
                                new Coordinate(-1.75,2.415),new Coordinate(-1.51,1.8),
                                new Coordinate(-2.01,1.45), new Coordinate(-2.81,1.61)};
        ArrayList<Coordinate> list = new ArrayList<>(java.util.Arrays.asList(array));
        while(list.size() > 0) {
            Segment x = LineBuilder.findNextBestLine(list);
            list.remove(x.getStart());
            list.add(0, list.remove(list.indexOf(x.getEnd())));
            System.out.println(x.getEnd());
        }
        */
    }

}
