package gui;

import geometry.Coordinate;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * @author Alejandro Doberenz
 * @version 3/8/2019
 *
 * This table model allows for the creation of a table model using a coordinate array list. This minimizes the code
 * to format the data by using the coordinates to get their X and Y positions. Another note, this took like 5 hours
 * to make just because I have no idea what an abstract table model is. That also means that this probably is not
 * very efficient, so I may come back to refactor this when I have more knowledge on the subject.
 */
public final class CoordinateTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"X", "Y"};

    private ArrayList<Coordinate> coordinateDataList;

    CoordinateTableModel(ArrayList<Coordinate> pointList) {
        coordinateDataList = new ArrayList<>(pointList);
    }

    @Override public int getColumnCount() {
        return 2;
    }

    @Override public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override public int getRowCount() {
        return coordinateDataList.size();
    }

    @Override public Double getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
                return coordinateDataList.get(rowIndex).getX();
            case 1:
                return coordinateDataList.get(rowIndex).getY();
            default:
                throw new IllegalArgumentException("columnIndex: " + columnIndex + " is not 0 or 1.");
        }
    }

}
