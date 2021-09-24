package uk.ac.ed.inf;

import org.w3c.dom.ranges.Range;

import java.util.Random;

public class LongLat {

    private static double NORTHWEST_BOUND_LONGITUDE = -3.184319;
    private static double NORTHWEST_BOUND_LATITUDE = 55.942617 ;
    private static double SOUTHEAST_BOUND_LONGITUDE = -3.192473;
    private static double SOUTHEAST_BOUND_LATITUDE = 55.946233;

    private static double CLOSENESS_TOLERANCE = 0.00015;

    public double longitude;
    public double latitude;

    //temp
    // TODO remove me
    LongLat longLat = new LongLat(0.1,0.1);
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined() {
        return isInRange(NORTHWEST_BOUND_LONGITUDE,SOUTHEAST_BOUND_LONGITUDE,longitude)
                && isInRange(NORTHWEST_BOUND_LATITUDE,SOUTHEAST_BOUND_LATITUDE,latitude);
    }

    public double distanceTo(LongLat longLat) {
        return calculateDistance(longitude, longLat.longitude, latitude, longLat.latitude);
    }

    public boolean closeTo(LongLat longLat) {
        //TODO implement me
        //reutnr true if the points are close to each other based on def in page 3
        return isInRange((longitude - CLOSENESS_TOLERANCE),(longitude + CLOSENESS_TOLERANCE), longLat.longitude)
                && isInRange((latitude - CLOSENESS_TOLERANCE), (latitude + CLOSENESS_TOLERANCE), longLat.latitude);
    }

    public LongLat nextPosition(int angle) {
        //TODO implement me
        //returns next longlat position if given angle is executed
        return new LongLat(longitude + (double) angle, latitude + (double) angle);
    }

    private boolean isInRange(double lowerBound, double upperBound, double testValue) {
        return lowerBound < testValue && upperBound > testValue;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }


}
