import gpdraw.SketchPad;

import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) {
        //SketchPad sp = new SketchPad(250,250);
        //Pen pen = new Pen(sp);

        Coordinate a = new Coordinate(20,45);
        Coordinate b = new Coordinate(40,28);
        Coordinate c = new Coordinate(10,33);
        Coordinate d = new Coordinate(78, -39);
        Coordinate e = new Coordinate(-18, -49);
        ArrayList<Coordinate> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        list.add(e);
        LineBuilder lb = new LineBuilder(true, list);
        IrregularPolygon shape0 = new IrregularPolygon();
        //lb.auto().toArray(new Segment[0])

        GUI gui = new GUI(lb);

        //shape0.draw(pen);
    }

}