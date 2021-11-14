package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DroneTest {
    @Test
    public void testConstructor() {
        Drone actualDrone = new Drone(1);
        actualDrone.setBatteryLevel(1);
        LongLat longLat = new LongLat(10.0, 10.0);

        actualDrone.setCurrentPosition(longLat);
        actualDrone.turn(1);
        assertEquals(1, actualDrone.getBatteryLevel());
        assertSame(longLat, actualDrone.getCurrentPosition());
        assertEquals(0, actualDrone.getStepCount());
    }

    @Test
    public void testConstructor2() {
        Drone actualDrone = new Drone(1);
        assertEquals(1, actualDrone.getBatteryLevel());
        assertEquals(0, actualDrone.getStepCount());
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
        assertSame(longLat, drone.getCurrentPosition());
    }

    @Test
    public void testMoveTo2() {
        Drone drone = new Drone(1);
        drone.moveTo(new LongLat(Double.NaN, 10.0));
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        assertEquals(-3.186874, drone.getCurrentPosition().longitude, 0.0);
    }

    @Test
    public void testMoveTo3() {
        Drone drone = new Drone(1);
        drone.setCurrentPosition(new LongLat(10.0, 10.0));
        LongLat longLat = new LongLat(10.0, 10.0);

        drone.moveTo(longLat);
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        assertEquals(longLat, drone.getCurrentPosition());
    }

    @Test
    public void testHoverDrone() {
        Drone drone = new Drone(1);
        drone.hoverDrone();
        assertEquals(0, drone.getBatteryLevel());
    }

    @Test
    public void testLoadItems() {
        Drone drone = new Drone(1);
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        Date deliveryDate = mock(Date.class);
        FoodOrder foodOrder = new FoodOrder(items, "Order No", "Customer", deliveryDate, new LongLat(10.0, 10.0),
                "42 Main St", 10.0);

        drone.loadItems(foodOrder);
        assertEquals("Customer", foodOrder.getCustomer());
        assertFalse(foodOrder.isHasBeenDelivered());
        assertEquals("Order No", foodOrder.getOrderNo());
        List<LongLat> expectedOrderItems = foodOrder.getPickUpLocations();
        assertEquals(expectedOrderItems, foodOrder.getOrderItems());
        assertEquals("42 Main St", foodOrder.getDeliveryW3wAddress());
        assertEquals(10.0, foodOrder.getDeliveryCost(), 0.0);
        assertEquals(10.0, foodOrder.getDeliveryLocationLongLat().latitude, 0.0);
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        assertEquals(-3.186874, drone.getCurrentPosition().longitude, 0.0);
    }
}

