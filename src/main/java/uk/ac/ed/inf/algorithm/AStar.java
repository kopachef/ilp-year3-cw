package uk.ac.ed.inf.algorithm;

import java.util.*;

public class AStar {
  private Node[][] searchArea;
  private PriorityQueue<Node> openList;
  private Set<Node> closedSet;
  private Node initialNode;
  private Node finalNode;

  /**
   * AStar object creates a search space representation of grid and primarily allows us to look up
   * the shortest path between nodes on the graph.
   *
   * @param grid grid on which the search will be performed.
   * @param initialNode start node for our path
   * @param finalNode destination node for our path.
   */
  public AStar(Node[][] grid, Node initialNode, Node finalNode) {
    setInitialNode(initialNode);
    setFinalNode(finalNode);
    this.searchArea = grid;
    this.openList =
            new PriorityQueue<>(
                    Comparator.comparingDouble(Node::getF));
    setNodes();
    this.closedSet = new HashSet<>();
  }

  /** Sets the straight line heuristic from each node to the destination node. */
  private void setNodes() {
    for (Node[] nodes : searchArea) {
      for (int j = 0; j < searchArea[0].length; j++) {
        nodes[j].calculateHeuristic(getFinalNode());
      }
    }
  }

  /**
   * Allows to manually set some nodes as restricted which allows us to exclude them from the
   * search.
   *
   * <p>While we can set nodes as restricted here, we can also do this directly from a graph object.
   * This has only been included for convince and testing.
   *
   * @param restrictedNodes List of restricted nodes.
   */
  public void setRestrictedAreas(Node[] restrictedNodes) {
    for (Node restrictedNode : restrictedNodes) {
      int row = restrictedNode.getRow();
      int col = restrictedNode.getCol();
      setRestricted(row, col);
    }
  }

  /**
   * Traverses our search space starting from the start node and returns a list of connecting nodes
   * that form our shortest path.
   *
   * @return List of connected Nodes that form our shortest path.
   */
  public List<Node> findPath() {
    openList.add(initialNode);
    while (!isEmpty(openList)) {
      Node currentNode = openList.poll();
      closedSet.add(currentNode);
      if (currentNode != null) {
        if (isFinalNode(currentNode)) {
          return getPath(currentNode);
        } else {
          addAdjacentNodes(currentNode);
        }
      }
    }
    return new ArrayList<>();
  }

  /**
   * Helper function to our findPath function that traverses up our nodes and reorganises our
   * shortest path in the right order.
   *
   * @param currentNode node to start traversal from.
   * @return List of ordered Nodes forming the shortest path.
   */
  private List<Node> getPath(Node currentNode) {
    List<Node> path = new ArrayList<>();
    path.add(currentNode);
    Node parent;
    while ((parent = currentNode.getParent()) != null && !path.contains(parent)) {
      path.add(0, parent);
      currentNode = parent;
    }
    return path;
  }

  /**
   * From a given start node, this function allows us to explore adjacent nodes if there is a
   * connecting edge between them
   *
   * @param currentNode node from which we begin to explore.
   */
  private void addAdjacentNodes(Node currentNode) {
    addAdjacentUpperRow(currentNode);
    addAdjacentMiddleRow(currentNode);
    addAdjacentLowerRow(currentNode);
  }

  /**
   * Exploring adjacent nodes in the lower row.
   *
   * @param currentNode returns current node
   */
  private void addAdjacentLowerRow(Node currentNode) {
    int row = currentNode.getRow();
    int col = currentNode.getCol();
    int lowerRow = row + 1;
    if (lowerRow < getSearchArea().length) {
      if (col - 1 >= 0) {
        double cost = getSearchArea()[lowerRow][col - 1].stepCost;
        if (row % 2 == 0) {
          checkNode(
              currentNode,
              col - 1,
              lowerRow,
              cost); // Comment this line if left diagonal movement is not allowed
        }
      }
      if (col + 1 < getSearchArea()[0].length) {
        double cost = getSearchArea()[lowerRow][col + 1].stepCost;
        if (row % 2 == 1) {
          checkNode(
              currentNode,
              col + 1,
              lowerRow,
              cost); // Comment this line if right diagonal movement is not allowed
        }
      }
      double cost = getSearchArea()[lowerRow][col].stepCost;
      checkNode(currentNode, col, lowerRow, cost);
    }
  }

  /**
   * Exploring adjacent nodes in the same row.
   *
   * @param currentNode node being explored
   */
  private void addAdjacentMiddleRow(Node currentNode) {
    int row = currentNode.getRow();
    int col = currentNode.getCol();
    if (col - 1 >= 0) {
      double cost = getSearchArea()[row][col - 1].stepCost;
      checkNode(currentNode, col - 1, row, cost);
    }
    if (col + 1 < getSearchArea()[0].length) {
      double cost = getSearchArea()[row][col + 1].stepCost;
      checkNode(currentNode, col + 1, row, cost);
    }
  }

  /**
   * Exploring adjacent nodes in the top row.
   *
   * @param currentNode root node
   */
  private void addAdjacentUpperRow(Node currentNode) {
    int row = currentNode.getRow();
    int col = currentNode.getCol();
    int upperRow = row - 1;
    if (upperRow >= 0) {
      if (col - 1 >= 0) {
        double cost = getSearchArea()[upperRow][col - 1].stepCost;
        if (row % 2 == 0) {
          checkNode(
              currentNode,
              col - 1,
              upperRow,
              cost); // Comment this if left diagonal movements is not allowed
        }
      }
      if (col + 1 < getSearchArea()[0].length) {
        double cost = getSearchArea()[upperRow][col + 1].stepCost;
        if (row % 2 == 1) {
          checkNode(
              currentNode,
              col + 1,
              upperRow,
              cost); // Comment this if right diagonal movements is not allowed
        }
      }

      double cost = getSearchArea()[upperRow][col].stepCost;
      checkNode(currentNode, col, upperRow, cost);
    }
  }

  /**
   * Checks if traversing through adjacent node reduces/minimises our straight line heuristic. If it
   * does, switch nodes and use better node else we delete adjacent node from our open list.
   *
   * @param currentNode current node
   * @param col col value of adjacent node.
   * @param row row value of adjacent node.
   * @param cost cost of moving to new node.
   */
  private void checkNode(Node currentNode, int col, int row, double cost) {
    Node adjacentNode = getSearchArea()[row][col];
    if (!adjacentNode.isRestricted() && !getClosedSet().contains(adjacentNode)) {
      if (!getOpenList().contains(adjacentNode)) {
        adjacentNode.setNodeData(currentNode, cost);
        getOpenList().add(adjacentNode);
      } else {
        boolean changed = adjacentNode.lookUpBetterPath(currentNode, cost);
        if (changed) {
          // Remove and Add the changed node, so that the PriorityQueue can sort again its
          // contents with the modified "finalCost" value of the modified node
          getOpenList().remove(adjacentNode);
          getOpenList().add(adjacentNode);
        }
      }
    }
  }

  /**
   * Returns true if current node is the destination.
   *
   * @param currentNode current node
   * @return True if destination, False otherwise.
   */
  private boolean isFinalNode(Node currentNode) {
    return currentNode.equals(finalNode);
  }

  /**
   * Checks if our openList(priority queue) is empty
   *
   * @param openList list of nodes yet to be explored.
   * @return True is empty, False otherwise.
   */
  private boolean isEmpty(PriorityQueue<Node> openList) {
    return openList.size() == 0;
  }

  /**
   * Sets a given nodes as restricted.
   *
   * @param row current row
   * @param col current column
   */
  private void setRestricted(int row, int col) {
    this.searchArea[row][col].setRestricted(true);
  }

  /**
   * Returns the initial node.
   *
   * @return returns initial Node
   */
  public Node getInitialNode() {
    return initialNode;
  }

  /**
   * Sets the initial node.
   *
   * @param initialNode sets initial Node
   */
  public void setInitialNode(Node initialNode) {
    this.initialNode = initialNode;
  }

  /**
   * Returns the final node.
   *
   * @return returns final Node
   */
  public Node getFinalNode() {
    return finalNode;
  }

  /**
   * Sets the final node.
   *
   * @param finalNode sets the final Node.
   */
  public void setFinalNode(Node finalNode) {
    this.finalNode = finalNode;
  }

  /**
   * Returns the search area.
   *
   * @return returns search area.
   */
  public Node[][] getSearchArea() {
    return searchArea;
  }

  /**
   * Sets the search area.
   *
   * @param searchArea sets the search area.
   */
  public void setSearchArea(Node[][] searchArea) {
    this.searchArea = searchArea;
  }

  /**
   * Returns the open list of nodes to be traversed.
   *
   * @return open list
   */
  public PriorityQueue<Node> getOpenList() {
    return openList;
  }

  /**
   * Sets the open list of nodes to be traversed.
   *
   * @param openList open list to be set.
   */
  public void setOpenList(PriorityQueue<Node> openList) {
    this.openList = openList;
  }

  /**
   * Gets the closed set of visited nodes.
   *
   * @return closed set.
   */
  public Set<Node> getClosedSet() {
    return closedSet;
  }

  /**
   * Sets the closed set of visited nodes.
   *
   * @param closedSet return visited nodes.
   */
  public void setClosedSet(Set<Node> closedSet) {
    this.closedSet = closedSet;
  }
}
