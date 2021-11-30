package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

public class GraphTest {
    @Test
    public void testConstructor() {
        Graph actualGraph = new Graph(10.0, 10.0, 10.0, 10.0, 0);

        List<Node> expectedRestrictedNodes = actualGraph.getAllNodes();
        assertEquals(expectedRestrictedNodes, actualGraph.getRestrictedNodes());
        assertTrue(actualGraph.getNodeLonglatToTargetLonglat().isEmpty());
        assertEquals(0, actualGraph.getGrid().length);
    }

    @Test
    public void testConstructor2() {
        assertThrows(NegativeArraySizeException.class, () -> new Graph(10.0, 10.0, 10.0, 10.0, -1));

    }

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

    @Test
    public void testGetRestrictedNodes() {
        assertTrue((new Graph(10.0, 10.0, 10.0, 10.0, 0)).getRestrictedNodes().isEmpty());
    }

    @Test
    public void testGetAllNodes() {
        assertTrue((new Graph(10.0, 10.0, 10.0, 10.0, 0)).getAllNodes().isEmpty());
    }

    @Test
    public void testFindNearestNode() {
        Graph graph = new Graph(10.0, 10.0, 10.0, 10.0, 0);
        assertNull(graph.findNearestNode(new LongLat(10.0, 10.0)));
    }

    @Test
    public void testDistanceBetweenNodes() {
        Graph graph = new Graph(10.0, 10.0, 10.0, 10.0, 0);
        assertEquals(0.0, graph.distanceBetweenNodes(new ArrayList<Node>()), 0.0);
    }

    @Test
    public void testDistanceBetweenNodes2() {
        Graph graph = new Graph(10.0, 10.0, 10.0, 10.0, 0);

        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new Node(1, 1));
        assertEquals(0.0, graph.distanceBetweenNodes(nodeList), 0.0);
    }
}

