package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfTransformation;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.graph.Graph;
import uk.ac.ed.inf.graph.Node;

import javax.sound.sampled.Line;
import java.sql.Date;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/** Hello world! */
public class App {




    public static List<Double> generateDoubleSequence(double start, double end, double step, double offset) {
        return DoubleStream.iterate(start+offset, d -> d <= end-offset, d -> d + step)
                .boxed()
                .map(x -> Precision.round(x, 6))
                .collect(toList());
    }

  public static void main(String[] args) {
    int granularity = 55;

    LongLat home = Settings.getDefaultHomeLocation();
    LongLat devloc = new LongLat(-3.187837, 55.943497);

    LongLat end = new LongLat(-3.1898600, 55.942931);
    LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190, 55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);

    Graph g =
            new Graph(
                Settings.getDefaultNorthwestBoundLongitude(),
                Settings.getDefaultNorthwestBoundLatitude(),
                Settings.getDefaultSoutheastBoundLongitude(),
                Settings.getDefaultSoutheastBoundLatitude(),
                granularity);

    //List<Node> path = g.getShortestPath(home, devloc);
//    List<Node> adjusted = g.smoothenPath(path);
//    System.out.println(path);
//    for(int i = 0; i < path.size()-1; i++) {
//        Node st = path.get(i);
//        Node ed = path.get(i + 1);
//        System.out.println(st.getLongLat() + "\n" + ed.getLongLat());
//        System.out.println(st.getLongLat().distanceTo(ed.getLongLat()));
//    }
    //
    //    List<Feature> path =
    //        GeoJsonManager.generatePointsFromNodes(g.getRestrictedNodes()).stream()
    //            .map(x -> Feature.fromGeometry((Geometry) x))
    //            .collect(toList());
    //
        List<Feature> feats = GeoJsonManager.getRestrictedAreasFeatures();
        //feats.addAll(path);

      LineString ls =
     LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(g.getShortestPath(home,
     devloc)));
    //    LineString ls2 =
    // LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(g.getShortestPath(devloc,
    // home)));
    //
    //
    feats.add(Feature.fromGeometry((Geometry) ls));
    //    feats.add(Feature.fromGeometry((Geometry) ls2));
    //    feats.add(Feature.fromGeometry((Geometry)
    // TurfTransformation.circle(GeoJsonManager.createPointFromLongLat(dest2), 0.00015)));
    //System.out.println(GeoJsonManager.createFeatureCollection(feats).toJson());

    // Polygon pp = TurfTransformation.circle(GeoJsonManager.createPointFromLongLat(dest2),
    // 0.00015);
    // Feature f = Feature.fromGeometry(pp);
    // System.out.println(pp.toJson());

    // DatabaseIO dbo = new DatabaseIO(Settings.getDefaultDatabaseHost(),
    // Settings.getDefaultDatabasePort());

    // System.out.println(dbo.queryOrders("", Date.valueOf("2023-10-09"),"","").stream().map(x ->
    // x.deliveryDate).collect(toList()));

    //    Drone drone = new Drone(1, 1500);
    //    OrderDeliveryWorker odw = new OrderDeliveryWorker(drone, Date.valueOf("2023-12-27"));
    //
    //    //System.out.println(odw.getFoodOrderQueue());
    //    System.out.println(odw.getFoodOrderQueueSize());
    //    odw.populateFoodOrders();
    //    System.out.println(odw.getFoodOrderQueueSize());
    //    while(odw.getFoodOrderQueueSize() > 0) {
    //          FoodOrder fo = odw.getFoodOrderQueue().poll();
    //          double cost = fo.getDeliveryCost();
    //          double distance =
    // fo.getDeliveryLocationLongLat().distanceTo(drone.getCurrentPosition());
    //          System.out.println("cost: " + cost +" dist: " + (int)(distance * 100000) + " ratio:
    // " +
    //     (int)(cost/distance/10000) + "     item: " + fo.getOrderItems().stream().map(x ->
    //     x.getName()).collect(toList()));
    //        }
    //    System.out.println(odw.getFoodOrderQueue());
    //    odw.populateFoodOrders();
    //    System.out.println(odw.getFoodOrderQueueSize());
    //    odw.getFoodOrderQueue().poll();
    //    System.out.println(odw.getFoodOrderQueueSize());
    //    drone.setCurrentPosition(Settings.businessSchool);
    //    odw.populateFoodOrders();
    //    odw.updateFoodOrders(drone, odw.getFoodOrderQueue());
    //    while(odw.getFoodOrderQueueSize() > 0) {
    //      FoodOrder fo = odw.getFoodOrderQueue().poll();
    //      double cost = fo.getDeliveryCost();
    //      double distance =
    // fo.getDeliveryLocationLongLat().distanceTo(drone.getCurrentPosition());
    //      System.out.println("cost: " + cost +" dist: " + (int)(distance * 100000) + " ratio: " +
    //              (int)(cost/distance/10000) + "     item: " + fo.getOrderItems().stream().map(x
    // ->
    //              x.getName()).collect(toList()));
    //    }
    //    for(int i = 0; i < odw.getFoodOrderQueue().size(); i++) {
    //      FoodOrder fo = odw.getFoodOrderQueue().poll();
    //      double cost = fo.getDeliveryCost();
    //      double distance =
    // fo.getDeliveryLocationLongLat().distanceTo(drone.getCurrentPosition());
    //      System.out.println("cost: " + cost +" dist: " + (int)(distance * 100000) + " ratio: " +
      // (int)(cost/distance/10000) + "     item: " + fo.getOrderItems().stream().map(x ->
    // x.getName()).collect(toList()));
    //    }

     DeliveryPlanner deliveryPlanner = new DeliveryPlanner(Date.valueOf("2023-12-15"));
     deliveryPlanner.generatePathMap();
     //System.out.println(deliveryPlanner.getDeliveryPaths());

//      List<Node> path = g.getShortestPath(home, devloc);
//        Node cur = null;
//        for(Node node : path) {
//            if(cur == null) {cur = node; continue;}
//            double distance = node.getLongLat().distanceTo(cur.getLongLat());
//            cur = node;
//            System.out.println(distance);
//        }
//
//    System.out.println("-------------");
//      List<Node> smoothpath = g.smoothenPath(path);
//      Node curr = null;
//      for(Node node : smoothpath) {
//          if(cur == null) {curr = node; continue;}
//          double distance = node.getLongLat().distanceTo(cur.getLongLat());
//          cur = node;
//          System.out.println(distance);
//      }
    }
}