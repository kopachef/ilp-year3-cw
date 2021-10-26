package uk.ac.ed.inf.graph;

import org.apache.commons.math3.util.Precision;
import org.checkerframework.checker.units.qual.A;
import uk.ac.ed.inf.GeoJsonManager;
import uk.ac.ed.inf.LongLat;

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

    public Graph(double northWestBoundLongitude, double northWestBoundLatitude,
                 double southEastBoundLongitude, double southEastBoundLatitude,
                 int gridSize){
        this.gridSize = gridSize;
        this.grid = new Node[gridSize][gridSize];
        this.northWestBoundLongitude = northWestBoundLongitude;
        this.northWestBoundLatitude = northWestBoundLatitude;
        this.southEastBoundLongitude = southEastBoundLongitude;
        this.southEastBoundLatitude = southEastBoundLatitude;

        double longitudeStepSize = Precision.round((southEastBoundLongitude - northWestBoundLongitude) / gridSize, 6);
        double longitudeOffset = Precision.round((longitudeStepSize / 2), 6);

        double latitudeStepSize = Precision.round((northWestBoundLatitude - southEastBoundLatitude) / gridSize, 6);
        double latitudeOffset = Precision.round((latitudeStepSize / 2), 6);

        List<Double> longitudeCoordinates =
                generateDoubleSequence(northWestBoundLongitude,southEastBoundLongitude, longitudeStepSize, longitudeOffset);
        List<Double> latitudeCoordinates =
                generateDoubleSequence(southEastBoundLatitude, northWestBoundLatitude, latitudeStepSize, latitudeOffset);

        this.nearestNodeDistance = longitudeStepSize;

        for(int i = 0; i < gridSize; i++) {
            for(int j = 0; j < gridSize; j++) {
                LongLat nodeLongLat = new LongLat(longitudeCoordinates.get(j), latitudeCoordinates.get(i));
                grid[i][j] = new Node(i, j);
                grid[i][j].setLongLat(nodeLongLat);
                grid[i][j].setRestricted(GeoJsonManager.isInRestrictedArea(nodeLongLat));
            }
        }
    }

    public static List<Double> generateDoubleSequence(double start, double end, double step, double offset) {
        return DoubleStream.iterate(start+offset, d -> d <= end, d -> d + step)
                .boxed()
                .map(x -> Precision.round(x, 6))
                .collect(toList());
    }

    public Node[][] getGrid() {
        return grid;
    }

    public List<Node> getRestrictedNodes() {
        List<Node> result = new ArrayList<>();
        for(int i = 0; i < gridSize; i++) {
            for(int j = 0; j < gridSize; j++) {
                if(grid[i][j].isRestricted()) {
                    result.add(grid[i][j]);
                }
            }
        }
        return result;
    }

    public List<Node> getAllNodes() {
        List<Node> result = new ArrayList<>();
        for(int i = 0; i < gridSize; i++) {
            for(int j = 0; j < gridSize; j++) {
                result.add(grid[i][j]);
            }
        }
        return result;
    }

    public List<Node> getShortestPath(LongLat startLocation, LongLat destinationLocation) {
        Node startNode = null;
        Node endNode = null;

        for(int i = 0; i < gridSize; i++) {
            for(int j = 0; j < gridSize; j++) {
                startNode = startNode == null ? grid[i][j] : startNode;
                endNode = endNode == null ? grid[i][j] : endNode;

                if ((grid[i][j].getLongLat().distanceTo(startLocation) <= (0.5 * nearestNodeDistance)) && (grid[i][j].getLongLat().distanceTo(startLocation) < startNode.getLongLat().distanceTo(startLocation))) {
                    grid[i][j].setLongLat(startLocation);
                    startNode = grid[i][j];

                } else if ((grid[i][j].getLongLat().distanceTo(destinationLocation) <= (0.5 * nearestNodeDistance)) && (grid[i][j].getLongLat().distanceTo(destinationLocation) < endNode.getLongLat().distanceTo(destinationLocation))) {
                    grid[i][j].setLongLat(destinationLocation);
                    endNode = grid[i][j];
                }
            }
        }
        AStar aStar = new AStar(getGrid(), startNode, endNode);
        return aStar.findPath();
    }

}
