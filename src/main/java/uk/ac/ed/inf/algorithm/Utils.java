package uk.ac.ed.inf.algorithm;

import org.apache.commons.math3.util.Precision;

public class Utils {

    /**
     * The roundOffToNearestMultiple() method rounds off the given number to the nearest multiple
     *
     * @param value value to be rounded
     * @param multiple multiple to round close to.
     * @return rounded value
     */
    public static double roundOffToNearestMultiple(double value, double multiple) {
        return Precision.round((Math.round(value / multiple) * multiple), 6);
    }

    /**
     * the roundOffToNearest10th() method rounds off the number to the nearest multiple of 10.
     * @param value value to be rounded.
     * @return rounded value.
     */
    public static int roundOffToNearest10th(double value) {
        return (int) (Math.round(value / 10) * 10);
    }

    public static boolean isAMulitpleOf10(double angle) {
        return Precision.round(angle, 2) == Precision.round(roundOffToNearest10th(angle), 2);
    }

    public static boolean isWholeNumber(double d) {
        return d % 1 == 0;
    }

}
