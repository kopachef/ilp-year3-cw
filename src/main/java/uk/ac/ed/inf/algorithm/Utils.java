package uk.ac.ed.inf.algorithm;

import org.apache.commons.math3.util.Precision;

public class Utils {

    public static double roundOffToNearestMultiple(double value, double multiple) {
        System.out.println("Rounded bearing: " + Precision.round((Math.round(value / multiple) * multiple), 6));
        return Precision.round((Math.round(value / multiple) * multiple), 6);
    }

    public static int roundOffToNearest10th(double value) {
        System.out.println("Nearest 10th: " + (int) (Math.round(value / 10.0) * 10));
        return (int) (Math.round(value / 10) * 10);
    }
}
