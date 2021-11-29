package uk.ac.ed.inf.algorithm;

import com.mapbox.geojson.Feature;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.GeoJsonManager;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.DoubleStream;
import static java.util.stream.Collectors.toList;

public class Graph {

  private Node[][] grid;
  private int gridSize;
  private HashMap<LongLat, LongLat> nodeLonglatToTargetLonglat = new HashMap<>();
  private double nearestNodeDistance;
  private double northWestBoundLongitude;
  private double northWestBoundLatitude;
  private double southEastBoundLongitude;
  private double southEastBoundLatitude;
  public Graph(
          double northWestBoundLongitude,
          double northWestBoundLatitude,
          double southEastBoundLongitude,
          double southEastBoundLatitude,
          int gridSize) {
    this.gridSize = gridSize;
    this.grid = new Node[gridSize][gridSize];
    this.northWestBoundLongitude = northWestBoundLongitude;
    this.northWestBoundLatitude = northWestBoundLatitude;
    this.southEastBoundLongitude = southEastBoundLongitude;
    this.southEastBoundLatitude = southEastBoundLatitude;
    setGrid();
  }

  public static List<Double> generateDoubleSequence(
          double start, double end, double step, double offset) {
    return DoubleStream.iterate(start + offset, d -> d <= end, d -> d + step)
            .boxed()
            .map(x -> Precision.round(x, 6))
            .collect(toList());
  }

  public int getGridSize() {
    return gridSize;
  }

  /**
   * The setGrid() method creates a grid of equidistant points with the distance between each node
   * being equal to the length of single side of the area divided by the grid granularity.
   * Tt restricts each of these points to the boundaries of one of the following: the nearest node distance
   * (set by this.nearestNodeDistance), or the perimeter of a restricted area (set by
   * GeoJsonManager.isInPerimeterOfRestrictedArea).
   */
  private void setGrid() {
    double longitudeStepSize = Settings.getDefaultMovementStepDistance();//(southEastBoundLongitude - northWestBoundLongitude) / gridSize;
    double longitudeOffset = 0;//(longitudeStepSize / 2);

    double latitudeStepSize = Settings.getDefaultMovementStepDistance();//(northWestBoundLatitude - southEastBoundLatitude) / gridSize;
    double latitudeOffset = 0;//(latitudeStepSize / 2);

    List<Double> longitudeCoordinates =
            generateDoubleSequence(northWestBoundLongitude,southEastBoundLongitude, longitudeStepSize, longitudeOffset);
    List<Double> latitudeCoordinates =
            generateDoubleSequence(southEastBoundLatitude,northWestBoundLatitude, latitudeStepSize, latitudeOffset);


    List<LongLat> topRowCoords = new ArrayList<>();
    List<LongLat> twoRowCoords = new ArrayList<>();
    List<LongLat> botRowCoords = new ArrayList<>();

    double res = Math.abs((northWestBoundLongitude - southEastBoundLongitude) /
            Settings.getDefaultMovementStepDistance());
    int coordListLength = (int) Math.round(res);

    LongLat nxt = Settings.getDefaultNorthWestBound();
    for(int i = 0; i < coordListLength; i++) {
//      System.out.println("Distance to next: " + nxt.distanceTo(nxt.nextPosition(0))+"     new: "+nxt.distanceTo(nxt.nextPosition(0)));
//      System.out.println("Distance to bott: " + nxt.distanceTo(nxt.nextPosition(240))+ "      new: "+nxt.distanceTo(nxt.nextPosition(240)));
//      System.out.println("Bearings: " + nxt.calculateBearing(nxt.nextPosition(10)) + "    new: " +nxt.AngleBetweenThreePoints(nxt.nextPosition(10), nxt.nextPosition(300)));
//      System.out.println("Bearings: " + nxt.calculateBearing(nxt.nextPosition(240))+ "    new: " +nxt.AngleBetweenThreePoints(nxt.nextPosition(40), nxt.nextPosition(180)));
//      System.out.println("Bearings: " + nxt.calculateBearing(nxt.nextPosition(100)) + "    new: " +nxt.calculateBearing(nxt.nextPosition(100)));
      nxt = nxt.nextPosition(0);
      topRowCoords.add(nxt);
      //twoRowCoords.add(nxt.nextPosition(300));
      //botRowCoords.add(nxt.nextPosition(300).nextPosition(240));

      //System.out.println(nxt);
    }

//    for(int i = 1; i < botRowCoords.size(); i++) {
//      System.out.println("Bott row idx: " + botRowCoords.get(i-1).distanceTo(botRowCoords.get(i)));
//

//    for(int i = 1; i<topRowCoords.size(); i++) {
//      System.out.println(topRowCoords.get(i-1).distanceTo(topRowCoords.get(i)));
//    }
    //System.out.println(topRowCoords.size());
    //System.out.println(Math.abs(Math.round((northWestBoundLongitude - southEastBoundLongitude)/Settings.getDefaultMovementStepDistance())));
    this.nearestNodeDistance = longitudeStepSize;

    // For what could possibly be some due to a mapping error, directing adding the full offset to our points shifts
    // our degrees to the neighbouring node. Using only 0.9 of the offset seems to fix this.

    //double gridOffset = 0.9*Settings.getDefaultMovementStepDistance() - (Settings.getDefaultMovementStepDistance() * Math.sin(Math.toRadians(60)));

    int rightRotationAngle = 240;
    int leftRotationAngle = rightRotationAngle  + 60;
    LongLat nodeLongLat;

    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {

        if(i == 0) {
          nodeLongLat = topRowCoords.get(j);
          grid[i][j] = new Node(i, j);
          grid[i][j].setLongLat(nodeLongLat);
          grid[i][j].setRestricted(
                  GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat, nearestNodeDistance) ||
                          !nodeLongLat.isConfined());
        } else {
          if(i % 2 == 0) {
            nodeLongLat = grid[i - 1][j].getLongLat().nextPosition(rightRotationAngle);
          } else {
            nodeLongLat = grid[i - 1][j].getLongLat().nextPosition(leftRotationAngle);
          }

          /*
        As we wish to create a triangular mesh, we add the gridOffset to every odd row. Excluding this will result in
        square mesh which is not what we want to create. Further simply shifting odd rows does not result in an
        equilateral triangle mesh. We have to add a slight incremental Offset to latitude values to shift them up
         slightly thus forming an equilateral triangle mesh.
        */
          nodeLongLat =
                  new LongLat(nodeLongLat.longitude, nodeLongLat.latitude);
          grid[i][j] = new Node(i, j);
          grid[i][j].setLongLat(nodeLongLat);
          grid[i][j].setRestricted(
                  GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat, nearestNodeDistance) ||
                          !nodeLongLat.isConfined());
        }
      }
    }

//    for(int i = 1; i < gridSize-1; i++) {
//      if (i % 2 != 0) {
//        System.out.println(
//            "distance between: "
//                + grid[i][i].getLongLat().distanceTo(grid[i + 1][i].getLongLat())
//                + "      distance below        : "
//                + grid[i][i].getLongLat().distanceTo(grid[i][i - 1].getLongLat())
//                + "    bearings right: "
//                + grid[i+1][i+1].getLongLat().calculateBearing(grid[i][i].getLongLat())
//                + "  left: "
//                + grid[i-1][i-1].getLongLat().calculateBearing(grid[i][i].getLongLat()));
//        }
//    }
  }

  /**
   * Returns the grid mesh.
   *
   * @return
   */
  public Node[][] getGrid() {
    return grid;
  }

  /**
   * Returns all the nodes that have been marked as restricted.
   *
   * @return List of restricted nodes.
   */
  public List<Node> getRestrictedNodes() {
    List<Node> result = new ArrayList<>();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        if (grid[i][j].isRestricted()) {
          result.add(grid[i][j]);
        }
      }
    }
    return result;
  }

  /**
   * Returns all the nodes on our grid.
   *
   * @return List of nodes.
   */
  public List<Node> getAllNodes() {
    List<Node> result = new ArrayList<>();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        result.add(grid[i][j]);
      }
    }
    return result;
  }


  /**
   * The getShortestPath() method finds the shortest path between two points on a two-dimensional grid. The grid
   * in this case will be our graph. The startLocation and destinationLocation will be the nodes being traversed.
   * As a preprocessing step, the getShortestPath() method will iterate through all nodes in the grid and find the
   * nodes that are the closest to the startLocation and destinationLocation. It will then choose the node that is
   * closest to the startLocation and destinationLocation on our graph and call the AStar algorithm to find the
   * shortest path between these.
   *
   * @param startLocation
   * @param destinationLocation
   * @return A list of Nodes forming the shortest path.
   */
  public List<Node> getShortestPath(LongLat startLocation, LongLat destinationLocation) {
    Node startNode = null;//findNearestNode(startLocation);
    Node endNode = null;//findNearestNode(destinationLocation);
    setGrid();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        startNode = startNode == null ? grid[i][j] : startNode;
        endNode = endNode == null ? grid[i][j] : endNode;

        /*
        If current grid node is close to startLocation and distance to startLocation is shorter that the
        current one(multiple nodes can be close to a target node, we want to use the one that's the closest) then
        we reassign to current node.
         */
        if ((grid[i][j].getLongLat().closeTo(startLocation))
                && (grid[i][j].getLongLat().distanceTo(startLocation)
                <= startNode.getLongLat().distanceTo(startLocation))) {
          startNode = grid[i][j];

        } else if ((grid[i][j].getLongLat().closeTo(destinationLocation))
                && (grid[i][j].getLongLat().distanceTo(destinationLocation)
                <= endNode.getLongLat().distanceTo(destinationLocation))) {
          endNode = grid[i][j];

        }
      }
    }
    nodeLonglatToTargetLonglat.put(startNode.getLongLat(), startLocation);
    nodeLonglatToTargetLonglat.put(endNode.getLongLat(), destinationLocation);
    AStar aStar = new AStar(getGrid(), startNode, endNode);
    return aStar.findPath();
  }

  public Node findNearestNode(LongLat longlat) {

    Node node = null;

    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        node = node == null ? grid[i][j] : node;
        /*
        If current grid node is close to startLocation and distance to startLocation is shorter that the
        current one(multiple nodes can be close to a target node, we want to use the one that's the closest) then
        we reassign to current node.
         */
        if ((grid[i][j].getLongLat().closeTo(longlat))
            && (grid[i][j].getLongLat().distanceTo(longlat)
                <= node.getLongLat().distanceTo(longlat))) {
          node = grid[i][j];
        }
      }
    }
    return node;
  }

  /**
   * Calculates the total distance through a given list of nodes.
   *
   * @param nodes List of nodes to calculate the distance between.
   * @return double value as the total distance.
   */
  public double distanceBetweenNodes(List<Node> nodes) {
    if (nodes.isEmpty() || nodes.size() < 2) {
      return 0;
    }
    double total = 0;
    Node currentNode = nodes.get(0);
    for (int i = 1; i < nodes.size(); i++) {
      total += currentNode.getLongLat().distanceTo(nodes.get(i).getLongLat());
      currentNode = nodes.get(i);
    }
    return total;
  }

  public void resetNodeUsages() {
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        grid[i][j].setNodeUsage(Node.NodeUsage.ORDINARY);
      }
    }
  }

  public HashMap<LongLat, LongLat> getNodeLonglatToTargetLonglat() {
    return nodeLonglatToTargetLonglat;
  }

  /**
   * Testing utility funtion to calculate the distance and bearing between nodes.
   * @param nodes
   * TODO remove this function
   */
  public void printDistanceBetweenNodes(List<Node> nodes) {
    List<Feature> feats = new ArrayList();
    for (int i = 1; i < nodes.size(); i++) {
      System.out.println("");
      double div = Precision.round(nodes.get(i - 1).getLongLat().distanceTo(nodes.get(i).getLongLat())/Settings.getDefaultMovementStepDistance(), 6);
      System.out.print("start node: " + nodes.get(i-1) + "end node: " + nodes.get(i) + "  usage: " + nodes.get(i).getUsage());
      System.out.print("    Distance: " + div + "   bearing: ");
      System.out.println(nodes.get(i - 1).getLongLat().calculateBearing(nodes.get(i).getLongLat()));
    }
  }
}
