package uk.ac.ed.inf.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.Test;
import uk.ac.ed.inf.LongLat;

public class NodeTest {
    @Test
    public void testConstructor() {
        Node actualNode = new Node(1, 1);
        actualNode.setF(10.0);
        actualNode.setG(10.0);
        actualNode.setH(10.0);
        LongLat longLat = new LongLat(10.0, 10.0);

        actualNode.setLongLat(longLat);
        Node node = new Node(1, 1);

        actualNode.setParent(node);
        actualNode.setRestricted(true);
        assertEquals(1, actualNode.getCol());
        Node parent = actualNode.getParent();
        assertEquals(1, parent.getCol());
        assertEquals(10.0, actualNode.getF(), 0.0);
        assertEquals(0.0, parent.getF(), 0.0);
        assertEquals(10.0, actualNode.getG(), 0.0);
        assertEquals(0.0, parent.getG(), 0.0);
        assertEquals(10.0, actualNode.getH(), 0.0);
        assertEquals(0.0, parent.getH(), 0.0);
        assertSame(longLat, actualNode.getLongLat());
        assertNull(parent.getLongLat());
        assertSame(node, parent);
        assertNull(parent.getParent());
        assertEquals(1, actualNode.getRow());
        assertEquals(1, parent.getRow());
        assertTrue(actualNode.isRestricted());
        assertFalse(parent.isRestricted());
        assertEquals("Node [row = 1, col = 1]\nLongitude: 10.0\nLatitude: 10.0\n", actualNode.toString());
    }

    @Test
    public void testCalculateHeuristic() {
        LongLat longLat = mock(LongLat.class);
        when(longLat.distanceTo((LongLat) any())).thenReturn(10.0);

        Node node = new Node(1, 1);
        node.setLongLat(longLat);
        node.calculateHeuristic(new Node(1, 1));
        verify(longLat).distanceTo((LongLat) any());
        assertEquals("Node [row = 1, col = 1]\nLongitude: 0.0\nLatitude: 0.0\n", node.toString());
        assertEquals(10.0, node.getH(), 0.0);
    }

    @Test
    public void testSetNodeData() {
        Node node = new Node(1, 1);
        Node node1 = new Node(1, 1);

        node.setNodeData(node1, 10.0);
        assertSame(node1, node.getParent());
        assertEquals(10.0, node.getG(), 0.0);
        assertEquals(10.0, node.getF(), 0.0);
    }

    @Test
    public void testCalculateTotalCost() {
        Node node = new Node(1, 1);
        node.calculateTotalCost();
        assertEquals(0.0, node.getF(), 0.0);
    }

    @Test
    public void testLookUpBetterPath() {
        Node node = new Node(1, 1);
        assertFalse(node.lookUpBetterPath(new Node(1, 1), 10.0));
    }

    @Test
    public void testLookUpBetterPath2() {
        Node node = new Node(1, 1);
        Node node1 = new Node(1, 1);

        assertTrue(node.lookUpBetterPath(node1, -0.5));
        assertSame(node1, node.getParent());
        assertEquals(-0.5, node.getG(), 0.0);
        assertEquals(-0.5, node.getF(), 0.0);
    }

    @Test
    public void testEquals() {
        assertThrows(NullPointerException.class, () -> (new Node(1, 1)).equals(null));
        assertThrows(ClassCastException.class, () -> (new Node(1, 1)).equals("Different type to Node"));
        assertThrows(NullPointerException.class, () -> (new Node(1, 1)).equals(new Node(1, 1)));
    }

    @Test
    public void testEquals2() {
        Node node = new Node(1, 1);
        node.setLongLat(null);

        Node node1 = new Node(1, 1);
        node1.setLongLat(new LongLat(10.0, 10.0));
        assertFalse(node.equals(node1));
    }

    @Test
    public void testEquals3() {
        Node node = new Node(0, 1);
        node.setLongLat(null);

        Node node1 = new Node(1, 1);
        node1.setLongLat(new LongLat(10.0, 10.0));
        assertFalse(node.equals(node1));
    }

    @Test
    public void testEquals4() {
        Node node = new Node(1, 0);
        node.setLongLat(null);

        Node node1 = new Node(1, 1);
        node1.setLongLat(new LongLat(10.0, 10.0));
        assertFalse(node.equals(node1));
    }

    @Test
    public void testEquals5() {
        Node node = new Node(1, 1);
        node.setLongLat(new LongLat(10.0, 10.0));

        Node node1 = new Node(1, 1);
        node1.setLongLat(new LongLat(10.0, 10.0));
        assertTrue(node.equals(node1));
        int notExpectedHashCodeResult = node.hashCode();
        assertFalse(Objects.equals(notExpectedHashCodeResult, node1.hashCode()));
    }

    @Test
    public void testEquals6() {
        Node node = new Node(1, 1);
        node.setLongLat(null);

        Node node1 = new Node(1, 1);
        node1.setLongLat(mock(LongLat.class));
        assertFalse(node.equals(node1));
    }
}

