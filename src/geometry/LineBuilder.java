package geometry;

import geometry.Coordinate;
import geometry.Segment;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/27/2019
 *
 * A line builder is made so it can find the best solution to a system of coordinates. The solution is found when
 * all coordinates are connected to each other with line segments. No line segment may intersect each other.
 */
public class LineBuilder {

    /*
        TODO - Fix back()
        TODO - Fix step()
        TODO - Create Flag class
     */

    public enum StepResult {

        SUCCESSFUL(true),
        FAILED(false);

        private boolean worked;

        StepResult(boolean b) {
            worked = b;
        }

        public boolean getResult() {
            return worked;
        }

    }

    public class CannotGoBackException extends Exception {
        public CannotGoBackException(String message) {
            super(message);
        }
    }

    static public class Step {

        boolean connectLast;

        private ArrayList<Coordinate> availableCoordinates;
        private ArrayList<Coordinate> lastAvailable;
        private ArrayList<Segment> createdLines;
        private ArrayList<Step> steps;
        private ArrayList<Flag> flags;

        Segment lastLine;

        StepResult stepResult;

        Step(LineBuilder lnBldr) {
            connectLast = lnBldr.connectLast;
            availableCoordinates = new ArrayList<>(lnBldr.availableCoordinates);
            lastAvailable = new ArrayList<>(lnBldr.lastAvailable);
            createdLines = new ArrayList<>(lnBldr.createdLines);
            steps = new ArrayList<>(lnBldr.steps);
            flags = new ArrayList<>(lnBldr.flagList);
            lastLine = lnBldr.lastLine;
        }

        public void setStepResult(StepResult sr) {
            stepResult = sr;
        }

        public ArrayList<Coordinate> getAvailableCoordinates() {
            return availableCoordinates;
        }
        public ArrayList<Coordinate> getLastAvailable() {
            return lastAvailable;
        }
        public ArrayList<Segment> getCreatedLines() {
            return createdLines;
        }
        public ArrayList<Step> getSteps() {
            return steps;
        }
        public Segment getLastLine() {
            return lastLine;
        }
        public ArrayList<Flag> getFlags() {
            return flags;
        }
        public StepResult getStepResult() {
            return stepResult;
        }

    }

    /**
     * A flag stops a coordinate from being selected in the step() process.
     */
    static public class Flag {

        private Coordinate coordinate;

        public Flag(Coordinate flag) {
            coordinate = flag;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

    }

    private boolean connectLast;

    private ArrayList<Segment> createdLines = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();

    private ArrayList<Flag> flagList = new ArrayList<>();

    private ArrayList<Coordinate> originalCoordinates;
    private ArrayList<Coordinate> availableCoordinates;
    private ArrayList<Coordinate> lastAvailable;

    private Coordinate originPoint;

    private Segment lastLine;

    public LineBuilder(ArrayList<Coordinate> coordinateList) {
        if(coordinateList.size() == 0) throw new IllegalArgumentException("Given list contains no coordinates.");
        connectLast = false;
        originalCoordinates = new ArrayList<>(coordinateList);
        availableCoordinates = new ArrayList<>(coordinateList);
        originPoint = coordinateList.get(0);
    }

    public static void print(Object[] array) {
        for(int i = 0; i < array.length; i++)
            if(array.length==1) System.out.println("[" + array[i] + "]");
            else if(i == 0) System.out.print("[" + array[i] + ", ");
            else if (i == array.length - 1) System.out.println(array[i] + "]");
            else System.out.print(array[i] + ", ");
    }

    public static int findSmallestValue(ArrayList<Double> array) {
        double smallestValue = array.get(0);
        int indexOfSmallestValue = 0;
        for(Double value : array) {
            if(value < smallestValue) {
                smallestValue = value;
                indexOfSmallestValue = array.indexOf(value);
            }
        }
        return indexOfSmallestValue;
    }

    public static double round(double value, int places) {
        System.out.println(value);
        if(places < 0) throw new IllegalArgumentException();
        java.math.BigDecimal temp = new java.math.BigDecimal(Double.toString(value));
        temp = temp.setScale(places, java.math.RoundingMode.HALF_UP);
        return temp.doubleValue();
    }

    public void rollback(Step step) {
        connectLast = step.connectLast;
        availableCoordinates = new ArrayList<>(step.availableCoordinates);
        lastAvailable = new ArrayList<>(step.lastAvailable);
        createdLines = new ArrayList<>(step.createdLines);
        steps = new ArrayList<>(step.steps);
        /* THIS LINE PREVENTS NEW LINES FROM USING THE OLD POINT
            TODO - Find replacement for this line (Preferably flags)
         */
        //availableCoordinates.remove(lastLine.getEnd());
        lastLine = step.lastLine;
    }

    public void back() throws CannotGoBackException {
        if(steps.size() == 0) throw new CannotGoBackException("Cannot go back.");
        rollback(steps.get(steps.size()-1));
    }

    public void step() {
        // If there are available coordinates, execute a step. Otherwise, reject the input.
        if(availableCoordinates.size() != 0) {

            lastAvailable = new ArrayList<>(availableCoordinates);

            // If there is only one coordinate left to be connected, add the starting point back.
            if(availableCoordinates.size() == 1) {
                availableCoordinates.add(originPoint);
                connectLast = true;
            }

            // Create step point to go back to
            Step thisStep = new Step(this);
            steps.add(thisStep);

            // Find the next best line
            Segment candidate = findNextBestLine();

            // Check if the suggested line intersects any other lines.
            for(Segment line : createdLines)
                // If it does intersect, add back the starting point and remove the end point as to stop it from choosing this line again, then exit.
                if(candidate.doesIntersect(line)) {
                    Flag flag = new Flag(candidate.intersect(line));
                    flagList.add(flag);
                    thisStep.setStepResult(StepResult.FAILED);
                    return;
                }

            // If valid line, add it to createdLines, make it the lastLine, and make the end point of the line the starting point of the list.
            lastLine = candidate;
            createdLines.add(candidate);
            availableCoordinates.remove(candidate.getStart());
            availableCoordinates.remove(candidate.getEnd());
            availableCoordinates.add(0,candidate.getEnd());
            thisStep.setStepResult(StepResult.SUCCESSFUL);

            // If this step was the last step
            if(connectLast) {
                availableCoordinates.remove(originPoint);
            }

        } else System.out.println("No more lines can be drawn.");
    }

    private Segment findNextBestLine() {
        ArrayList<Coordinate> hand = new ArrayList<>(availableCoordinates);

        Coordinate start = hand.get(0);
        hand.remove(start);

        ArrayList<Double> combined = new ArrayList<>();

        for(Coordinate candidate : hand) {
            Segment hyp = new Segment(start, candidate);
            Coordinate rightAngle = new Coordinate(start.getX(), candidate.getY());
            Segment adj = new Segment(start, rightAngle);
            double angle = Math.toDegrees(Math.acos(adj.getDistance() / hyp.getDistance()));

            combined.add(angle*hyp.getDistance());
        }

        int indexOfSmallestCombined = findSmallestValue(combined);

        return new Segment(start, hand.get(indexOfSmallestCombined));
    }

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public ArrayList<Coordinate> getOriginalCoordinates() {
        return originalCoordinates;
    }
    public ArrayList<Coordinate> getAvailableCoordinates() {
        return availableCoordinates;
    }
    public ArrayList<Segment> getCreatedLines() {
        return createdLines;
    }
    public ArrayList<Step> getSteps() {
        return steps;
    }
    public ArrayList<Flag> getFlags() {
        return flagList;
    }
    public Segment getLastLine() {
        return lastLine;
    }
    // </editor-fold>

}