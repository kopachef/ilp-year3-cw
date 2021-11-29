package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.apache.commons.math3.util.Precision;
import org.checkerframework.checker.units.qual.A;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;
import uk.ac.ed.inf.algorithm.Utils;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/** Hello world! */
public class App {

  public static void main(String[] args) {
    int granularity = 54;

    LongLat home = Settings.getDefaultHomeLocation();
    LongLat devloc = new LongLat(-3.187837, 55.943497);

    //LongLat end = new LongLat(-3.1898600, 55.942931);
    //LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190, 55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);

    Graph g =
        new Graph(
            Settings.getDefaultNorthWestBound().longitude,
            Settings.getDefaultNorthWestBound().latitude,
            Settings.getDefaultSouthEastBound().longitude,
            Settings.getDefaultSouthEastBound().latitude,
            granularity);

    DeliveryPlanner deliveryPlanner = new DeliveryPlanner(Date.valueOf("2023-12-23"));
    deliveryPlanner.generatePathMap();

    List<Node> path = deliveryPlanner.getAPath();

    System.out.println(path.size());
    List<Feature> feats = GeoJsonManager.getRestrictedAreasFeatures();
//    List<DatabaseIO.Delivery> deliveries = deliveryPlanner.getDeliveries();
//    List<DatabaseIO.FlightPath> flightPaths = deliveryPlanner.getFlightPaths();

//    for(DatabaseIO.Delivery d : deliveries) {
//      //System.out.println(d.orderNo + "   " + d.deliveredTo + "     " + d.costInPence);
//    }
//
//    for(DatabaseIO.FlightPath f : flightPaths) {
//      //System.out.println(f.angle + "     " + f.orderNo);
//    }

//    List<Point> ls = new ArrayList<>();
//    ls.add(Point.fromLngLat(flightPaths.get(0).fromLongitude, flightPaths.get(0).fromLatitude));
//    for(DatabaseIO.FlightPath fp : flightPaths) {
//      ls.add(Point.fromLngLat(fp.toLongitude, fp.toLatitude));
//    }

    /**
     * qworkable solution for the mean tinme. still getting netrsectiung edges.
     */
    //List<Node> path = g.getShortestPath(dest2, start);


//    for (int j = 0; j < path.size()-2; j++) {
//      for (int i = j; i < path.size() - 2; i++) {
//        //        List<Point> pts =
//        //            GeoJsonManager.generatePointsFromNodes(
//        //                Arrays.asList(path.get(j), path.get(i + 1), path.get(i + 2),
//        // path.get(j)));
//        //        LineString ls = LineString.fromLngLats(pts);
//        //        Polygon p = Polygon.fromOuterInner(ls);
//        if (GeoJsonManager.lineOfSight(path.get(j), path.get(i+2))) {
//          break;
//        } else {
//          if(path.get(i+1).getUsage() == Node.NodeUsage.ORDINARY) {
//
//            //feats.add(Feature.fromGeometry((Geometry) ls));
//            //feats.add(Feature.fromGeometry((Geometry) p));
//            path.remove(i+1);
//          }
//        }
//
//        // System.out.println(GeoJsonManager.intersectsRestrictedArea(p));
//      }
//    }
//    List<Point> pathPoints = GeoJsonManager.generatePointsFromNodes(path);
//    feats.add(Feature.fromGeometry((Geometry) LineString.fromLngLats(pathPoints)));
//
//    for (int j = 0; j < path.size()-2; j++) {
//      for (int i = j; i < path.size() - 2; i++) {
//        List<Point> pts =
//                GeoJsonManager.generatePointsFromNodes(
//                        Arrays.asList(path.get(j), path.get(i + 1), path.get(i + 2), path.get(j)));
//        LineString ls = LineString.fromLngLats(pts);
//        Polygon p = Polygon.fromOuterInner(ls);
//        if (GeoJsonManager.intersectsRestrictedArea(p)) {
//          break;
//        } else {
//          if(path.get(i+1).getUsage() == Node.NodeUsage.ORDINARY) {
//            //feats.add(Feature.fromGeometry((Geometry) ls));
//            //feats.add(Feature.fromGeometry((Geometry) p));
//            path.remove(i+1);
//
//          }
//        }
//       }}

//        // System.out.println(GeoJsonManager.intersectsRestrictedArea(p));
//      }
//    }

    //g.printDistanceBetweenNodes(path);


    //g.printDistanceBetweenNodes(path);

//    for (int i = 0; i < path.size() - 2; i++) {
//        List<Point> pts =
//            GeoJsonManager.generatePointsFromNodes(
//                Arrays.asList(path.get(i), path.get(i + 1), path.get(i + 2), path.get(i)));
//        LineString ls = LineString.fromLngLats(pts);
//        Polygon p = Polygon.fromOuterInner(ls);
//        if (!GeoJsonManager.intersectsRestrictedArea(p)) {
//          path.remove(i+1);
//          //feats.add(Feature.fromGeometry((Geometry) p));
//        }
//
//        // System.out.println(GeoJsonManager.intersectsRestrictedArea(p));
//      }

    //System.out.println(FeatureCollection.fromFeature(Feature.fromGeometry((Geometry) ls)).toJson());
    // System.out.println(deliveryPlanner.getDeliveryPaths());

    //    List<Node> path = g.getShortestPath(devloc, dest2);
    //    List<Point> pts = GeoJsonManager.generatePointsFromNodes(path);
    //    LineString ls = LineString.fromLngLats(pts);
    //    Feature feats = Feature.fromGeometry((Geometry) ls);
    //
    //    List<Point> pt = GeoJsonManager.generatePointsFromNodes(g.getAllNodes());
    //    List<Feature> feats2 = pt.stream().map(x -> Feature.fromGeometry((Geometry)
    // x)).collect(toList());
    //    feats2.add(feats);
    //    List<Feature> res = GeoJsonManager.getRestrictedAreasFeatures();
    //    feats2.addAll(res);
    //
    //    FeatureCollection fc = FeatureCollection.fromFeatures(feats2);
    //
    //    //g.printDistanceBetweenNodes(path);
    //    for(int i = 1;i < path.size(); i++) {
    //      //System.out.println("Node angle: " + path.get(i-1).calculateAngleTo(path.get(i))+
    // "LongLat Angle: " + path.get(i-1).getLongLat().calculateBearing(path.get(i).getLongLat()));
    //    }
    //    //System.out.println(fc.toJson());
    //    }

//    List<Node> path = deliveryPlanner.getAPath();
//    System.out.println(path.size());
//    List<Feature> feats = GeoJsonManager.getRestrictedAreasFeatures();
//
//    int smoothGranularity = 9;
//    for (int j = 0; j < path.size() - smoothGranularity; j++) {
//      for (int i = j; i < path.size() - smoothGranularity; i++) {
//        List<Point> pts =
//                GeoJsonManager.generatePointsFromNodes(
//                        Arrays.asList(path.get(j), path.get(i + 1), path.get(i + smoothGranularity), path.get(j)));
//        LineString ls = LineString.fromLngLats(pts);
//        Polygon p = Polygon.fromLngLats(Collections.singletonList(pts));
//        if (GeoJsonManager.intersectsRestrictedArea(p)) {
//          break;
//        } else {
//          if(path.get(i+1).getUsage() == Node.NodeUsage.ORDINARY) {
//            //feats.add(Feature.fromGeometry((Geometry) ls));
//            //feats.add(Feature.fromGeometry((Geometry) p));
//            path.remove(i+1);
//
//          }
//        }
//
//        // System.out.println(GeoJsonManager.intersectsRestrictedArea(p));
//      }
//    }



//    double angleDebt = 0;
//    double distanceDebt = 0;
//    boolean previousNodeSet = true;
//
//    for (int i = 0; i < path.size() - 1; i++) {
//      Node curr = path.get(i);
//      Node next = path.get(i + 1);
//      double bearing = curr.getLongLat().calculateBearing(next.getLongLat());
//      double distance = curr.getLongLat().distanceTo(next.getLongLat());
//
//      int roundedAngle = Utils.roundOffToNearest10th(bearing);
//      double roundedDistance =
//          Utils.roundOffToNearestMultiple(distance, Settings.getDefaultMovementStepDistance());
//
//      int stepCount = (int) (roundedDistance / Settings.getDefaultMovementStepDistance());
//
//      // System.out.println("angle: " + bearing + " rounded angle: " + roundedAngle + " distance: "
//      // + distance + "   roudnedDist: " + roundedDistance + "step count: " + stepCount);
//
//       //angl;e projection options
//            int topAngle = 0;
//            int bottomAngle = 0;
//
//            if(roundedAngle > bearing) {
//              topAngle = roundedAngle;
//              bottomAngle = topAngle - 10;
//            } else {
//              topAngle = roundedAngle + 10;
//              bottomAngle = roundedAngle;
//            }
//      LongLat posUpper = curr.getLongLat();
//      LongLat posLower = curr.getLongLat();
//
//      while (stepCount > 0) {
//        posUpper = posUpper.nextPosition(roundedAngle);
//        posLower = posLower.nextPosition(bottomAngle);
//        stepCount -= 1;
//      }
//
//      //next.setLongLat(posLower);
//      //reassignment
//      if(next.getUsage() == Node.NodeUsage.ORDINARY) {
//        if(!GeoJsonManager.isInRestrictedArea(posUpper) && posUpper.isConfined() && !GeoJsonManager.crossesRestricedArea(curr.getLongLat(), posUpper)) {
//          next.setLongLat(posUpper);
//        } else if(!GeoJsonManager.isInRestrictedArea(posLower) && posLower.isConfined() && !GeoJsonManager.crossesRestricedArea(curr.getLongLat(), posLower)) {
//          next.setLongLat(posLower);
//        } else {
//          previousNodeSet = false;
//        }
//      } else {
//        LongLat trueLocation = deliveryPlanner.getMapping().get(next.getLongLat());
//        if(!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), posUpper)) {
//          next.setLongLat(posUpper);
//        } else if(!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), posLower)) {
//          next.setLongLat(posLower);
//        } else {
//          System.out.println("Could nopt find alternate for ordinary node:  " + i+1 );
//          Node rebound = new Node(0,0);
//          double longitude = (curr.getLongLat().longitude + next.getLongLat().longitude)/2;
//          double latitude = (curr.getLongLat().latitude + next.getLongLat().latitude)/2;
//
//          LongLat mid = new LongLat(longitude, latitude);
//          LongLat upperProjection = mid;
//          LongLat lowerProjection = mid;
//
//          boolean reboundSet = false;
//
//          while (!reboundSet) {
//            upperProjection = upperProjection.nextPosition((roundedAngle - 90) % 360);
//            lowerProjection = lowerProjection.nextPosition((roundedAngle + 90) % 360);
//
//            if (!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), upperProjection)
//                && !GeoJsonManager.crossesRestricedArea(upperProjection, next.getLongLat())) {
//              rebound.setLongLat(upperProjection);
//              rebound.setNodeUsage(Node.NodeUsage.ORDINARY);
//              path.add(i + 1, rebound);
//              reboundSet = true;
//            } else if (!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), lowerProjection)
//                    && !GeoJsonManager.crossesRestricedArea(lowerProjection, next.getLongLat())){
//              rebound.setLongLat(lowerProjection);
//              rebound.setNodeUsage(Node.NodeUsage.ORDINARY);
//              path.add(i + 1, rebound);
//              reboundSet = true;
//            }
//            previousNodeSet = false;
//          }
//        }
//      }
//    }




    for(int i = 0; i < path.size(); i++)  {
      if(path.get(i).getUsage() != Node.NodeUsage.ORDINARY) {
        Node current = path.get(i);
        LongLat oldLocation = current.getLongLat();
        LongLat trueLocation = deliveryPlanner.getMapping().get(oldLocation);
        current.setLongLat(trueLocation);
        path.set(i, current);
      }
    }

    List<Node> path2 = new ArrayList<>(path);
    HashMap<Node, Integer> idxLookUp = new HashMap<>();
    for(int i = 0; i < path.size(); i++) {
      idxLookUp.put(path2.get(i), i);
    }
//
    int cycle = 2;
    while (cycle > 0) {
      for (int j = 0; j < path.size() - 2; j++) {
        if (!GeoJsonManager.crossesRestricedArea(path.get(j).getLongLat(), path.get(j + 2).getLongLat())
                && (path.get(j + 1).getUsage() == Node.NodeUsage.ORDINARY)) {
          path.remove(j + 1);
        }
      }
      cycle -= 1;
    }
//    System.out.println(path.size());
//
//
//    //path2.add(path.get(0));
    double angleDebt = 0;
    double distanceDebt = 0;

    for (int i = 0; i < path.size() - 1; i++) {
      Node curr = path.get(i);
      Node next = path.get(i + 1);

      double bearing = curr.getLongLat().calculateBearing(next.getLongLat()) + angleDebt;
      double distance = curr.getLongLat().distanceTo(next.getLongLat()) + distanceDebt;

      int roundedAngle = Utils.roundOffToNearest10th(bearing);
      double roundedDistance =
          Utils.roundOffToNearestMultiple(distance, Settings.getDefaultMovementStepDistance());
      int stepCount = (int) (roundedDistance / Settings.getDefaultMovementStepDistance());

//      distanceDebt = distance - roundedDistance;
//      angleDebt = bearing - roundedAngle;

      LongLat pos1 = curr.getLongLat();
      LongLat pos2= curr.getLongLat();
      LongLat pos3 = curr.getLongLat();
      LongLat pos4 = curr.getLongLat();
      LongLat pos5 = curr.getLongLat();
      LongLat pos6 = curr.getLongLat();
      LongLat pos7 = curr.getLongLat();
      LongLat pos8 = curr.getLongLat();
      LongLat pos9 = curr.getLongLat();
      LongLat pos10 = curr.getLongLat();
      LongLat pos11 = curr.getLongLat();
      LongLat pos12 = curr.getLongLat();
      LongLat pos13 = curr.getLongLat();
//////////      Node rebound = new Node(0, 0);
//////////
//////////      double longitude = (curr.getLongLat().longitude + next.getLongLat().longitude) / 2;
//////////      double latitude = (curr.getLongLat().latitude + next.getLongLat().latitude) / 2;
//////////
//////////      LongLat mid = new LongLat(longitude, latitude);
//////////      LongLat upperProjection = mid;
//////////      LongLat lowerProjection = mid;
//////////
//////////      boolean reboundSet = false;
//////////
//////////      if (path.get(i + 1).getUsage() != Node.NodeUsage.ORDINARY) {
//////////        while (!reboundSet) {
//////////          upperProjection = upperProjection.nextPosition((roundedAngle - 90) % 360);
//////////          lowerProjection = lowerProjection.nextPosition((roundedAngle + 90) % 360);
//////////
//////////          if (!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), upperProjection)
//////////              && !GeoJsonManager.crossesRestricedArea(upperProjection, next.getLongLat())) {
//////////            rebound.setLongLat(upperProjection);
//////////            rebound.setNodeUsage(Node.NodeUsage.ORDINARY);
//////////            path2.add(path.get(i));
//////////            path2.add(rebound);
//////////            reboundSet = true;
//////////          } else if (!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), lowerProjection)
//////////              && !GeoJsonManager.crossesRestricedArea(lowerProjection, next.getLongLat())) {
//////////            rebound.setLongLat(lowerProjection);
//////////            rebound.setNodeUsage(Node.NodeUsage.ORDINARY);
//////////            path2.add(path.get(i));
//////////            path.add(rebound);
//////////            reboundSet = true;
//////////          }
//////////        }
//////////      }
    while(stepCount > 0){
        pos1 = pos1.nextPosition(roundedAngle);
        pos2 = pos2.nextPosition(roundedAngle + 10);
        pos3 = pos3.nextPosition(roundedAngle - 10);
        pos4 = pos4.nextPosition(roundedAngle + 20);
        pos5 = pos5.nextPosition(roundedAngle - 20);
        pos6 = pos6.nextPosition(roundedAngle + 30);
        pos7 = pos7.nextPosition(roundedAngle - 30);
        pos8 = pos8.nextPosition(roundedAngle + 40);
        pos9 = pos9.nextPosition(roundedAngle - 40);
        pos10 = pos10.nextPosition(roundedAngle + 50);
        pos11 = pos11.nextPosition(roundedAngle - 50);
        pos12 = pos12.nextPosition(roundedAngle + 60);
        pos13 = pos13.nextPosition(roundedAngle - 60);
        stepCount-=1;
    }
    List<LongLat> positions = Arrays.asList(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pos10, pos11);

//    int counter = 5;
//    while(counter > 0 && GeoJsonManager.crossesRestricedArea(curr.getLongLat(), pos)){
//      pos = pos.nextPosition(roundedAngle);
//      counter+=1;
//    }
      for(LongLat l : positions) {
        if(!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), l) && !GeoJsonManager.isInRestrictedArea(l)) {
          next.setLongLat(l);
          break;
        }
        if(l == pos13){
          System.out.println("could not find any pos");
        }
      }

      path2.set(i + 1, next);
      path.set(i + 1, next);
    }


//    for (int j = 0; j < path.size() - 2; j++) {
//      if (!GeoJsonManager.crossesRestricedArea(path.get(j).getLongLat(), path.get(j + 2).getLongLat())
//              && (path.get(j + 1).getUsage() == Node.NodeUsage.ORDINARY)
//              && (Utils.isWholeNumber(path.get(j).getLongLat().distanceTo(path.get(j+1).getLongLat())/Settings.getDefaultMovementStepDistance()))) {
//        path.remove(j + 1);
//      }
//    }

////////////
//    for (int i = 0; i < path2.size() - 1; i++) {
//      Node curr = path2.get(i);
//      Node next = path2.get(i + 1);
//
//      double bearing = curr.getLongLat().calculateBearing(next.getLongLat());
//      double distance = curr.getLongLat().distanceTo(next.getLongLat());
//
//      int roundedAngle = Utils.roundOffToNearest10th(bearing);
//      double roundedDistance =
//              Utils.roundOffToNearestMultiple(distance, Settings.getDefaultMovementStepDistance());
//      int stepCount = (int) (roundedDistance / Settings.getDefaultMovementStepDistance());
//
//      LongLat pos = curr.getLongLat();
//      while(stepCount > 0){
//        pos = pos.nextPosition(roundedAngle);
//        stepCount-=1;
//      }
//
//      path2.set(i+1, next);
//      next.setLongLat(pos);
//
//      path.set(i+1,next);
//    }
/////////////////
    //g.printDistanceBetweenNodes(path);
//    for(int i = 0; i<path.size()-1; i++){
//      if(GeoJsonManager.crossesRestricedArea(path.get(i).getLongLat(), path.get(i+1).getLongLat())){
//        Node curr = path.get(i);
//        Node next = path.get(i+1);
//        System.out.println("found a spamy: " + curr + "to : " + next);
//
//
//        List<Node> connectingPath = new ArrayList<>();
//        int start = idxLookUp.get(curr) + 1;
//        for(int j = start; j < path2.size(); j++){
//          if(path2.get(j) == next) {
//            break;
//          } else {
//            connectingPath.add(path2.get(j));
//          }
//        }
//        for(int j = connectingPath.size()-1; j >= 0; j--) {
//          path.add(i+1,connectingPath.get(j));
//        }
//      }
//    }

    g.printDistanceBetweenNodes(path);

    feats.addAll(deliveryPlanner.getMapping().values().stream().map(x -> Feature.fromGeometry((Geometry) GeoJsonManager.createPointFromLongLat(x))).collect(toList()));
    LineString ls = LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(path));
    feats.add(Feature.fromGeometry((Geometry) ls));
    System.out.println(FeatureCollection.fromFeatures(feats).toJson());


  }
}