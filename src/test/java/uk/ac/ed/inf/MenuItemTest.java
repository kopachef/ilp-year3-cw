package uk.ac.ed.inf;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class MenuItemTest {
    @Test
    public void testConstructor() {
        MenuItem actualMenuItem = new MenuItem("Name", "Location", 1, "Nile Valley");

        assertEquals("Location", actualMenuItem.getLocation());
        assertEquals("Name", actualMenuItem.getName());
        assertEquals(1, actualMenuItem.getPrice());
    }

    @Test
    public void testEquality() {
        MenuItem menu1 = new MenuItem("Name", "Location", 1, "Nile Valley");
        MenuItem menu2 = new MenuItem("Name", "Location", 1, "Nile Valley");

        assertNotSame(menu1,menu2);
        assertEquals(menu1,menu2);
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

        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        menuItem.dump();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}
