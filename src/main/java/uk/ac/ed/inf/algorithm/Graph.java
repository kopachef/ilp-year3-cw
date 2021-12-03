package uk.ac.ed.inf.algorithm;

import com.mapbox.geojson.Feature;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.dataio.GeoJsonManager;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.utils.Settings;

import java.util.*;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

public class Graph {

  private final Node[][] grid;
  private final int gridSize;
  private final HashMap<LongLat, LongLat> nodeLonglatToTargetLonglat = new HashMap<>();
  private final double northWestBoundLongitude;
  private final double northWestBoundLatitude;
  private final double southEastBoundLongitude;
  private final double southEastBoundLatitude;

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

  /**
   * This method generates a list of doubles that represent a sequence starting at the given start
   * value, ending at the given end value, incremented by the given step value, and offset by the
   * given offset. The returned list will be sorted in ascending order.
   *
   * <p>Key use case of this includes grid point generation logic. Points generated exactly on the
   * bounded of the confinement area are considered restricted. We add an offset to shift these
   * points slightly inwards.
   *
   * @param start start value
   * @param end end value
   * @param step size of a single step
   * @param offset offset value
   * @return generated double sequence
   */
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
   * being equal to the length of single drone step. Tt restricts each of these points to the
   * boundaries of one of the following:
   * <li>the nearest node distance (set by this.nearestNodeDistance)
   * <li>The perimeter of a restricted area (set by GeoJsonManager.isInPerimeterOfRestrictedArea).
   *     Considering the use of an additional perimeter value allows us to prevent points from
   *     spawning too close to a restricted area.
   */
  private void setGrid() {
    double longitudeStepSize = Settings.getDefaultMovementStepDistance();
    double longitudeOffset = 0;
    double latitudeStepSize = Settings.getDefaultMovementStepDistance();
    double latitudeOffset = 0;

    List<Double> longitudeCoordinates =
        generateDoubleSequence(
            northWestBoundLongitude, southEastBoundLongitude, longitudeStepSize, longitudeOffset);
    List<Double> latitudeCoordinates =
        generateDoubleSequence(
            southEastBoundLatitude, northWestBoundLatitude, latitudeStepSize, latitudeOffset);

    List<LongLat> topRowCoords = new ArrayList<>();

    double consecutiveNodeDistance =
        Math.abs(
            (northWestBoundLongitude - southEastBoundLongitude)
                / Settings.getDefaultMovementStepDistance());
    int coordListLength = (int) Math.round(consecutiveNodeDistance);

    LongLat nxt = Settings.getDefaultNorthWestBound();
    for (int i = 0; i < coordListLength; i++) {
      nxt = nxt.nextPosition(0);
      topRowCoords.add(nxt);
    }
    int rightRotationAngle = 240;
    int leftRotationAngle = rightRotationAngle + 60;
    LongLat nodeLongLat;

    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {

        if (i == 0) {
          nodeLongLat = topRowCoords.get(j);
          grid[i][j] = new Node(i, j);
          grid[i][j].setLongLat(nodeLongLat);
          grid[i][j].setRestricted(
              GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat, longitudeStepSize)
                  || !nodeLongLat.isConfined());
        } else {
          if (i % 2 == 0) {
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
          nodeLongLat = new LongLat(nodeLongLat.longitude, nodeLongLat.latitude);
          grid[i][j] = new Node(i, j);
          grid[i][j].setLongLat(nodeLongLat);
          grid[i][j].setRestricted(
              GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat, longitudeStepSize)
                  || !nodeLongLat.isConfined());
        }
      }
    }
  }

  /**
   * Returns the grid mesh.
   *
   * @return grid mesh
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
      result.addAll(Arrays.asList(grid[i]).subList(0, gridSize));
    }
    return result;
  }

  /**
   * The getShortestPath() method finds the shortest path between two points on a two-dimensional
   * grid. The grid in this case will be our graph. The startLocation and destinationLocation will
   * be the nodes being traversed. As a preprocessing step, the getShortestPath() method will
   * iterate through all nodes in the grid and find the nodes that are the closest to the
   * startLocation and destinationLocation. It will then choose the node that is closest to the
   * startLocation and destinationLocation on our graph and call the AStar algorithm to find the
   * shortest path between these.
   *
   * @param startLocation start longlat.
   * @param destinationLocation destination longlat.
   * @return A list of Nodes forming the shortest path.
   */
  public List<Node> getShortestPath(LongLat startLocation, LongLat destinationLocation) {
    Node startNode = null;
    Node endNode = null;
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
    nodeLonglatToTargetLonglat.put(Objects.requireNonNull(startNode).getLongLat(), startLocation);
    nodeLonglatToTargetLonglat.put(endNode.getLongLat(), destinationLocation);
    AStar aStar = new AStar(getGrid(), startNode, endNode);
    return aStar.findPath();
  }

  /**
   * This method finds the nearest node to a given longitude and latitude. It starts at the given
   * location and looks for the nearest node in all directions. If the current node is close to the
   * startLocation and the distance to the startLocation is shorter than the current node's distance
   * to the target, then the node picked as a potential return value.
   *
   * @param longlat reference point
   * @return nearest node off all the nodes observed.
   */
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

  /**
   * This method resets the node usages in the grid to Node.NodeUsage.ORDINARY.
   */
  public void resetNodeUsages() {
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        grid[i][j].setNodeUsage(Node.NodeUsage.ORDINARY);
      }
    }
  }

  /**
   * <p>This method returns the HashMap<LongLat, LongLat> that maps LongLat coordinates to their target
   * LongLat coordinates.</p>
   *
   * <p>This is important because when calculating a distance to a target location, the nearest node to the actual target
   * is what is used as the final destination since it is guaranteed to be closeTo to the target. This HashMap helps
   * us lookup the original target location.</p>
   *
   * @return Node Longlat to original target Longlat
   */
  public HashMap<LongLat, LongLat> getNodeLonglatToTargetLonglat() {
    return nodeLonglatToTargetLonglat;
  }

  /**
   * Testing utility function to calculate and preview the distance and bearing between nodes given as in a List.
   *
   * @param nodes node list.
   */
  public void printDistanceBetweenNodes(List<Node> nodes) {
    for (int i = 1; i < nodes.size(); i++) {
      System.out.println();
      double div =
          Precision.round(
              nodes.get(i - 1).getLongLat().distanceTo(nodes.get(i).getLongLat())
                  / Settings.getDefaultMovementStepDistance(),
              6);
      System.out.print(
          "start node: "
              + nodes.get(i - 1)
              + "end node: "
              + nodes.get(i)
              + "  usage: "
              + nodes.get(i).getUsage());
      System.out.print("    Distance: " + div + "   bearing: ");
      System.out.println(nodes.get(i - 1).getLongLat().calculateBearing(nodes.get(i).getLongLat()));
    }
  }
}
