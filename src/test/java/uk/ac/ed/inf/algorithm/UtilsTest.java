package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {
    @Test
    public void testRoundOffToNearestMultiple() {
        assertEquals(10.0, Utils.roundOffToNearestMultiple(10.0, 10.0), 0.0);
        assertEquals(0.0, Utils.roundOffToNearestMultiple(1.0, 10.0), 0.0);
        assertEquals(0.0, Utils.roundOffToNearestMultiple(0.5, 10.0), 0.0);
        assertEquals(0.0, Utils.roundOffToNearestMultiple(2.0, 10.0), 0.0);
        assertEquals(Double.NaN, Utils.roundOffToNearestMultiple(10.0, Double.NaN), 0.0);
    }

    @Test
    public void testRoundOffToNearest10th() {
        assertEquals(10, Utils.roundOffToNearest10th(10.0));
    }

    @Test
    public void testIsAMulitpleOf10() {
        assertTrue(Utils.isAMulitpleOf10(10.0));
        assertFalse(Utils.isAMulitpleOf10(1.0));
        assertFalse(Utils.isAMulitpleOf10(Double.NaN));
    }

    @Test
    public void testIsWholeNumber() {
        assertTrue(Utils.isWholeNumber(10.0));
        assertFalse(Utils.isWholeNumber(0.5));
    }
}

