package uk.ac.ed.inf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import org.apache.commons.math3.util.Precision;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LongLatTest {

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);

    @Test
    public void testConstructor() {
        assertEquals("Longitude: 10.0\nLatitude: 10.0", (new LongLat(10.0, 10.0)).toString());
        assertEquals("Longitude: 10.0\nLatitude: 10.0", (new LongLat(10.0, 10.0)).toString());
    }

    @Test
    public void testConstructor2() {
        LongLat actualLongLat = new LongLat(10.0, 10.0);

        assertEquals(10.0, actualLongLat.longitude, 0.0);
        assertEquals(10.0, actualLongLat.latitude, 0.0);
    }

    @Test
    public void testConstructor3() {
        LongLat actualLongLat = new LongLat(10.0, 10.0);

        assertEquals(10.0, actualLongLat.longitude, 0.0);
        assertEquals(10.0, actualLongLat.latitude, 0.0);
    }

    @Test
    public void testIsConfined() {
        assertFalse((new LongLat(10.0, 10.0)).isConfined());
        assertFalse((new LongLat(-3.192473, 10.0)).isConfined());
        assertFalse((new LongLat(10.0, 10.0)).isConfined());
        assertFalse((new LongLat(-3.192473, 10.0)).isConfined());
    }

    @Test
    public void testDistanceTo() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertEquals(0.0, longLat.distanceTo(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testDistanceTo2() {
        LongLat longLat = new LongLat(2.0, -1.0);
        assertEquals(Precision.round(13.601470508735444, 6), longLat.distanceTo(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testDistanceTo3() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertEquals(0.0, longLat.distanceTo(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testDistanceTo4() {
        LongLat longLat = new LongLat(2.0, 10.0);
        assertEquals(8.0, longLat.distanceTo(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testDistanceTo5() {
        LongLat longLat = new LongLat(Double.NaN, 10.0);
        assertEquals(Double.NaN, longLat.distanceTo(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCloseTo() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertTrue(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo2() {
        LongLat longLat = new LongLat(9.99985, 10.0);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo3() {
        LongLat longLat = new LongLat(10.00015, 10.0);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo4() {
        LongLat longLat = new LongLat(10.0, 9.99985);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo5() {
        LongLat longLat = new LongLat(9.99985, 9.9997);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo6() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertTrue(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo7() {
        LongLat longLat = new LongLat(9.99985, 10.0);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo8() {
        LongLat longLat = new LongLat(10.00015, 10.0);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testCloseTo9() {
        LongLat longLat = new LongLat(10.0, 9.99985);
        assertFalse(longLat.closeTo(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testNextPosition() {
        LongLat actualNextPositionResult = (new LongLat(10.0, 10.0)).nextPosition(1);
        assertEquals(10.0, actualNextPositionResult.longitude, 0.0);
        assertEquals(10.0, actualNextPositionResult.latitude, 0.0);
    }

    @Test
    public void testNextPosition2() {
        LongLat actualNextPositionResult = (new LongLat(10.0, 10.0)).nextPosition(0);
        assertEquals(10.00015, actualNextPositionResult.longitude, 0.0);
        assertEquals(10.0, actualNextPositionResult.latitude, 0.0);
    }

    @Test
    public void testNextPosition3() {
        LongLat actualNextPositionResult = (new LongLat(10.0, 10.0)).nextPosition(1);
        assertEquals(10.0, actualNextPositionResult.longitude, 0.0);
        assertEquals(10.0, actualNextPositionResult.latitude, 0.0);
    }

    @Test
    public void testNextPosition4() {
        LongLat actualNextPositionResult = (new LongLat(10.0, 10.0)).nextPosition(0);
        assertEquals(10.00015, actualNextPositionResult.longitude, 0.0);
        assertEquals(10.0, actualNextPositionResult.latitude, 0.0);
    }

    @Test
    public void testNextPositionUnrestricted() {
        LongLat actualNextPositionUnrestrictedResult = (new LongLat(10.0, 10.0)).nextPositionUnrestricted(1);
        assertEquals(10.000149977154274, actualNextPositionUnrestrictedResult.longitude, 0.0);
        assertEquals(10.000002617860966, actualNextPositionUnrestrictedResult.latitude, 0.0);
    }

    @Test
    public void testCalculateBearing() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertEquals(0.0, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCalculateBearing2() {
        LongLat longLat = new LongLat(2.0, 10.0);
        assertEquals(0.0, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCalculateBearing3() {
        LongLat longLat = new LongLat(180.0, 10.0);
        assertEquals(180.0, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCalculateBearing4() {
        LongLat longLat = new LongLat(Double.NaN, 10.0);
        assertEquals(Double.NaN, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCalculateBearing5() {
        LongLat longLat = new LongLat(10.0, 2.0);
        assertEquals(90.0, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testCalculateBearing6() {
        LongLat longLat = new LongLat(2.0, 2.0);
        assertEquals(45.00000252699836, longLat.calculateBearing(new LongLat(10.0, 10.0)), 0.0);
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, (new LongLat(10.0, 10.0)));
        assertFalse((new LongLat(10.0, 10.0)).equals(null));
        assertFalse((new LongLat(10.0, 10.0)).equals("Different type to LongLat"));
    }

    @Test
    public void testEquals6() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertTrue(longLat.equals(longLat));
        int expectedHashCodeResult = longLat.hashCode();
        assertEquals(expectedHashCodeResult, longLat.hashCode());
    }

    @Test
    public void testEquals7() {
        LongLat longLat = new LongLat(10.0, 10.0);
        LongLat longLat1 = new LongLat(10.0, 10.0);

        assertTrue(longLat.equals(longLat1));
        int notExpectedHashCodeResult = longLat.hashCode();
        assertFalse(Objects.equals(notExpectedHashCodeResult, longLat1.hashCode()));
    }

    @Test
    public void testEquals8() {
        LongLat longLat = new LongLat(-1.0, 10.0);
        assertFalse(longLat.equals(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testEquals9() {
        LongLat longLat = new LongLat(10.0, -1.0);
        assertFalse(longLat.equals(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testEquals2() {
        LongLat longLat = new LongLat(10.0, 10.0);
        assertEquals(longLat, longLat);
        int expectedHashCodeResult = longLat.hashCode();
        assertEquals(expectedHashCodeResult, longLat.hashCode());
    }

    @Test
    public void testEquals3() {
        LongLat longLat = new LongLat(10.0, 10.0);
        LongLat longLat1 = new LongLat(10.0, 10.0);

        assertEquals(longLat, longLat1);
        int notExpectedHashCodeResult = longLat.hashCode();
        assertNotEquals(notExpectedHashCodeResult, longLat1.hashCode());
    }

    @Test
    public void testEquals4() {
        LongLat longLat = new LongLat(-1.0, 10.0);
        assertNotEquals(longLat, new LongLat(10.0, 10.0));
    }

    @Test
    public void testEquals5() {
        LongLat longLat = new LongLat(10.0, -1.0);
        assertNotEquals(longLat, new LongLat(10.0, 10.0));
    }

    @Test
    public void testAngleInvalidMove() {
        int invalidAngle = 888;
        LongLat nextPosition = appletonTower.nextPosition(invalidAngle);
        assertEquals(appletonTower, nextPosition);
    }

    @Test
    public void testLongLatDump() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String expectedOutput = "Longitude: " + appletonTower.longitude + "\nLatitude: " + appletonTower.latitude + "\n";
        appletonTower.dump();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}

