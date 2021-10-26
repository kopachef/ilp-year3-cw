package uk.ac.ed.inf;

public class Drone {

    /**
     * mileage
     * localetime
     * makedelivery
     */
    private int batteryCapacity;
    private LongLat currentLocaiton;

    public void moveTo(LongLat destination){
        return;
    }
    public void turn(int angle) {
        return;
    }
    public int getStepCount(){
        return 0;
    }
    public void hover(){
        return;
    }
    public LongLat getCurrentPosiition(){
        return new LongLat(0,0);
    }
    public int getBatteryPercentage(){
        return 0;
    }
}
