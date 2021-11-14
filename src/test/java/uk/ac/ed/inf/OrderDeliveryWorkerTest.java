package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.PriorityQueue;

import org.junit.Test;

public class OrderDeliveryWorkerTest {
    @Test
    public void testConstructor() {
        Drone drone = new Drone(1);
        OrderDeliveryWorker actualOrderDeliveryWorker = new OrderDeliveryWorker(drone, mock(Date.class));

        assertNull(actualOrderDeliveryWorker.getFoodOrder());
        assertEquals(0, actualOrderDeliveryWorker.getFoodOrderQueueSize());
        assertTrue(actualOrderDeliveryWorker.getFoodOrderQueue().isEmpty());
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        LongLat currentPosition = drone.getCurrentPosition();
        assertTrue(currentPosition.isConfined());
        assertEquals(-3.186874, currentPosition.longitude, 0.0);
        assertEquals(55.944494, currentPosition.latitude, 0.0);
        assertEquals("Longitude: -3.186874\nLatitude: 55.944494", currentPosition.toString());
    }

    @Test
    public void testConstructor2() {
        assertNull((new OrderDeliveryWorker(new Drone(1), mock(Date.class))).getFoodOrder());
    }

    @Test
    public void testConstructor3() {
        Drone drone = new Drone(1);
        OrderDeliveryWorker actualOrderDeliveryWorker = new OrderDeliveryWorker(drone, mock(Date.class));

        assertNull(actualOrderDeliveryWorker.getFoodOrder());
        assertEquals(0, actualOrderDeliveryWorker.getFoodOrderQueueSize());
        assertTrue(actualOrderDeliveryWorker.getFoodOrderQueue().isEmpty());
        assertEquals(1, drone.getBatteryLevel());
        assertEquals(0, drone.getStepCount());
        LongLat currentPosition = drone.getCurrentPosition();
        assertTrue(currentPosition.isConfined());
        assertEquals(-3.186874, currentPosition.longitude, 0.0);
        assertEquals(55.944494, currentPosition.latitude, 0.0);
        assertEquals("Longitude: -3.186874\nLatitude: 55.944494", currentPosition.toString());
    }

    @Test
    public void testUpdateFoodOrders() {
        OrderDeliveryWorker orderDeliveryWorker = new OrderDeliveryWorker(new Drone(1), mock(Date.class));
        Drone drone = new Drone(1);
        orderDeliveryWorker.updateFoodOrders(drone, new PriorityQueue<FoodOrder>(1));
        assertNull(orderDeliveryWorker.getFoodOrder());
        assertEquals(0, orderDeliveryWorker.getFoodOrderQueueSize());
    }

    @Test
    public void testGetFoodOrderQueueSize() {
        assertEquals(0, (new OrderDeliveryWorker(new Drone(1), mock(Date.class))).getFoodOrderQueueSize());
    }

    @Test
    public void testGetFoodOrder() {
        OrderDeliveryWorker orderDeliveryWorker = new OrderDeliveryWorker(new Drone(1), mock(Date.class));
        assertNull(orderDeliveryWorker.getFoodOrder());
        assertTrue(orderDeliveryWorker.getFoodOrderQueue().isEmpty());
    }
}

