package gui;

import geometry.Coordinate;
import geometry.Segment;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * @author Alejandro Doberenz
 * @version 3/8/2019
 *
 * This table model allows for the formatting of segments on a table, displaying their starting and ending points'
 * positions, similar to the CoordinateTableModel.
 */
public class LineTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Start X", "Start Y", "End X", "End Y"};

    private ArrayList<Segment> segmentDataList;

    LineTableModel(ArrayList<Segment> lineList) {
        segmentDataList = new ArrayList<>(lineList);
    }

    @Override public int getColumnCount() {
        return 4;
    }

    @Override public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override public int getRowCount() {
        return segmentDataList.size();
    }

    @Override public Double getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
                return segmentDataList.get(rowIndex).getStart().getX();
            case 1:
                return segmentDataList.get(rowIndex).getStart().getY();
            case 2:
                return segmentDataList.get(rowIndex).getEnd().getX();
            case 3:
                return segmentDataList.get(rowIndex).getEnd().getY();
            default:
                throw new IllegalArgumentException("columnIndex: " + columnIndex + " is not 0, 1, 2, or 3.");
        }
    }
}