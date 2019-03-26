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

    // <editor-fold defaultstate="collapsed" desc="Nested Classes">
    /**
     * Custom exception for when the line builder cannot go back anymore. This is usually caused
     * because the step list is empty.
     */
    public static class CannotGoBackException extends RuntimeException {
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
        private ArrayList<Segment> createdLines;
        private ArrayList<Step> steps;
        private Segment lastLine;
        private Step lastStep;
        private StepResult stepResult;

        private Step(LineBuilder lineBuilder) {
            connectLast = lineBuilder.connectLast;
            availableCoordinates = new ArrayList<>(lineBuilder.availableCoordinates);
            createdLines = new ArrayList<>(lineBuilder.linesList);
            steps = new ArrayList<>(lineBuilder.steps);
        }

        private void setStepResult(StepResult sr) {
            stepResult = sr;
        }

        public ArrayList<Coordinate> getAvailableCoordinates() {
            return availableCoordinates;
        }
        public StepResult getStepResult() {
            return stepResult;
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

    private ArrayList<Segment> linesList = new ArrayList<>();

    private ArrayList<Step> steps = new ArrayList<>();

    private ArrayList<Flag> flagList = new ArrayList<>();

    private ArrayList<Coordinate> originalCoordinates;

    private ArrayList<Coordinate> availableCoordinates;

    private Coordinate originPoint, currentCoordinate;
    // </editor-fold>

    /**
     * This method commands the line builder to go back one step. It will go back if
     * there is a step to go back to, otherwise it will throw an exception.
     *
     * @throws CannotGoBackException If there are no steps to go back to, it will throw this exception.
     */
    public void back() throws CannotGoBackException {
        if(steps.size() == 0) throw new CannotGoBackException("Cannot go back.");
        rollback(getLastStep());
    }
    /**
     * This method changes all of the values of the line builder to the given step.
     *
     * @param step The step to rollback to.
     */
    private void rollback(Step step) {
        connectLast = step.connectLast;
        availableCoordinates = new ArrayList<>(step.availableCoordinates);
        linesList = new ArrayList<>(step.createdLines);
        steps = new ArrayList<>(step.steps);
    }

    /**
     * Finds the next best line and validates it and creates a new step.
     */
    public void step() {
        if(availableCoordinates.size() == 0) {
            Step thisStep = new Step(this);
            thisStep.setStepResult(Step.StepResult.NO_MORE_LINES);
            steps.add(thisStep);
        }

        Step step = new Step(this);
        steps.add(step);

        for(Flag flag : flagList)
            availableCoordinates.remove(flag.coordinate);

        Segment candidate = findNextBestLine(availableCoordinates);

        for(Segment line : linesList)
            if(candidate.doesIntersect(line)) {
                step.setStepResult(Step.StepResult.FAILED);
                return;
            }

        linesList.add(candidate);
        availableCoordinates.remove(candidate.getStart());
        availableCoordinates.remove(candidate.getEnd());
        availableCoordinates.add(0,candidate.getEnd());
        for(Flag flag : flagList)
            availableCoordinates.add(flag.coordinate);
        flagList = new ArrayList<>();
        step.setStepResult(Step.StepResult.SUCCESSFUL);

        if(availableCoordinates.size() == 1 && getLastLine().getEnd().equals(originPoint)) {
            availableCoordinates = new ArrayList<>();
            step.setStepResult(Step.StepResult.COMPLETE);
        }

        if(availableCoordinates.size() == 1) availableCoordinates.add(originPoint);
    }

    private enum searchMethod {
        SMALLEST_ANGLE,
        SMALLEST_DISTANCE
    }

    public synchronized Segment improvedStep() {
        if(availableCoordinates.size() == 0) throw new IllegalArgumentException("'availableCoordinates' has 0 size.");

        Step step = new Step(this);
        steps.add(step);

        for(Flag flag : flagList)
            availableCoordinates.remove(flag.coordinate);

        currentCoordinate = availableCoordinates.get(0);
        availableCoordinates.remove(currentCoordinate);
        ArrayList<Segment> possibleLines = new ArrayList<>();
        for(Coordinate candidate : availableCoordinates)
            possibleLines.add(new Segment(currentCoordinate, candidate));
        for(Segment candidate : possibleLines)
            for(Segment line : linesList)
                if(candidate.doesIntersect(line))
                    possibleLines.remove(candidate);
        sortByLength(possibleLines);
        step.setStepResult(Step.StepResult.SUCCESSFUL);
        Segment selected = possibleLines.get(0);
        linesList.add(selected);
        availableCoordinates.remove(getLastLine().getEnd());
        availableCoordinates.add(0,getLastLine().getEnd());
        return selected;
    }

    public static void sortByLength(ArrayList<Segment> lines) {
        Segment smallestLine = lines.get(0);
        Segment lastLine = lines.get(0);
        sortLoop: while(true) {
            for(Segment line : lines) {
                if(line == lines.get(lines.size()-1)) break sortLoop;
                if(line.getDistance() > lines.get(lines.indexOf(line)+1).getDistance()) break;
            }
            for(int i = 0; i < lines.size(); i++) {
                Segment currentLine = lines.get(i);
                if(currentLine == smallestLine) {
                    lastLine = currentLine;
                    continue;
                }
                if(currentLine.getDistance() < smallestLine.getDistance()) {
                    lines.remove(currentLine);
                    lines.add(0, currentLine);
                    smallestLine = currentLine;
                    continue;
                }
                if(currentLine.getDistance() < lastLine.getDistance()) {
                    int index = lines.indexOf(lastLine)+1;
                    lines.remove(lastLine);
                    lines.add(index, lastLine);
                    continue;
                }
                lastLine = new Segment(currentLine);
            }
        }
    }

    public static Segment findNextBestLine(ArrayList<Coordinate> available) {
        if(available.size() == 0) throw new IllegalArgumentException("Argument 'available' is 0: " + available);
        ArrayList<Coordinate> hand = new ArrayList<>(available);

        Coordinate start = hand.get(0);
        hand.remove(start);

        ArrayList<Double> combined = new ArrayList<>();

        for(Coordinate candidate : available)
            combined.add(start.angle(candidate));

        return new Segment(start, available.get(combined.indexOf(findLargestValue(combined))));
    }

    private static double findSmallestValue(ArrayList<Double> list) {
        if(list.size() == 0) throw new IllegalArgumentException("Argument 'list' is 0: " + list);
        double smallestValue = list.get(0);
        for(Double value : list)
            if(value < smallestValue)
                smallestValue = value;
        return smallestValue;
    }

    private static double findLargestValue(ArrayList<Double> list) {
        if(list.size() == 0) throw new IllegalArgumentException("Argument 'list' is 0: " + list);
        double largestValue = list.get(0);
        for(Double value : list)
            if(value > largestValue)
                largestValue = value;
        return largestValue;
    }

    public LineBuilder(ArrayList<Coordinate> coordinateList) {
        if(coordinateList.size() == 0) throw new IllegalArgumentException("Given list contains no coordinates.");
        if(coordinateList.size() == 1) throw new IllegalArgumentException("Need at least 3 coordinates to make a polygon.");
        connectLast = false;
        originalCoordinates = new ArrayList<>(coordinateList);
        availableCoordinates = new ArrayList<>(coordinateList);
        originPoint = coordinateList.get(0);
    }

    // <editor-fold defaultstate="collapsed" desc="Accessor Methods">
    public ArrayList<Coordinate> getOriginalCoordinates() {
        return originalCoordinates;
    }
    public Coordinate getOriginPoint() {
        return originPoint;
    }
    public ArrayList<Coordinate> getAvailableCoordinates() {
        return availableCoordinates;
    }
    public ArrayList<Segment> getLinesList() {
        return linesList;
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
    public Step getLastStep() throws CannotGoBackException {
        if(steps.size()-1 < 0) throw new CannotGoBackException("No previous step to retrieve.");
        return steps.get(steps.size()-1);
    }
    public Segment getLastLine() {
        return linesList.get(linesList.size()-1);
    }
    // </editor-fold>

}