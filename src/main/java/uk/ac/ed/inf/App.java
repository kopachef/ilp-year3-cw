package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;

import java.sql.Date;
import java.util.List;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/** Hello world! */
public class App {

  public static void main(String[] args) {
    int granularity = 54;

    LongLat home = Settings.getDefaultHomeLocation();
    LongLat devloc = new LongLat(-3.187837, 55.943497);

    LongLat end = new LongLat(-3.1898600, 55.942931);
    LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190, 55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);

    Graph g =
            new Graph(
                Settings.getDefaultNorthWestBound().longitude,
                Settings.getDefaultNorthWestBound().latitude,
                Settings.getDefaultSouthEastBound().longitude,
                Settings.getDefaultSouthEastBound().latitude,
                granularity);

     //DeliveryPlanner deliveryPlanner = new DeliveryPlanner(Date.valueOf("2023-12-19"));
     //deliveryPlanner.generatePathMap();
     //System.out.println(deliveryPlanner.getDeliveryPaths());

    List<Node> path = g.getShortestPath(devloc, dest2);
    List<Point> pts = GeoJsonManager.generatePointsFromNodes(path);
    LineString ls = LineString.fromLngLats(pts);
    Feature feats = Feature.fromGeometry((Geometry) ls);

    List<Point> pt = GeoJsonManager.generatePointsFromNodes(g.getAllNodes());
    List<Feature> feats2 = pt.stream().map(x -> Feature.fromGeometry((Geometry) x)).collect(toList());
    feats2.add(feats);

    FeatureCollection fc = FeatureCollection.fromFeatures(feats2);

    for(int i = 1;i < path.size(); i++) {
      //System.out.println("Node angle: " + path.get(i-1).calculateAngleTo(path.get(i))+ "LongLat Angle: " + path.get(i-1).getLongLat().calculateBearing(path.get(i).getLongLat()));
    }
    System.out.println(fc.toJson());
    }
}