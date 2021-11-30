package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.dataio.JsonObjectManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class MenusTest {

    @Test
    public void testMenusDetails() {
        Menus menus = new Menus("localhost", "9898");
        String testItem = "Flaming tiger latte";
        String testItemLocation = "looks.clouds.daring";
        int testItemPrice = 460;

        assertEquals(menus.getItemLocation(testItem), testItemLocation);
        assertEquals(menus.getItemPrice(testItem), testItemPrice);
    }

    @Test
    public void testMenusAllItemsLoaded() {
        Menus menus = new Menus("localhost", "9898");
        int menuSize = 181;

        assertEquals(menuSize, menus.getAvailableItems().size());
    }

    @Test
    public void testMenusParseW3WObject() {
        String testItemLocation = "looks.clouds.daring";
        String nearestPlace = "Edinburgh";
        String country = "GB";
        JsonObjectManager.W3WObject w3wObject = JsonObjectManager.parseW3WObject(testItemLocation);

        assertEquals(testItemLocation, w3wObject.words);
        assertEquals(nearestPlace, w3wObject.nearestPlace);
        assertEquals(country, w3wObject.country);
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