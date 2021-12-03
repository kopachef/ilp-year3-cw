package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GraphUtilsTest {
    @Test
    public void testRoundOffToNearestMultiple() {
        assertEquals(10.0, GraphUtils.roundOffToNearestMultiple(10.0, 10.0), 0.0);
        assertEquals(0.0, GraphUtils.roundOffToNearestMultiple(1.0, 10.0), 0.0);
        assertEquals(0.0, GraphUtils.roundOffToNearestMultiple(0.5, 10.0), 0.0);
        assertEquals(0.0, GraphUtils.roundOffToNearestMultiple(2.0, 10.0), 0.0);
        assertEquals(Double.NaN, GraphUtils.roundOffToNearestMultiple(10.0, Double.NaN), 0.0);
    }

    @Test
    public void testRoundOffToNearest10th() {
        assertEquals(10, GraphUtils.roundOffToNearest10th(10.0));
    }

    @Test
    public void testIsAMulitpleOf10() {
        assertTrue(GraphUtils.isAMultipleOf10(10.0));
        assertFalse(GraphUtils.isAMultipleOf10(1.0));
        assertFalse(GraphUtils.isAMultipleOf10(Double.NaN));
    }

    @Test
    public void testIsWholeNumber() {
        assertTrue(GraphUtils.isWholeNumber(10.0));
        assertFalse(GraphUtils.isWholeNumber(0.5));
    }
}

