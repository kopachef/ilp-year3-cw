package uk.ac.ed.inf.algorithm;

import com.mapbox.geojson.Feature;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.GeoJsonManager;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import static java.util.stream.Collectors.toList;

public class Graph {

  private Node[][] grid;

  public int getGridSize() {
    return gridSize;
  }

  private int gridSize;
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

  private void setGrid() {
    double longitudeStepSize = (southEastBoundLongitude - northWestBoundLongitude) / gridSize;
    double longitudeOffset = (longitudeStepSize / 2);

    double latitudeStepSize = (northWestBoundLatitude - southEastBoundLatitude) / gridSize;
    double latitudeOffset = (latitudeStepSize / 2);

    List<Double> longitudeCoordinates =
            generateDoubleSequence(northWestBoundLongitude,southEastBoundLongitude, longitudeStepSize, longitudeOffset);
    List<Double> latitudeCoordinates =
            generateDoubleSequence(southEastBoundLatitude, northWestBoundLatitude, latitudeStepSize, latitudeOffset);

    this.nearestNodeDistance = longitudeStepSize;
    double gridOffset = nearestNodeDistance / 2.0;
    double incrementalOffset = 0.000007;

    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        LongLat nodeLongLat = (i % 2) == 0 ? new LongLat(longitudeCoordinates.get(j), latitudeCoordinates.get(i) + i*incrementalOffset) :
                new LongLat(longitudeCoordinates.get(j) + gridOffset, latitudeCoordinates.get(i) + i*incrementalOffset);
        grid[i][j] = new Node(i, j);
        grid[i][j].setLongLat(nodeLongLat);
        // grid[i][j].setRestricted(GeoJsonManager.isInRestrictedArea(nodeLongLat));
        grid[i][j].setRestricted(
            GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat, nearestNodeDistance) ||
                !nodeLongLat.isConfined());
        /**
         * grid[i][j].setRestricted(GeoJsonManager.isInPerimeterOfRestrictedArea(nodeLongLat,
         * nearestNodeDistance)); Alterniative ethod s to be used if we dont want to exclude node
         * sthat a clise the resyturceted nodes.
         */
      }
    }
  }

  public static List<Double> generateDoubleSequence(
      double start, double end, double step, double offset) {
    return DoubleStream.iterate(start + offset, d -> d <= end, d -> d + step)
        .boxed()
        .map(x -> Precision.round(x, 6))
        .collect(toList());
  }

  public Node[][] getGrid() {
    return grid;
  }

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

  public List<Node> getAllNodes() {
    List<Node> result = new ArrayList<>();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        result.add(grid[i][j]);
      }
    }
    return result;
  }

  public List<Node> getShortestPath(LongLat startLocation, LongLat destinationLocation) {
    Node startNode = null;
    Node endNode = null;
    //setGrid();

    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        startNode = startNode == null ? grid[i][j] : startNode;
        endNode = endNode == null ? grid[i][j] : endNode;

        if ((grid[i][j].getLongLat().closeTo(startLocation))
            && (grid[i][j].getLongLat().distanceTo(startLocation)
                <= startNode.getLongLat().distanceTo(startLocation))) {
          //grid[i][j].setLongLat(startLocation);
          startNode = grid[i][j];

        } else if ((grid[i][j].getLongLat().closeTo(destinationLocation))
            && (grid[i][j].getLongLat().distanceTo(destinationLocation)
                <= endNode.getLongLat().distanceTo(destinationLocation))) {
          //grid[i][j].setLongLat(destinationLocation);
          endNode = grid[i][j];
        }
      }
    }
    AStar aStar = new AStar(getGrid(), startNode, endNode);
    return aStar.findPath();
  }

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

  public void printDistanceBetweenNodes(List<Node> nodes) {
    List<Feature> feats = new ArrayList();
    for (int i = 1; i < nodes.size(); i++) {
      System.out.println("BEARING AND DISTANCE");
      double div = Precision.round(nodes.get(i - 1).getLongLat().distanceTo(nodes.get(i).getLongLat())/Settings.getDefaultMovementStepDistance(), 6);
      System.out.println("Distance: " + div);
      System.out.println(nodes.get(i - 1).getLongLat().calculateBearing(nodes.get(i).getLongLat()));
    }
  }
}
