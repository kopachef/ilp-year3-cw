package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.ArrayList;

import org.junit.Test;

public class DroneTest {
    @Test
    public void testConstructor() {
        Drone actualDrone = new Drone(1);
        actualDrone.setBatteryLevel(1);
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        Date deliveryDate = mock(Date.class);
        LongLat longLat = new LongLat(10.0, 10.0);

        FoodOrder foodOrder = new FoodOrder(items, "Order No", "Customer", deliveryDate, longLat, "42 Main St", 1);

        actualDrone.setCurrentFoodOrder(foodOrder);
        LongLat longLat1 = new LongLat(10.0, 10.0);

        actualDrone.setCurrentPosition(longLat1);
        assertEquals(1, actualDrone.getBatteryLevel());
        assertSame(foodOrder, actualDrone.getCurrentFoodOrder());
        LongLat currentPosition = actualDrone.getCurrentPosition();
        assertSame(longLat1, currentPosition);
        assertEquals(longLat, currentPosition);
        assertEquals(Drone.DroneState.STATIONARY, actualDrone.getDroneState());
        assertEquals(0, actualDrone.getStepCount());
    }

    @Test
    public void testConstructor2() {
        Drone actualDrone = new Drone(1);
        assertEquals(1, actualDrone.getBatteryLevel());
        assertEquals(0, actualDrone.getStepCount());
        assertEquals(Drone.DroneState.STATIONARY, actualDrone.getDroneState());
        assertEquals(-3.186874, actualDrone.getCurrentPosition().longitude, 0.0);
    }

    @Test
    public void testCalculateMovementStepCost() {
        assertEquals(66666, (new Drone(1)).calculateMovementStepCost(10.0));
    }

    @Test
    public void testMoveTo() {
        Drone drone = new Drone(1);
        LongLat longLat = new LongLat(10.0, 10.0);

        drone.moveTo(longLat);
        assertEquals(-318662, drone.getBatteryLevel());
        assertEquals(318663, drone.getStepCount());
        assertSame(longLat, drone.getCurrentPosition());
    }

    @Test
    public void testMoveTo2() {
        Drone drone = new Drone(1);
        LongLat longLat = new LongLat(Double.NaN, 10.0);

        drone.moveTo(longLat);
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        assertSame(longLat, drone.getCurrentPosition());
    }

    @Test
    public void testHoverDrone() {
        Drone drone = new Drone(1);
        drone.hoverDrone();
        assertEquals(0, drone.getBatteryLevel());
        assertEquals(Drone.DroneState.HOVERING, drone.getDroneState());
    }

    @Test
    public void testLoadItems() {
        Drone drone = new Drone(1);
        drone.loadItems();
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        assertEquals(Drone.DroneState.STATIONARY, drone.getDroneState());
        assertEquals(-3.186874, drone.getCurrentPosition().longitude, 0.0);
    }

    @Test
    public void testResetBatterLevel() {
        Drone drone = new Drone(1);
        drone.resetBatterLevel();
        assertEquals(1500, drone.getBatteryLevel());
    }
}

