package uk.ac.ed.inf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class MenuItemTest {
    @Test
    public void testConstructor() {
        MenuItem actualMenuItem = new MenuItem("Name", "Location", 1, "Restaurant Name");

        assertEquals("Location", actualMenuItem.getLocation());
        assertEquals("Name", actualMenuItem.getName());
        assertEquals(1, actualMenuItem.getPrice());
        assertEquals("Restaurant Name", actualMenuItem.getRestaurantName());
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, (new MenuItem("Name", "Location", 1, "Restaurant Name")));
        assertNotEquals(null, (new MenuItem(null, "Location", 1, "Restaurant Name")));
        assertNotEquals(null, (new MenuItem("Name", null, 1, "Restaurant Name")));
        assertNotEquals(null, (new MenuItem("Name", "Location", 1, null)));
    }

    @Test
    public void testEquals2() {
        MenuItem menuItem = new MenuItem("Name", "Location", 1, "Restaurant Name");
        assertEquals(menuItem, menuItem);
        int expectedHashCodeResult = menuItem.hashCode();
        assertEquals(expectedHashCodeResult, menuItem.hashCode());
    }

    @Test
    public void testEquals3() {
        MenuItem menuItem = new MenuItem("Name", "Location", 1, "Restaurant Name");
        MenuItem menuItem1 = new MenuItem("Name", "Location", 1, "Restaurant Name");
        assertEquals(menuItem, menuItem1);
        int notExpectedHashCodeResult = menuItem.hashCode();
        assertNotEquals(notExpectedHashCodeResult, menuItem1.hashCode());
    }

    @Test
    public void testEquals4() {
        MenuItem menuItem = new MenuItem("Location", "Location", 1, "Restaurant Name");
        assertNotEquals(menuItem, new MenuItem("Name", "Location", 1, "Restaurant Name"));
    }

    @Test
    public void testEquals5() {
        MenuItem menuItem = new MenuItem("Name", "Name", 1, "Restaurant Name");
        assertNotEquals(menuItem, new MenuItem("Name", "Location", 1, "Restaurant Name"));
    }

    @Test
    public void testEquals6() {
        MenuItem menuItem = new MenuItem("Name", "Location", 0, "Restaurant Name");
        assertNotEquals(menuItem, new MenuItem("Name", "Location", 1, "Restaurant Name"));
    }

    @Test
    public void testEquals7() {
        MenuItem menuItem = new MenuItem("Name", "Location", 1, "Name");
        assertNotEquals(menuItem, new MenuItem("Name", "Location", 1, "Restaurant Name"));
    }

    @Test
    public void testDump() {
        String testItem = "Flaming tiger latte";
        String testItemLocation = "looks.clouds.daring";
        int testItemPrice = 460;
        String testItemRestaurant = "Nile Valley";
        String expectedOutput =
                "Name: Flaming tiger latte\n"
                        + "Location: looks.clouds.daring\n"
                        + "Price: 460\n"
                        + "Restaurant Name: Nile Valley\n"
                        + "\n"
                        + "Formatted Location: \n"
                        + "\tCountry: GB\n"
                        + "\tNearest Place: Edinburgh\n"
                        + "\tWords: looks.clouds.daring\n"
                        + "\tLanguage: en\n"
                        + "\tMap: https://w3w.co/looks.clouds.daring\n"
                        + "\tSquare: \n"
                        + "\t\tNortheast: longitude: -3.185308 latitude: 55.944669\n"
                        + "\t\tSouthwest: longitude: -3.185356 latitude: 55.944642\n"
                        + "\tCoords: \n"
                        + "\t\tLongitude: -3.185332\n"
                        + "\t\tLatitude: 55.944656\n";

        MenuItem menuItem = new MenuItem(testItem, testItemLocation, testItemPrice, testItemRestaurant);
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        menuItem.dump();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}

