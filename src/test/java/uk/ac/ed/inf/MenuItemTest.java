package uk.ac.ed.inf;

import org.junit.Test;

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
}
