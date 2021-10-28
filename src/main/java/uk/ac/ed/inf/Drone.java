package uk.ac.ed.inf;

import java.util.Calendar;
public class Drone {


    private int droneId;
    private int movementCount;
    private DroneState droneState;
    private boolean hasOrder;
    private int batteryLevel;
    private int batteryCapacity;
    private LongLat currentPosition;
    private double currentAngle;
    private Calendar currentDate;
    private double movementStepDistance;
    private double droneWeight;

    private enum DroneState {
        FLYING, STATIONARY, HOVERING
    }

    public Drone(int droneId, int batteryCapacity) {
        Calendar calenderInstance = Calendar.getInstance();
        calenderInstance.clear();
        calenderInstance.set(2019,8,21,12,30,12);
        this.currentDate = calenderInstance;
        this.droneId = droneId;
        movementCount = 0;
        droneState = DroneState.STATIONARY;
        hasOrder = false;
        this.batteryCapacity = batteryCapacity;
        batteryLevel = this.batteryCapacity;
        currentPosition = Settings.getDefaultHomeLocation();
        currentAngle = 0;
        movementStepDistance = Settings.getDefaultMovementStepDistance();
        droneWeight = 0;
    }

    public int calculateMovementStepCost(double distanceDegrees) {
        return (int) (distanceDegrees / movementStepDistance);
    }

    /**
     * ignore angel for now, comme back later.
     * @param destination
     */
    public void moveTo(LongLat destination){
        int stepCost = calculateMovementStepCost(currentPosition.distanceTo(destination));
        if(stepCost >= batteryLevel) {
            batteryLevel -= stepCost;
            currentPosition = destination;
        }
    }

    /**
     * we are ingnoring these for now.
     * @param angle
     */
    public void turn(int angle) {
        return;
    }
    public int getAngleDifference(LongLat destination) {
        return 0;
    }
    public int getStepCount(){
        return movementCount;
    }
    public void hover(){
        droneState = DroneState.HOVERING;
    }
    public LongLat getCurrentPosiition(){
        return currentPosition;
    }
    public int getBatteryLevel(){
        return batteryLevel;
    }
    public void hoverDrone() {
        droneState = DroneState.HOVERING;
        movementCount += 1;
    }
    public void flyDrobe() {
        movementCount += 1;
        droneState = DroneState.FLYING;
    }
    public void returnToHome() {
        droneState = DroneState.STATIONARY;
    }
    public void loadItems(FoodOrder order) {
        if(droneState == DroneState.HOVERING && !hasOrder) {
            droneWeight += 0;
            hasOrder = true;
            movementCount += 1;
            //TODO add wieght from food order
        }
    }

    public Calendar getTravelTime(double distance) {
        return Calendar.getInstance();
        //TODO modify to reflect actual travel time.
    }
    public void unloadOrder() {
        hoverDrone();
        hasOrder = false;
        droneWeight = 0;
    }
}
