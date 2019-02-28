package geometry;

import geometry.Coordinate;
import geometry.Segment;

import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
 *
 * A linebuilder's purpose is to connect
 */
public class LineBuilder {

    public class CannotGoBackException extends Exception {
        public CannotGoBackException(String message) {
            super(message);
        }
    }

    public class Step {

        boolean connectLast;

        ArrayList<Coordinate> availableCoordinates;
        ArrayList<Coordinate> lastAvailable;
        ArrayList<Segment> createdLines;
        ArrayList<Step> steps;

        Segment lastLine;

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

        Step(boolean conLast, ArrayList<Coordinate> ava, ArrayList<Coordinate> last, ArrayList<Segment> lines, ArrayList<Step> lotsOfSteps, Segment laLine) {
            connectLast = conLast;
            availableCoordinates = new ArrayList<>(ava);
            lastAvailable = new ArrayList<>(last);
            createdLines = new ArrayList<>(lines);
            steps = new ArrayList<>(lotsOfSteps);
            lastLine = laLine;
        }

    }

    private boolean verbose, connectLast;

    private ArrayList<Segment> createdLines = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();

    private ArrayList<Coordinate> originalCoordinates;
    private ArrayList<Coordinate> availableCoordinates;
    private ArrayList<Coordinate> lastAvailable;

    private Coordinate originPoint;

    private Segment lastLine;


    public LineBuilder(boolean verboseMode, ArrayList<Coordinate> coordinateList) {
        if(coordinateList.size() == 0) throw new IllegalArgumentException("Given list contains no coordinates.");
        verbose = verboseMode;
        connectLast = false;
        originalCoordinates = new ArrayList<>(coordinateList);
        availableCoordinates = new ArrayList<>(coordinateList);
        originPoint = coordinateList.get(0);
        if(verbose) {
            System.out.print("Original Coordinates[" + availableCoordinates.size() + "]: ");
            print(coordinateList.toArray());
            System.out.println("Origin Point: " + originPoint);
        }
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

    private Segment findNextBestLine() {
        if(verbose) System.out.println("------------------------\n" +
        "      BEST SEGMENT");

        Coordinate start = availableCoordinates.get(0);
        availableCoordinates.remove(start);
        if(verbose) System.out.print("Starting new line at: " + start + "\n" +
        "Available Coordinates[" + availableCoordinates.size() + "]: ");
        if(verbose) print(availableCoordinates.toArray());

        ArrayList<Double> angles = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();
        ArrayList<Double> combined = new ArrayList<>();

        for(Coordinate candidate : availableCoordinates) {
            Segment hyp = new Segment(start, candidate);
            Coordinate rightAngle = new Coordinate(start.getX(), candidate.getY());
            Segment adj = new Segment(start, rightAngle);
            Double angle = Math.toDegrees(Math.acos(adj.getDistance() / hyp.getDistance()));

            if(verbose) System.out.println("Angle from origin to " + candidate + ":\t" + angle + "\nHyp length: " + hyp.getDistance() + "\nDistance * Angle: " + angle * hyp.getDistance() + "\n");

            angles.add(angle);
            distances.add(hyp.getDistance());
            combined.add(angle*hyp.getDistance());
        }

        int indexOfSmallestAngle = findSmallestValue(angles);
        int indexOfSmallestDistance = findSmallestValue(distances);
        int indexOfSmallestCombined = findSmallestValue(combined);

        Segment suggested = new Segment(start, availableCoordinates.get(indexOfSmallestCombined));

        if(verbose) System.out.println("Smallest Angle: " + availableCoordinates.get(indexOfSmallestAngle) + ", angle of " + round(angles.get(indexOfSmallestAngle),3) +
        "\nSmallest Distance: " + availableCoordinates.get(indexOfSmallestDistance) + ", distance of " + round(distances.get(indexOfSmallestDistance),3) +
        "\nSmallest Combined Distance: " + availableCoordinates.get(indexOfSmallestCombined) + ", combined distance of " + round(combined.get(indexOfSmallestCombined),3) +
        "\n\nSuggesting Segment: " + suggested);


        if(verbose) System.out.println("------------------------");
        return suggested;
    }

    public void rollback(Step step) {
        connectLast = step.connectLast;
        availableCoordinates = new ArrayList<>(step.availableCoordinates);
        lastAvailable = new ArrayList<>(step.lastAvailable);
        createdLines = new ArrayList<>(step.createdLines);
        steps = new ArrayList<>(step.steps);
        availableCoordinates.remove(lastLine.getEnd());
        lastLine = step.lastLine;
    }

    public void back() throws CannotGoBackException {
        if(steps.size() == 0) throw new CannotGoBackException("Cannot go back.");
        if(verbose) System.out.println("GOING BACK ONE STEP");
        if(verbose) System.out.print("Available Coordinates[" + availableCoordinates.size() +"]: ");
        if(verbose) print(availableCoordinates.toArray());
        rollback(steps.get(steps.size()-1));
    }

    public void step() throws CannotGoBackException {
        // If there are available coordinates, execute a step. Otherwise, reject the input.
        if(availableCoordinates.size() != 0) {

            // If the available coordinates are the same as last time, go back a step and proceed.
            if(availableCoordinates.equals(lastAvailable)) back();

            lastAvailable = new ArrayList<>(availableCoordinates);

            // If there is only one coordinate left to be connected, add the starting point back.
            if(availableCoordinates.size() == 1) {
                if (verbose) System.out.println("Added: " + originPoint);
                availableCoordinates.add(originPoint);
                connectLast = true;
            }

            Step thisStep = new Step(connectLast, availableCoordinates, lastAvailable, createdLines, steps, lastLine);
            steps.add(thisStep);

            if(verbose) System.out.print("Available Coordinates[" + availableCoordinates.size() +"]: ");
            if(verbose) print(availableCoordinates.toArray());

            Segment candidate = findNextBestLine();

            // Check if the suggested line intersects any other lines.
            for(Segment line : createdLines)
                // If it does intersect, add back the starting point and remove the end point as to stop it from choosing this line again, then exit.
                if(candidate.doesIntersect(line)) {
                    availableCoordinates.add(0, candidate.getStart());
                    availableCoordinates.remove(candidate.getEnd());
                    if(verbose) System.out.println("Suggested line is invalid: Intersects " + line + "." +
                    "\nRestored: " + candidate.getStart() + "\nRemoved: " + candidate.getEnd());
                    return;
                }

            // If valid line, add it to createdLines, make it the lastLine, and make the end point of the line the starting point of the list.
            if(verbose) System.out.println("Suggested line is valid." + "\nTranslated: " + candidate.getEnd() + " to availableCoordinates[0]");
            lastLine = candidate;
            createdLines.add(candidate);
            availableCoordinates.remove(candidate.getEnd());
            availableCoordinates.add(0,candidate.getEnd());

            // If this step was the last step
            if(connectLast) {
                if(verbose) System.out.println("Removed: " + originPoint);
                availableCoordinates.remove(originPoint);
            }

            if(verbose) {
                System.out.print("Current lines[" + createdLines.size() + "]: ");
                print(createdLines.toArray());
            }
        } else System.out.println("No more lines can be drawn.");
    }

    public boolean getVerbose() {
        return verbose;
    }
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

}