package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class GraphTest {
    @Test
    public void testGenerateDoubleSequence() {
        assertTrue(Graph.generateDoubleSequence(10.0, 10.0, 10.0, 10.0).isEmpty());
    }

    @Test
    public void testGenerateDoubleSequence2() {
        List<Double> actualGenerateDoubleSequenceResult = Graph.generateDoubleSequence(-0.5, 10.0, 10.0, 10.0);
        assertEquals(1, actualGenerateDoubleSequenceResult.size());
        assertEquals(9.5, actualGenerateDoubleSequenceResult.get(0), 0.0);
    }
}

