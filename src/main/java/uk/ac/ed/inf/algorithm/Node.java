package uk.ac.ed.inf.algorithm;

import uk.ac.ed.inf.LongLat;

import java.util.Objects;

public class Node implements Cloneable {

  /**
   * A node object represents a single node on our groan which also represents a location on our
   * map.
   */
  public double stepCost = 1;

  public int row, col;
  double g, h, f;
  private LongLat longLat;
  private boolean isRestricted;
  private Node parent;
  private NodeUsage usage = NodeUsage.ORDINARY;

  public Node(int row, int col) {
    super();
    this.col = col;
    this.row = row;
  }

  /**
   * The getLongLat() method returns a longitude value. It is a read-only property.
   *
   * @return associated nodes LongLat
   */
  public LongLat getLongLat() {
    return longLat;
  }

  /**
   * The setLongLat() method sets the longitude for this Node. It is a setter method.
   *
   * @param longLat value
   */
  public void setLongLat(LongLat longLat) {
    this.longLat = longLat;
  }

  /**
   * The calculateHeuristic() method calculates the heuristic for the node. It uses the distance
   * between the node's longitude and the destination's longitude to calculate the heuristic.
   *
   * @param destination node
   */
  public void calculateHeuristic(Node destination) {
    this.h = this.longLat.distanceTo(destination.getLongLat());
  }

  /**
   * The setNodeData() method sets the node's G cost value if it is not already set. It also sets
   * the parent node of the node. It calculates the total cost of the node.
   *
   * @param currentNode value
   * @param cost value
   */
  public void setNodeData(Node currentNode, double cost) {
    double gTotal = currentNode.getG() + cost;
    setParent(currentNode);
    setG(gTotal);
    calculateTotalCost();
  }

  /**
   * The calculateTotalCost method will be used to calculate the total cost of a purchase. The
   * calculateTotalCost method adds the cost of the two products that have been selected. Then the
   * totalCost is set to this value.
   */
  public void calculateTotalCost() {
    double totalCost = getG() + getH();
    setF(totalCost);
  }

  public double calculateAngleTo(Node node) {
    double real = this.getLongLat().calculateBearing(node.getLongLat());
    return GraphUtils.roundOffToNearest10th(real);
  }

  /**
   * The lookUpBetterPath is a function that is used to determine if a better path is available. If
   * the cost of the product is less than the current cost of the product, it will replace the
   * current product.
   *
   * @param currentNode value
   * @param cost value
   * @return True if lower cost path is found, False otherwise.
   */
  public boolean lookUpBetterPath(Node currentNode, double cost) {
    double gTotal = currentNode.getG() + cost;
    if (gTotal < getG()) {
      setNodeData(currentNode, cost);
      return true;
    }
    return false;
  }

  /**
   * The equals function checks to see if the locations are the same, the rows are the same, and the
   * columns are the same.
   *
   * @param node value
   * @return output
   */
  @Override
  public boolean equals(Object node) {
    if ((node == null) || (getClass() != node.getClass())) {
      return false;
    } else {
      Node inputNode = (Node) node;
      return this.getRow() == inputNode.getRow()
          && this.getCol() == inputNode.getCol()
          && inputNode.getLongLat().equals(this.getLongLat());
      }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object
   *     <p>Returns a hash of the MenuItem object.
   * @return hash value of MenuItem object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(stepCost, row, col, g, h, f, longLat, isRestricted, parent, usage);
  }

  /**
   * The toString() method returns a string containing the row, column, latitude, and longitude of
   * the square's location.
   *
   * @return output
   */
  @Override
  public String toString() {
    return "Node [row = " + row + ", col = " + col + "]\n"; // + longLat.toString() + "\n";
  }

  /**
   * The getRow() method returns the row of the square
   *
   * @return row
   */
  public int getRow() {
    return this.row;
  }

  /**
   * the getCol() method returns the column of the square
   *
   * @return column
   */
  public int getCol() {
    return this.col;
  }

  /**
   * the getG() method returns the square's g-value.
   *
   * @return value
   */
  public double getG() {
    return this.g;
  }

  /**
   * The setG() method sets the square's g-value.
   *
   * @param g value
   */
  public void setG(double g) {
    this.g = g;
  }

  /**
   * The getH() method returns the square's h-value
   *
   * @return value
   */
  public double getH() {
    return this.h;
  }

  /**
   * the setH() method sets the square's h-value.
   *
   * @param h value
   */
  public void setH(double h) {
    this.h = h;
  }

  /**
   * The getParent() method returns the square's parent
   *
   * @return value
   */
  public Node getParent() {
    return parent;
  }

  /**
   * the setParent() method sets the square's parent.
   *
   * @param parent value
   */
  public void setParent(Node parent) {
    this.parent = parent;
  }

  /**
   * the setF() method sets the square's f-value.
   *
   * @return value
   */
  public double getF() {
    return this.f;
  }

  /**
   * the setF() method sets the square's f-value.
   *
   * @param f value
   */
  public void setF(double f) {
    this.f = f;
  }

  /**
   * The isRestricted() method returns a boolean indicating whether the square is restricted
   *
   * @return value
   */
  public boolean isRestricted() {
    return isRestricted;
  }

  /**
   * the setRestricted() method sets the square's restricted status.
   *
   * @param restricted value
   */
  public void setRestricted(boolean restricted) {
    isRestricted = restricted;
  }

  /**
   * Checks if our Longlat has been set.
   *
   * @return True if set, False otherwise.
   */
  public boolean isLongLatSet() {
    return !(longLat == null);
  }

  /**
   * Not all nodes on the graph are the same. We use this to denote nodes that are pickup or
   * delivery nodes. This is done because drone behaviour is different on these nodes(HOVER
   * required). We could have avoided this by checking if the longlat values of a Node match a
   * pickup or delivery node but that would be computationally expensive and this is an easy
   * efficient solution.
   *
   * @return Node usage value
   */
  public NodeUsage getUsage() {
    return usage;
  }

  /**
   * Sets whether a node is an ordinary, pickup or delivery node.
   *
   * @param usage value
   */
  public void setNodeUsage(NodeUsage usage) {
    this.usage = usage;
  }

  /**
   * This method clones the current node.
   *
   * @return cloned node object.
   *
   * @throws CloneNotSupportedException if not supported.
   */
  public Node clone() throws CloneNotSupportedException {
    return (Node) super.clone();
  }

  /**
   * The NodeUsage enumeration defines the possible ways in which a node may be used in a
   * path network.
   *
   * <p>PICKUP indicates that the node is being used to pick up food orders.
   *
   * <p>DROPOFF indicates that the node is being used to drop off food orders.
   *
   * <p>HOME indicates that the node is being used as a home base.
   *
   * <p>ORDINARY indicates that the node is being used in the ordinary way(usual nodes within a path).
   */
  public enum NodeUsage {
    PICKUP,
    DROPOFF,
    HOME,
    ORDINARY
  }
}
