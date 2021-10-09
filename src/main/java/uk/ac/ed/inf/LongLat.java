package uk.ac.ed.inf;

import org.jetbrains.annotations.NotNull;

/**
 * This class provides a blueprint for the definition of a LongLat object. This object is mean't to represent a
 * physical location of the surface of the earth using a pair of coordinates representiing the longitude and latitude
 * values respectively.
 *
 * <p>Further utility functions are provided within the class to enable easier operations on an instances of this
 * class</>
 *
 * @author Martin Namukombo
 */
public class LongLat {

  public double longitude;
  public double latitude;
  Settings settings;

  public LongLat(double longitude, double latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
    settings = new Settings();
  }


  @NotNull
  /**
   * Returns true if the current instance of the LongLat object is within the confinment area
   * Confinement area is defined by longitude and latitude values specified in our settings file.
   *
   * <p>Location coordinates must be exclusively within the bounds defined in our settings file. Points
   * that lie at the boundary will not count as confined.</p>
   *
   * @return True if instance is within boundary, False otherwise.
   */
  public boolean isConfined() {
    return isInRange(settings.getDefaultNorthwestBoundLongitude(), settings.getDefaultSoutheastBoundLongitude(), longitude)
        && isInRange(settings.getDefaultSoutheastBoundLatitude(), settings.getDefaultNorthwestBoundLatitude(), latitude);
  }

  @NotNull
  /**
   * Called on an instance of LongLat and calculates distance between the instance given as an argument and
   * the instance it is called on.
   *
   * @param longLat instance of LongLat to which we are calculating the instance to.
   * @return        returns the calculated distance as double.
   */
  public double distanceTo(LongLat longLat) {
    return calculateDistance(longitude, latitude, longLat.longitude, longLat.latitude);
  }

  @NotNull
  /**
   * Returns a boolean value denoting whether the instance given as an argument is close to the instance it is
   * called on.
   *
   * <p> A LongLat instance is close to the instance it is called on if the euclidean distance between the two
   * instances is less than or equal to the closeness threshold defined in our settings file.</p>
   *
   * @return  True if calculated distance is less than threshold, False otherwise.
   */
  public boolean closeTo(LongLat longLat) {
    return isInRange(
            (longitude - settings.getDefaultClosenessTolerance()), (longitude + settings.getDefaultClosenessTolerance()), longLat.longitude)
        && isInRange(
            (latitude - settings.getDefaultClosenessTolerance()), (latitude + settings.getDefaultClosenessTolerance()), longLat.latitude);
  }

  @NotNull
  /**
   * This method returns a new LongLat object whose new location coordinates are the coordinates resulting from
   * take single step from the current instances' location in the direction of the provided angle.
   *
   * <p> Instance returned as output will only have new coordinates if the provided angle is a valid 'Move' angle
   * (with an exception to the 'hover' angle) otherwise generated instance will have the same coordinates as the
   * instance it is called on. </p>
   *
   * @param angle angle direction to take a single step in.
   * @return      new LongLat instance resulting from taking a step in provided angle direction.
   */
  public LongLat nextPosition(int angle) {
    if (validMove(angle)) {
      double destination_longitude =
              longitude + settings.getDefaultMovementStepDistance() * Math.cos(angle * Math.PI / 180);
      double destination_latitude =
              latitude + settings.getDefaultMovementStepDistance() * Math.sin(angle * Math.PI / 180);
      return new LongLat(destination_longitude, destination_latitude);
    } else {
      return new LongLat(longitude, latitude);
    }
  }

  /**
   * Helper function to verify the validity of a provided move angle. Angled validity is specified as being a multiple
   * 10 and falling within the range of -1 and 351.
   *
   * @param angle Provided move angle to check validity for.
   * @return      True if provided angled fits criteria, False otherwise.
   */
  private boolean validMove(int angle) {
    return angle % 10 == 0 && isInRange(-1, 351, angle);
  }

  /**
   * Helper function that checks if a given value falls within the range of the provided boundary values.
   *
   * <p>Value falls within the range of two bounds if it falls in between them but is not equal to either of them</p>
   *
   * @param lowerBound lower bound double value
   * @param upperBound upper bound double value
   * @param testValue double value to check if it falls within the bounds.
   * @return          True if the test value falls within our bounds, False otherwise.
   */
  private boolean isInRange(double lowerBound, double upperBound, double testValue) {
    return lowerBound < testValue && upperBound > testValue;
  }

  /**
   * Helper function the calculates the euclidean distance between a pair of coordinates.
   *
   * @param x1 x value of the first coordinate.
   * @param y1 y value of the first coordinate.
   * @param x2 x value of the second coordinate.
   * @param y2 y value of the second coordinate.
   * @return   Returns the calculated euclidean distance between the provided set of coordinates.
   */
  private double calculateDistance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));

  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object
   *
   * Returns true if given LongLat object represents same coordinates as current instance of LongLat.
   *
   * @param longLat longLat object to compare against.
   * @return        True if given instance represents the same set of coordinates.
   */
  @Override
  public boolean equals(Object longLat) {
    boolean result;
    if((longLat == null) || (getClass() != longLat.getClass())){
      result = false;
    } else {
      LongLat inputLongLat = (LongLat) longLat;
      result = longitude == inputLongLat.longitude
              && latitude == inputLongLat.latitude;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object
   *
   * Returns a String representation of a LongLat object.
   *
   * @return String representing the current LongLat object.
   */
  @Override
  public String toString() {
    return "Longitude: " + longitude + "\nLatitude: " + latitude;
  }

  /**
   * Dumps current state and attributes of the LongLat object it is called on.
   */
  public void dump() {
    System.out.println(toString());
  }
}