package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.awt.*;

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

        assertFalse(menu1 == menu2);
        assertTrue(menu1.equals(menu2));
    }
}
