import java.util.ArrayList;

/**
 * @author Alejandro Doberenz
 * @version 2/23/2019
 *
 * This class contains some helper methods that looked weird being in the middle of my other classes.
 */
public class Helper {

    // Rounds a double to a given amount of places
    public static double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();
        java.math.BigDecimal temp = new java.math.BigDecimal(Double.toString(value));
        temp = temp.setScale(places, java.math.RoundingMode.HALF_UP);
        return temp.doubleValue();
    }

    // Generate a random double with a lower and upper bound
    public static double random(double min, double max) {
        return round((Math.random() * (max - min) + min), 3);
    }

    // Generate a random integer with a lower and upper bound
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    // Print out the contents of an array
    public static void print(Object[] array) {
        for(int i = 0; i < array.length; i++)
            if(array.length==1) System.out.println("[" + array[i] + "]");
            else if(i == 0) System.out.print("[" + array[i] + ", ");
            else if (i == array.length - 1) System.out.println(array[i] + "]");
            else System.out.print(array[i] + ", ");
    }

    public static String asString(Object[] array) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < array.length; i++)
            if(array.length==1) builder.append("[").append(array[i]).append("]");
            else if(i == 0) builder.append("[").append(array[i]).append(", ");
            else if (i == array.length - 1) builder.append(array[i]).append("]");
            else builder.append(array[i]).append(", ");
        return builder.toString();
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

}
