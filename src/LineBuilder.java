import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
 *
 * A linebuilder's purpose is to connect
 */
public class LineBuilder {

    private class Step {

        boolean connectLast;

        ArrayList<Coordinate> availableCoordinates;
        ArrayList<Coordinate> lastAvailable;
        ArrayList<Segment> createdLines;
        ArrayList<Step> steps;

        Segment lastLine;

        Step(boolean conLast, ArrayList<Coordinate> ava, ArrayList<Coordinate> last, ArrayList<Segment> lines, ArrayList<Step> lotsOfSteps, Segment laLine) {
            connectLast = conLast;
            availableCoordinates = ava;
            lastAvailable = last;
            createdLines = lines;
            steps = lotsOfSteps;
            lastLine = laLine;
            steps.add(this);
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

    private Segment findNextBestLine() {
        if(verbose) System.out.println("------------------------\n" +
        "      BEST SEGMENT");

        Coordinate start = availableCoordinates.get(0);
        availableCoordinates.remove(start);
        if(verbose) System.out.print("Starting new line at: " + start + "\n" +
        "Available Coordinates[" + availableCoordinates.size() + "]: ");
        if(verbose) Helper.print(availableCoordinates.toArray());

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

        int indexOfSmallestAngle = Helper.findSmallestValue(angles);
        int indexOfSmallestDistance = Helper.findSmallestValue(distances);
        int indexOfSmallestCombined = Helper.findSmallestValue(combined);

        Segment suggested = new Segment(start, availableCoordinates.get(indexOfSmallestCombined));

        if(verbose) System.out.println("Smallest Angle: " + availableCoordinates.get(indexOfSmallestAngle) + ", angle of " + Helper.round(angles.get(indexOfSmallestAngle),3) +
        "\nSmallest Distance: " + availableCoordinates.get(indexOfSmallestDistance) + ", distance of " + Helper.round(distances.get(indexOfSmallestDistance),3) +
        "\nSmallest Combined Distance: " + availableCoordinates.get(indexOfSmallestCombined) + ", combined distance of " + Helper.round(combined.get(indexOfSmallestCombined),3) +
        "\n\nSuggesting Segment: " + suggested);


        System.out.println("------------------------");
        return suggested;
    }

    public void rollback(Step step) {
        connectLast = step.connectLast;
        availableCoordinates = step.availableCoordinates;
        availableCoordinates.add(0,lastLine.getStart());
        lastAvailable = step.lastAvailable;
        createdLines = step.createdLines;
        steps = step.steps;
        availableCoordinates.remove(lastLine.getEnd());
        lastLine = step.lastLine;
    }

    public void back() {
        System.out.println(steps.size());
        if(steps.size() == 0) {
            System.out.println("Cannot go back.");
            return;
        }
        if(verbose) System.out.println("GOING BACK ONE STEP");
        if(verbose) System.out.print("Available Coordinates[" + availableCoordinates.size() +"]: ");
        if(verbose) Helper.print(availableCoordinates.toArray());
        rollback(steps.get(steps.size()-1));
    }

    public void step() {
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

            if(verbose) System.out.print("Available Coordinates[" + availableCoordinates.size() +"]: ");
            if(verbose) Helper.print(availableCoordinates.toArray());

            Step thisStep = new Step(connectLast, availableCoordinates, lastAvailable, createdLines, steps, lastLine);
            steps.add(thisStep);

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
                Helper.print(createdLines.toArray());
            }
        } else System.out.println("No more lines can be drawn.");
    }

    public ArrayList<Segment> auto() {
        while(availableCoordinates.size() != 0)
            step();
        System.out.println("Completed.");
        return createdLines;
    }

    public LineBuilder(boolean verboseMode, ArrayList<Coordinate> coordinateList) {
        if(coordinateList.size() == 0) throw new IllegalArgumentException("Given list contains no coordinates.");
        verbose = verboseMode;
        connectLast = false;
        originalCoordinates = (ArrayList<Coordinate>) coordinateList.clone();
        availableCoordinates = coordinateList;
        originPoint = coordinateList.get(0);
        if(verbose) {
            System.out.print("Original Coordinates[" + availableCoordinates.size() + "]: ");
            Helper.print(coordinateList.toArray());
            System.out.println("Origin Point: " + originPoint);
        }
    }

}