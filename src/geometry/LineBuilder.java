package geometry;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 3/18/2019
 *
 * The purpose of a line builder is to build lines when given a series of points. It attempts to do this without
 * intersecting any lines.
 */
public class LineBuilder {

    /*
        TODO - Find next point, but don't solve
     */

    // <editor-fold defaultstate="collapsed" desc="Nested Classes">
    /**
     * Custom exception for when the line builder cannot go back anymore. This is usually caused
     * because the step list is empty.
     */
    public static class CannotGoBackException extends Exception {
        private CannotGoBackException(String message) {
            super(message);
        }
    }

    /**
     * A step is the representation of the line builder at a given moment. It saves what coordinates
     * are available, the created lines, the steps it has, the flags you've placed, and more. This was made
     * so that you can undo an action with a line builder.
     */
    public static class Step {

        /**
         * An enum for the results of a step. A successful step means that the step found a coordinate
         * to go to and it wasn't a flag and a line connected to it didn't intersect with any other lines.
         * A failed step means the step intersected a line or the coordinate was on a flag. No more lines
         * means it could not make any lines.
         */
        public enum StepResult {

            SUCCESSFUL,
            FAILED,
            NO_MORE_LINES,
            COMPLETE

        }

        private boolean connectLast;
        private ArrayList<Coordinate> availableCoordinates;
        private ArrayList<Coordinate> lastAvailable;
        private ArrayList<Segment> createdLines;
        private ArrayList<Step> steps;
        private ArrayList<Flag> flags;
        private Segment lastLine;
        private Step lastStep;
        private StepResult stepResult;

        private Step(LineBuilder lineBuilder, StepResult result) {
            this(lineBuilder);
            setStepResult(result);
        }
        private Step(LineBuilder lineBuilder) {
            connectLast = lineBuilder.connectLast;
            availableCoordinates = new ArrayList<>(lineBuilder.availableCoordinates);
            lastAvailable = new ArrayList<>(lineBuilder.lastAvailable);
            createdLines = new ArrayList<>(lineBuilder.createdLines);
            steps = new ArrayList<>(lineBuilder.steps);
            flags = new ArrayList<>(lineBuilder.flagList);
            lastLine = lineBuilder.lastLine;
            lastStep = lineBuilder.lastStep;
        }

        private void setStepResult(StepResult sr) {
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
        public Step getLastStep() {
            return lastStep;
        }
    }

    /**
     * A flag stops a coordinate from being selected in the step process. This allows
     * the user to control where they want the line to go.
     */
    static public class Flag {

        private Coordinate coordinate;  // Location of the flag

        public Flag(Coordinate flag) {
            coordinate = flag;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Variables"
    private boolean connectLast;

    private ArrayList<Segment> createdLines = new ArrayList<>();

    private ArrayList<Step> steps = new ArrayList<>();

    private ArrayList<Flag> flagList = new ArrayList<>();

    private ArrayList<Coordinate> originalCoordinates;

    private ArrayList<Coordinate> availableCoordinates;

    private ArrayList<Coordinate> lastAvailable;

    private Coordinate originPoint;

    private Segment lastLine;

    private Step lastStep;
    // </editor-fold>

    /**
     * This method commands the line builder to go back one step. It will go back if
     * there is a step to go back to, otherwise it will throw an exception.
     *
     * @throws CannotGoBackException If there are no steps to go back to, it will throw this exception.
     */
    public void back() throws CannotGoBackException {
        if(steps.size() == 0) throw new CannotGoBackException("Cannot go back.");
        rollback(lastStep);
    }

    /**
     * This method changes all of the values of the line builder to the given step.
     *
     * @param step The step to rollback to.
     */
    private void rollback(Step step) {
        connectLast = step.connectLast;
        availableCoordinates = new ArrayList<>(step.availableCoordinates);
        lastAvailable = new ArrayList<>(step.lastAvailable);
        createdLines = new ArrayList<>(step.createdLines);
        steps = new ArrayList<>(step.steps);
        lastLine = step.lastLine;
        lastStep = step.lastStep;
    }

    /**
     * Finds the next best line and validates it and creates a new step.
     */
    public void step() {
        // If there are available coordinates, execute a step. Otherwise, reject the input.
        if(availableCoordinates.size() != 0) {

            lastAvailable = new ArrayList<>(availableCoordinates);

            // Create step point to go back to
            Step thisStep = new Step(this);
            steps.add(thisStep);
            lastStep = thisStep;

            /*
            // If there is only one coordinate left to be connected, add the starting point back.
            if(availableCoordinates.size() == 1) {
                availableCoordinates.add(originPoint);
                connectLast = true;
            }
            */

            // Remove all flagged coordinates
            for(Flag flag : flagList) {
                availableCoordinates.remove(flag.coordinate);
            }

            // Find the next best line
            Segment candidate = findNextBestLine(availableCoordinates);

            // Check if the suggested line intersects any other lines.
            for(Segment line : createdLines)
                // If it does intersect, add back the starting point and remove the end point as to stop it from choosing this line again, then exit.
                if(candidate.doesIntersect(line)) {
                    thisStep.setStepResult(Step.StepResult.FAILED);
                    return;
                }
            // Check if suggested line intersects any flags
            for(Flag flag : flagList) {
                if(candidate.getStart().equals(flag.coordinate) || candidate.getEnd().equals(flag.coordinate)) {
                    thisStep.setStepResult(Step.StepResult.FAILED);
                    return;
                }
            }

            // If valid line, add it to createdLines, make it the lastLine, and make the end point of the line the starting point of the list.
            lastLine = candidate;
            createdLines.add(candidate);
            availableCoordinates.remove(candidate.getStart());
            availableCoordinates.remove(candidate.getEnd());
            availableCoordinates.add(0,candidate.getEnd());
            for(Flag flag : flagList) {
                availableCoordinates.add(flag.coordinate);
            }
            flagList = new ArrayList<>();
            thisStep.setStepResult(Step.StepResult.SUCCESSFUL);

            if(availableCoordinates.size() == 1 && lastLine.getEnd().equals(originPoint)) {
                availableCoordinates.clear();
                thisStep.setStepResult(Step.StepResult.COMPLETE);
            }

            if(availableCoordinates.size() == 1) availableCoordinates.add(originPoint);

        } else {
            Step thisStep = new Step(this, Step.StepResult.NO_MORE_LINES);
            steps.add(thisStep);
        }
    }

    /**
     * (Hypothetical step) Finds the next line without changing anything.
     */
    public Segment hypoStep() {
        ArrayList<Coordinate> coordinateList = new ArrayList<>(availableCoordinates);
        if(coordinateList.size() == 1) coordinateList.add(originPoint);
        for(Flag flag : flagList)
            coordinateList.remove(flag.coordinate);
        return findNextBestLine(coordinateList);
    }

    private Segment findNextBestLine(ArrayList<Coordinate> available) {
        ArrayList<Coordinate> hand = new ArrayList<>(available);

        Coordinate start = hand.get(0);
        hand.remove(start);

        ArrayList<Double> combined = new ArrayList<>();

        for(Coordinate candidate : hand) {
            Segment hyp = new Segment(start, candidate);
            Coordinate rightAngle = new Coordinate(start.getX(), candidate.getY());
            Segment adj = new Segment(start, rightAngle);
            double angle = Math.toDegrees(Math.acos(adj.getDistance() / hyp.getDistance()));

            combined.add(angle+hyp.getDistance());
        }

        int indexOfSmallestCombined = findSmallestValue(combined);

        return new Segment(start, hand.get(indexOfSmallestCombined));
    }

    private static int findSmallestValue(ArrayList<Double> array) {
        if(array.size() == 0) throw new IllegalArgumentException("Argument has 0 size.");
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

    public LineBuilder(ArrayList<Coordinate> coordinateList) {
        if(coordinateList.size() == 0) throw new IllegalArgumentException("Given list contains no coordinates.");
        connectLast = false;
        originalCoordinates = new ArrayList<>(coordinateList);
        availableCoordinates = new ArrayList<>(coordinateList);
        originPoint = coordinateList.get(0);
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
    public ArrayList<Coordinate> getFlaggedCoordinates() {
        ArrayList<Coordinate> list = new ArrayList<>();
        for(Flag flag : flagList)
            list.add(flag.getCoordinate());
        return list;
    }
    public Segment getLastLine() {
        return lastLine;
    }
    public Step getLastStep() throws CannotGoBackException {
        if(lastStep == null) throw new CannotGoBackException("No previous step to retrieve.");
        return lastStep;
    }
    // </editor-fold>

}