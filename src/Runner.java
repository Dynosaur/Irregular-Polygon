import geometry.Coordinate;
import geometry.IrregularPolygon;
import geometry.LineBuilder;
import geometry.Segment;
import gui.GUI;
import gpdraw.SketchPad;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) {
        /*
        ArrayList<Coordinate> list = new ArrayList<>(IrregularPolygon.generateIrregularPolygon(16));
        LineBuilder lb = new LineBuilder(list);
        java.awt.EventQueue.invokeLater(new Thread(() -> new GUI(lb)));
        */
        ArrayList<Segment> lines = new ArrayList<>();
        lines.add(Segment.random(10));
        lines.add(Segment.random(10));
        lines.add(Segment.random(10));
        lines.add(Segment.random(10));
        lines.add(Segment.random(10));
        LineBuilder.sortByLength(lines);
        for(Segment line : lines)
            System.out.printf("%1.2f%n", line.getDistance());
    }

}
