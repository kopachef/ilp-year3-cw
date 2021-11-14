package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AStarTest {

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

