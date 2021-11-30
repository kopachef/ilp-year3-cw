package uk.ac.ed.inf;

import uk.ac.ed.inf.utils.Settings;

public class Drone {

  /** Drone class representing our drone at an object level within the program. */
  private int movementCount;

  private DroneState droneState;
  private boolean hasOrder;
  private int batteryLevel;
  private final int batteryCapacity;
  private LongLat currentPosition;
  private final double movementStepDistance;
  private FoodOrder currentFoodOrder;

  public Drone(int batteryCapacity) {
    movementCount = 0;
    droneState = DroneState.STATIONARY;
    hasOrder = false;
    this.batteryCapacity = batteryCapacity;
    batteryLevel = this.batteryCapacity;
    currentPosition = Settings.getDefaultHomeLocation();
    movementStepDistance = Settings.getDefaultMovementStepDistance();
  }

  /**
   * Calculate and return the battery cost of traversing the given distance. Distance is given in
   * degrees
   *
   * @param distanceInDegrees distance for which battery cost is to calculated. This is expected to
   *     be in degrees.
   * @return return the battery units incurred by travelling the given distance.
   */
  public int calculateMovementStepCost(double distanceInDegrees) {
    return (int) (distanceInDegrees / movementStepDistance);
  }

  /**
   * Given a longLat position, the drone will move to the specified location and update the battery
   * level depending on the travel cost.
   *
   * @param destination destination LongLat
   */
  public void moveTo(LongLat destination) {
    int stepCost = calculateMovementStepCost(currentPosition.distanceTo(destination));
    batteryLevel -= stepCost;
    currentPosition = destination;
    movementCount += stepCost;
  }

  /**
   * Returns the number of steps that have been taken by the drone so far.
   *
   * @return integer representing the number of steps taken by the drone so far.
   */
  public int getStepCount() {
    return movementCount;
  }

  /**
   * Returns the current LongLat position of the drone.
   *
   * @return LongLat associated with the current position of the drone.
   */
  public LongLat getCurrentPosition() {
    return currentPosition;
  }

  /**
   * Changes the current position of the drone to the ine given as an argument.
   *
   * @param currentPosition LongLat position to which the drone will be set.
   */
  public void setCurrentPosition(LongLat currentPosition) {
    this.currentPosition = currentPosition;
  }

  /**
   * Return the battery level if the drone.
   *
   * @return Current drone battery level as an int.
   */
  public int getBatteryLevel() {
    return batteryLevel;
  }

  /**
   * Sets the drone battery level to the one specified as the argument.
   *
   * @param batteryLevel new value for the drone battery level.
   */
  public void setBatteryLevel(int batteryLevel) {
    this.batteryLevel = batteryLevel;
  }

  /**
   * Hovers the drone for a single step. Each hover costs one step and will be updated by this
   * function.
   */
  public void hoverDrone() {
    // System.out.println("Hovered: " + batteryLevel);
    droneState = DroneState.HOVERING;
    batteryLevel -= 1;
  }

  /** Changes the drone state to the 'FLYING' state. */
  public void flyDrone() {
    droneState = DroneState.FLYING;
  }

  /**
   * Return the drone to a stationary state which is the default state for when the drone is not
   * flying.
   */
  public void returnToHome() {
    // System.out.println("retunred home with final battery :" + batteryLevel);
    droneState = DroneState.STATIONARY;
  }

  /**
   * Loads a food order onto the drone. Loading an item costs a single step and is reflected here.
   */
  public void loadItems() {
    if (droneState == DroneState.HOVERING && !hasOrder) {
      hasOrder = true;
    }
  }

  /**
   * This method clears the list of items.
   */
  public void unloadItems() {
    hasOrder = false;
  }

  /**
   * This method returns the droneState.
   *
   * @return current drone state
   */
  public DroneState getDroneState() {
    return droneState;
  }

  /**
   * The getCurrentFoodOrder() method returns the currentFoodOrder object.
   *
   * @return current food order.
   */
  public FoodOrder getCurrentFoodOrder() {
    return currentFoodOrder;
  }

  /**
   * This method sets the currentFoodOrder to the given FoodOrder.
   *
   * @param foodOrder food order to be set
   */
  public void setCurrentFoodOrder(FoodOrder foodOrder) {
    currentFoodOrder = foodOrder;
  }

  /**
   * The resetBatterLevel() function resets the batter level to the default level.
   */
  public void resetBatterLevel() {
    this.setBatteryLevel(Settings.getDefaultBatteryLevel());
  }

  /**
   * The DroneState enumeration defines the possible states that a drone can be in. The enumeration has three possible
   * values: FLYING, STATIONARY, and HOVERING.
   */
  public enum DroneState {
    FLYING,
    STATIONARY,
    HOVERING
  }
}
