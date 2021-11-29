package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DeliveryPlanner {

  /**
   * Delivery Planner manages drones delivery by keeping track of the following:
   *
   * <p>- Creates an instance of the OrderDeliveryWorker which is used to get FoodOrders. - Checking
   * if the drone can fulfill and order before deploying it(including checking that it will have
   * enough battery capacity to get back home) - Updating the FoodOrder Queue based on the drone's
   * location. - Generating the delivery path as json output file. - Keeping a hashmap of the
   * partial delivery path to avoid recalculation.
   */
  private final Date deliveryDate;
  private final LinkedList<FoodOrder> deliverableOrders = new LinkedList<>();
  private final HashMap<FoodOrder, List<Node>> deliveryPaths = new HashMap<>();
  private final Drone drone = new Drone(1500);
  private final OrderDeliveryWorker orderDeliveryWorker;
  private final List<DatabaseIO.Delivery> deliveries = new ArrayList<>();
  private final List<DatabaseIO.FlightPath> flightPaths = new ArrayList<>();
  private final int totalOrderCount;
  private final Graph graph =
      new Graph(
          Settings.getDefaultNorthWestBound().longitude,
          Settings.getDefaultNorthWestBound().latitude,
          Settings.getDefaultSouthEastBound().longitude,
          Settings.getDefaultSouthEastBound().latitude,
          Settings.GRID_GRANULARITY);
  List<Node> pickupNodes = new ArrayList<>();
  private List<Node> pathToHome = new LinkedList<>();
  private double totalOrderValue = 0;

  public DeliveryPlanner(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
    this.orderDeliveryWorker = new OrderDeliveryWorker(drone, deliveryDate);
    this.orderDeliveryWorker.populateFoodOrders();
    totalOrderCount = orderDeliveryWorker.getFoodOrderQueueSize();
    drone.setCurrentPosition(graph.findNearestNode(drone.getCurrentPosition()).getLongLat());
    updateFoodDeliveryList();
  }

  private void updateFoodDeliveryList() {
    boolean canDeliver = true;
    //TODO remove this shit!!!
//    while(orderDeliveryWorker.getFoodOrderQueueSize() > 2) {
//      orderDeliveryWorker.getFoodOrder();
//    }
    while (canDeliver && orderDeliveryWorker.getFoodOrderQueueSize() > 0) {
      orderDeliveryWorker.updateFoodOrders(drone, orderDeliveryWorker.getFoodOrderQueue());
      FoodOrder foodOrder = orderDeliveryWorker.getFoodOrder();
      graph.resetNodeUsages();
      List<LongLat> pickUpLocations = foodOrder.getPickUpLocations();
      LongLat deliveryLongLat = foodOrder.getDeliveryLocationLongLat();
      LongLat startLocation = drone.getCurrentPosition();
      List<Node> pathToClient = new ArrayList<>();
      for (LongLat longlat : pickUpLocations) {
        List<Node> path = graph.getShortestPath(startLocation, longlat);
        setLastNodeUsage(path, Node.NodeUsage.PICKUP);
        if (!pathToClient.isEmpty()
            && pathToClient.get(pathToClient.size() - 1).equals(path.get(0))) {
          path.remove(0);
          pathToClient.addAll(path);
        } else {
          pathToClient.addAll(path);
        }
        startLocation = longlat;
        Node placeHolderNode = new Node(0, 0);
        placeHolderNode.setLongLat(longlat);
        if (!pickupNodes.contains(placeHolderNode)) {
          pickupNodes.add(placeHolderNode);
        }
      }
      List<Node> pathToFinalStation = graph.getShortestPath(startLocation, deliveryLongLat);
      setLastNodeUsage(pathToFinalStation, Node.NodeUsage.DROPOFF);
      pathToClient.addAll(pathToFinalStation);
      List<Node> pathFromClientToHome =
          graph.getShortestPath(deliveryLongLat, Settings.getDefaultHomeLocation());
      double travelDistance =
          graph.distanceBetweenNodes(pathToClient)
              + graph.distanceBetweenNodes(pathFromClientToHome);
      int estimatedBatteryUsage = drone.calculateMovementStepCost(travelDistance);
      if (drone.getBatteryLevel() - estimatedBatteryUsage > 0) {
        drone.setCurrentFoodOrder(foodOrder);
        executeMoves(drone, pathToClient);
        deliverableOrders.add(foodOrder);
        deliveryPaths.put(foodOrder, pathToClient);
        pathToHome = pathFromClientToHome;
      } else {
        canDeliver = false;
      }
    }
    setLastNodeUsage(pathToHome, Node.NodeUsage.HOME);
    executeMoves(drone, pathToHome);
  }

  public void executeMoves(Drone drone, List<Node> pathNodes) {
    for (Node node : pathNodes) {
      switch (node.getUsage()) {
        case PICKUP:
          DatabaseIO.FlightPath pickUpFlightPath = new DatabaseIO.FlightPath();
          pickUpFlightPath.angle = Settings.getDefaultHoverAngle();
          pickUpFlightPath.toLatitude = node.getLongLat().latitude;
          pickUpFlightPath.toLongitude = node.getLongLat().longitude;
          pickUpFlightPath.fromLatitude = node.getLongLat().latitude;
          pickUpFlightPath.fromLongitude = node.getLongLat().longitude;
          pickUpFlightPath.orderNo = drone.getCurrentFoodOrder().getOrderNo();

          drone.hoverDrone();
          drone.loadItems();
          drone.flyDrone();

          flightPaths.add(pickUpFlightPath);
          //System.out.println("picked order     " + node);
          break;
        case DROPOFF:
          DatabaseIO.Delivery dropOffDelivery = new DatabaseIO.Delivery();
          dropOffDelivery.deliveredTo = drone.getCurrentFoodOrder().getDeliveryW3wAddress();
          dropOffDelivery.costInPence = drone.getCurrentFoodOrder().getDeliveryCost();
          dropOffDelivery.orderNo = drone.getCurrentFoodOrder().getOrderNo();

          DatabaseIO.FlightPath dropOffFLightPath = new DatabaseIO.FlightPath();
          dropOffFLightPath.angle = Settings.getDefaultHoverAngle();
          dropOffFLightPath.toLatitude = node.getLongLat().latitude;
          dropOffFLightPath.toLongitude = node.getLongLat().longitude;
          dropOffFLightPath.fromLatitude = node.getLongLat().latitude;
          dropOffFLightPath.fromLongitude = node.getLongLat().longitude;
          dropOffFLightPath.orderNo = drone.getCurrentFoodOrder().getOrderNo();

          drone.hoverDrone();
          drone.unloadItems();
          drone.flyDrone();

          flightPaths.add(dropOffFLightPath);
          deliveries.add(dropOffDelivery);
          //System.out.println("dropped order" + "   " + node);
          break;
        case HOME:
          drone.returnToHome();
          //System.out.println("got home safe      " + node);
          break;
        default:
          if (!drone.getCurrentPosition().equals(node.getLongLat())) {

            DatabaseIO.FlightPath moveFlightPath = new DatabaseIO.FlightPath();
            moveFlightPath.angle =
                (int) Math.round(drone.getCurrentPosition().calculateBearing(node.getLongLat()));
            moveFlightPath.toLatitude = node.getLongLat().latitude;
            moveFlightPath.toLongitude = node.getLongLat().longitude;
            moveFlightPath.fromLatitude = drone.getCurrentPosition().latitude;
            moveFlightPath.fromLongitude = drone.getCurrentPosition().longitude;
            moveFlightPath.orderNo = drone.getCurrentFoodOrder().getOrderNo();

            flightPaths.add(moveFlightPath);
            //System.out.println("ordinary move   " + node + "noted angle: " + moveFlightPath.angle + "       actual angle: " + drone.getCurrentPosition().calculateBearing(node.getLongLat()));
            drone.moveTo(node.getLongLat());
          }
      }
    }
  }

  public void setLastNodeUsage(List<Node> nodes, Node.NodeUsage usage) {
    Node lastNode = null;
    try {
      lastNode = (Node) nodes.get(nodes.size() - 1).clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    lastNode.setNodeUsage(usage);
    nodes.add(lastNode);
  }

  public void generatePathMap() {
    FeatureCollection featureCollection =
        GeoJsonManager.createFeatureCollection(convertPathsToGeojsonFeatures());
    String filePrefix = "src/main/java/uk/ac/ed/inf/";
    try {
      FileWriter myWriter = new FileWriter(filePrefix + "geojson-" + deliveryDate + ".geojson");
      myWriter.write(featureCollection.toJson());
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public List<Feature> convertPathsToGeojsonFeatures() {
    List<Feature> features = new ArrayList<>();
    features.addAll(GeoJsonManager.getRestrictedAreasFeatures());
    features.addAll(
        GeoJsonManager.generatePointsFromNodes(graph.getAllNodes()).stream()
            .map(x -> Feature.fromGeometry(x))
            .collect(toList()));
    int counter = 0;
    for (FoodOrder foodOrder : deliverableOrders) {
      Point deliveryPoint =
          GeoJsonManager.createPointFromLongLat(foodOrder.getDeliveryLocationLongLat());
      LineString lineString =
          LineString.fromLngLats(
              GeoJsonManager.generatePointsFromNodes(deliveryPaths.get(foodOrder)));
      if (counter != 5) {
        features.add(Feature.fromGeometry(deliveryPoint));
        features.add(Feature.fromGeometry(lineString));
        //graph.printDistanceBetweenNodes(deliveryPaths.get(foodOrder));
      }
      totalOrderValue += foodOrder.getDeliveryCost();
      counter += 1;

      // System.out.println("Delivery no: " +counter+"/"+ deliverableOrders.size()+"\nMonetary
      // value: " +
      //        100*totalOrderValue/orderDeliveryWorker.getTotalOrderValue());
    }
    // Add final path to home and home marker.
    // Point homeMarker =
    // GeoJsonManager.createPointFromLongLat(Settings.getDefaultHomeLocation());
    LineString lineStringToHome =
        LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(pathToHome));
    // features.add(Feature.fromGeometry((Geometry) lineStringToHome));
    // features.add(Feature.fromGeometry((Geometry) homeMarker));
    Point homeMarker = GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition());
    // Point test =
    // GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition().nextPosition(10));
    // LineString ls = LineString.fromLngLats(List.of(homeMarker, test));
    // features.add(Feature.fromGeometry((Geometry) ls));
    // features.add(Feature.fromGeometry((Geometry) test));
    // features.add(Feature.fromGeometry((Geometry) homeMarker));
    return features;
  }

  public LinkedList<FoodOrder> getDeliverableOrders() {
    return deliverableOrders;
  }

  public HashMap<FoodOrder, List<Node>> getDeliveryPaths() {
    return deliveryPaths;
  }

  public List<DatabaseIO.Delivery> getDeliveries() {
    return deliveries;
  }

  //TODO remove can, can be transient.
  public HashMap<LongLat, LongLat> getMapping() {
    return graph.getNodeLonglatToTargetLonglat();
  }

  public List<DatabaseIO.FlightPath> getFlightPaths() {
    return flightPaths;
  }
  public List<Node> getAPath() {
    List<Node> paths = new ArrayList<>();
    int counter = 0;
    for(FoodOrder f : deliverableOrders) {
      paths.addAll(deliveryPaths.get(f));
      if(counter == -2) {
        break;
      }
      counter += 1;
    }
    paths.addAll(pathToHome);
    //return deliveryPaths.get(deliverableOrders.get(5));
    return paths;
  }
}
