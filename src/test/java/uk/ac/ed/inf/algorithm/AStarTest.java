package uk.ac.ed.inf.algorithm;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
        // TODO: This test is incomplete.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by setRestrictedAreas(Node[])
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

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

//    @Test
//    public  void testFindPaths() {
//        Node initialNode = new Node(2, 1);
//        Node finalNode = new Node(2, 5);
//        int rows = 6;
//        int cols = 7;
//        AStar aStar = new AStar(rows, cols, initialNode, finalNode);
//        int[][] blocksArray = new int[][]{{1, 3}, {2, 3}, {3, 3},{1,4},{1,5}};
//        aStar.setRestrictedAreas(blocksArray);
//        List<Node> path = aStar.findPath();
//        for (Node node : path) {
//            System.out.println(node);
//        }

    //Search Area
    //      0   1   2   3   4   5   6
    // 0    -   -   -   -   -   -   -
    // 1    -   -   -   B   B   B   -
    // 2    -   I   -   B   -   F   -
    // 3    -   -   -   B   -   -   -
    // 4    -   -   -   -   -   -   -
    // 5    -   -   -   -   -   -   -

    //Expected output with diagonals
    //Node [row=2, col=1]
    //Node [row=3, col=2]
    //Node [row=4, col=3]
    //Node [row=3, col=4]
    //Node [row=2, col=5]

    //Shortest path
    //      0   1   2   3   4   5   6
    // 0    -   -   -   -   -   -   -
    // 1    -   -   -   B   B   B   -
    // 2    -   I*  -   B   -  *F   -
    // 3    -   -   *   B   *   -   -
    // 4    -   -   -   *   -   -   -
    // 5    -   -   -   -   -   -   -

}
//start + (((end - start)/granularity)*j) + (((end - start)/granularity)*i

