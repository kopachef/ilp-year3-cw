package uk.ac.ed.inf;

import java.util.Calendar;
public class Drone {

    /**
     * Drone class representing our drone at an object level within the program.
     */
    private int movementCount;
    private DroneState droneState;
    private boolean hasOrder;
    private int batteryLevel;
    private int batteryCapacity;
    private LongLat currentPosition;
    private double movementStepDistance;

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
     * Calculate and return the battery cost of traversing the given distance. Distance is given and degrees
     *
     * @param distanceInDegrees distance for which battery cost is to calculated. This is expected to be in degrees.
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
    public void moveTo(LongLat destination){
        int stepCost = calculateMovementStepCost(currentPosition.distanceTo(destination));
        if(stepCost >= batteryLevel) {
            batteryLevel -= stepCost;
            currentPosition = destination;
        }
    }

    /**
     * Will turn the drone to face a specified angle direction. The angle is oriented with respect to the
     * north and could be considered a the bearing.
     *
     * @param angle angle that the drone will be facing and turning.
     */
    public void turn(int angle) {
        return;
    }

    /**
     * Returns the number of steps that have been taken by the drone so far.
     *
     * @return integer representing the number of steps taken by the drone so far.
     */
    public int getStepCount(){
        return movementCount;
    }

    /**
     * Returns the current LongLat position of the drone.
     *
     * @return LongLat associated with the current position of the drone.
     */
    public LongLat getCurrentPosition(){
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
    public int getBatteryLevel(){
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
     * Hovers the drone for a single step. Each hover costs one step and will be updated by this function.
     */
    public void hoverDrone() {
        droneState = DroneState.HOVERING;
        batteryLevel -= 1;
    }

    /**
     * Changes the drone state to the 'FLYING' state.
     */
    public void flyDrone() {
        droneState = DroneState.FLYING;
    }

    /**
     * Return the drone to a stationary state which is the default state for when  the drone is not flying.
     */
    public void returnToHome() {
        droneState = DroneState.STATIONARY;
    }

    /**
     * Loads a food order onto the drone. Loading an item costs a single step and is reflected here.
     *
     * @param order FoodOrder to be loaded onto the drone.
     */
    public void loadItems(FoodOrder order) {
        if(droneState == DroneState.HOVERING && !hasOrder) {
            hasOrder = true;
            batteryLevel -= 1;
        }
    }

    /**
     * Enum representing the different states the drone can be in.
     */
    private enum DroneState {
        FLYING, STATIONARY, HOVERING
    }

}
