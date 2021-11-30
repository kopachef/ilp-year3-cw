package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FoodOrderTest {
    @Test
    public void testConstructor() {
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        Date deliveryDate = mock(Date.class);
        LongLat longLat = new LongLat(10.0, 10.0);

        FoodOrder actualFoodOrder = new FoodOrder(menuItemList, "Order No", "Customer", deliveryDate, longLat, "42 Main St",
                1);

        assertEquals("Customer", actualFoodOrder.getCustomer());
        assertEquals(1, actualFoodOrder.getDeliveryCost());
        assertSame(longLat, actualFoodOrder.getDeliveryLocationLongLat());
        assertEquals("42 Main St", actualFoodOrder.getDeliveryW3wAddress());
        List<MenuItem> orderItems = actualFoodOrder.getOrderItems();
        assertSame(menuItemList, orderItems);
        assertEquals(actualFoodOrder.getPickUpLocations(), orderItems);
        assertEquals("Order No", actualFoodOrder.getOrderNo());
        assertFalse(actualFoodOrder.isHasBeenDelivered());
    }

    @Test
    public void testCalculateTravelDistance() {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        Date deliveryDate = mock(Date.class);
        FoodOrder foodOrder = new FoodOrder(items, "Order No", "Customer", deliveryDate, new LongLat(10.0, 10.0),
                "42 Main St", 1);
        assertEquals(0.0, foodOrder.calculateTravelDistance(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testGetPickUpLocations() {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        Date deliveryDate = mock(Date.class);
        assertTrue((new FoodOrder(items, "Order No", "Customer", deliveryDate, new LongLat(10.0, 10.0), "42 Main St", 1))
                .getPickUpLocations()
                .isEmpty());
    }
}

