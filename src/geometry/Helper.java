package geometry;

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
        System.out.println(value);
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
        return (int) random(min, max);
    }

    // Print out the contents of an array
    public static void print(Object[] array) {
        for(int i = 0; i < array.length; i++)
            if(array.length==1) System.out.println("[" + array[i] + "]");
            else if(i == 0) System.out.print("[" + array[i] + ", ");
            else if (i == array.length - 1) System.out.println(array[i] + "]");
            else System.out.print(array[i] + ", ");
    }

}
