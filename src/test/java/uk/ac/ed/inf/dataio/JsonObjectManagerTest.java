package uk.ac.ed.inf.dataio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

public class JsonObjectManagerTest {
    @Test
    public void testCoordToLonglat() {
        JsonObjectManager.W3WObject.Coords coords = new JsonObjectManager.W3WObject.Coords();
        coords.lng = "42";
        coords.lat = "42";
        LongLat actualCoordToLonglatResult = JsonObjectManager.coordToLonglat(coords);
        assertEquals(42.0, actualCoordToLonglatResult.longitude, 0.0);
        assertEquals(42.0, actualCoordToLonglatResult.latitude, 0.0);
    }
}

