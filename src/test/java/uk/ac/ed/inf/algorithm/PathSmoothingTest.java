package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

public class PathSmoothingTest {

    @Test
    public void testSmoothenPath() {
        PathSmoothing pathSmoothing = new PathSmoothing(new HashMap<LongLat, LongLat>(1), 1);

        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new Node(1, 1));
        assertEquals(1, pathSmoothing.smoothenPath(nodeList).size());
    }

    @Test
    public void testSmoothenPath2() {
        PathSmoothing pathSmoothing = new PathSmoothing(new HashMap<LongLat, LongLat>(1), 1);

        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new Node(1, 1));
        assertEquals(1, pathSmoothing.smoothenPath(nodeList).size());
    }
}

