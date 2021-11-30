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
        assertEquals(0.0, (new OrderDeliveryWorker(new Drone(1), mock(Date.class))).getTotalOrderValue(), 0.0);
    }

    @Test
    public void testConstructor2() {
        OrderDeliveryWorker actualOrderDeliveryWorker = new OrderDeliveryWorker(new Drone(1), mock(Date.class));

        assertNull(actualOrderDeliveryWorker.getFoodOrder());
        assertEquals(0.0, actualOrderDeliveryWorker.getTotalOrderValue(), 0.0);
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

