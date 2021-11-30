package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

public class AStarTest {
    @Test
    public void testConstructor() {
        Node node = new Node(1, 1);

        Node node1 = new Node(1, 1);

        Node node2 = new Node(1, 1);

        Node node3 = new Node(1, 1);

        Node initialNode = new Node(1, 1);

        AStar actualAStar = new AStar(
                new Node[][]{new Node[]{}, new Node[]{node, node1, new Node(1, 1)}, new Node[]{node2, node3, new Node(1, 1)}},
                initialNode, new Node(1, 1));

        assertTrue(actualAStar.getClosedSet().isEmpty());
        assertEquals(3, actualAStar.getSearchArea().length);
        assertTrue(actualAStar.getOpenList().isEmpty());
        assertEquals(5.0, actualAStar.getInitialNode().stepCost, 0.0);
        assertEquals(5.0, actualAStar.getFinalNode().stepCost, 0.0);
    }

    @Test
    public void testSetRestrictedAreas() {

        Node node = new Node(1, 1);

        Node node1 = new Node(1, 1);

        Node node2 = new Node(1, 1);

        Node node3 = new Node(1, 1);

        Node initialNode = new Node(1, 1);

        AStar aStar = new AStar(
                new Node[][]{new Node[]{}, new Node[]{node, node1, new Node(1, 1)}, new Node[]{node2, node3, new Node(1, 1)}},
                initialNode, new Node(1, 1));
        aStar.setRestrictedAreas(new Node[]{new Node(1, 1)});
    }

    @Test
    public void testSetRestrictedAreas2() {
        Node initialNode = new Node(1, 1);

        AStar aStar = new AStar(new Node[][]{}, initialNode, new Node(1, 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> aStar.setRestrictedAreas(new Node[]{new Node(1, 1)}));
    }

    @Test
    public void testSetRestrictedAreas3() {
        Node node = new Node(1, 1);

        Node node1 = new Node(1, 1);

        Node initialNode = new Node(1, 1);

        AStar aStar = new AStar(
                new Node[][]{new Node[]{}, new Node[]{new Node(1, 1)}, new Node[]{node, node1, new Node(1, 1)}}, initialNode,
                new Node(1, 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> aStar.setRestrictedAreas(new Node[]{new Node(1, 1)}));
    }

    @Test
    public void testSetRestrictedAreas4() {
        Node node = new Node(1, 1);

        Node node1 = new Node(1, 1);

        Node node2 = new Node(1, 1);

        Node initialNode = new Node(1, 1);

        AStar aStar = new AStar(
                new Node[][]{new Node[]{}, new Node[]{node, null, new Node(1, 1)}, new Node[]{node1, node2, new Node(1, 1)}},
                initialNode, new Node(1, 1));
        aStar.setRestrictedAreas(new Node[]{});
        assertTrue(aStar.getClosedSet().isEmpty());
        assertEquals(3, aStar.getSearchArea().length);
        assertTrue(aStar.getOpenList().isEmpty());
        assertEquals(5.0, aStar.getInitialNode().stepCost, 0.0);
        assertEquals(5.0, aStar.getFinalNode().stepCost, 0.0);
    }

    @Test
    public void testFindPath() {
        Node node = new Node(1, 1);
        node.setLongLat(new LongLat(10.0, 10.0));
        Node node1 = new Node(1, 1);

        Node node2 = new Node(1, 1);

        Node node3 = new Node(1, 1);

        Node node4 = new Node(1, 1);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> (new AStar(
                new Node[][]{new Node[]{}, new Node[]{node1, node2, new Node(1, 1)}, new Node[]{node3, node4, new Node(1, 1)}},
                new Node(1, 1), node)).findPath());
    }
}

