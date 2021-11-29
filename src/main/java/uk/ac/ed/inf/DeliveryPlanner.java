package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;
import uk.ac.ed.inf.algorithm.PathSmoothing;

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
  private final List<Node> fullPath = new ArrayList<>();
  private final Drone drone = new Drone(1500);
  private final OrderDeliveryWorker orderDeliveryWorker;
  private final LinkedList<DatabaseIO.Delivery> deliveries = new LinkedList<>();
  private final LinkedList<DatabaseIO.FlightPath> flightPaths = new LinkedList<>();
  private final List<Node> pickupNodes = new ArrayList<>();
  private final double totalOrderValue;
  private final int totalOrderCount;

  private List<Node> pathToHome = new LinkedList<>();
  private List<Node> smoothedPath;
  private PathSmoothing pathSmoothing;

  private static final Object lock = new Object();

  private int fullfiledOrderCount = 0;
  private double fulfilledOrderValue = 0;
  private final Graph graph =
      new Graph(
          Settings.getDefaultNorthWestBound().longitude,
          Settings.getDefaultNorthWestBound().latitude,
          Settings.getDefaultSouthEastBound().longitude,
          Settings.getDefaultSouthEastBound().latitude,
          Settings.GRID_GRANULARITY);

  public DeliveryPlanner(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
    this.orderDeliveryWorker = new OrderDeliveryWorker(drone, deliveryDate);
    this.orderDeliveryWorker.populateFoodOrders();
    totalOrderCount = orderDeliveryWorker.getFoodOrderQueueSize();
    totalOrderValue = orderDeliveryWorker.getTotalOrderValue();
    drone.setCurrentPosition(graph.findNearestNode(drone.getCurrentPosition()).getLongLat());
    pathSmoothing = new PathSmoothing(getMapping(), Settings.getDefaultPathSmoothingThreshold());
    updateFoodDeliveryList();
  }

  public void createDatabaseEntries() {
    drone.setCurrentPosition(fullPath.get(0).getLongLat());
    drone.flyDrone();
    drone.resetBatterLevel();
    executeMoves(drone, smoothedPath);
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
      pathToFinalStation.remove(0);
      pathToClient.addAll(pathToFinalStation);
      List<Node> pathFromClientToHome =
          graph.getShortestPath(deliveryLongLat, Settings.getDefaultHomeLocation());
      double travelDistance =
          graph.distanceBetweenNodes(pathToClient)
              + graph.distanceBetweenNodes(pathFromClientToHome);
      int estimatedBatteryUsage = drone.calculateMovementStepCost(travelDistance);
      if (drone.getBatteryLevel() - estimatedBatteryUsage > 0) {
        drone.setCurrentFoodOrder(foodOrder);

        // TODO replaced execute with add to path. execution at the end.
        // executeMoves(drone, pathToClient);
        pathToClient.remove(0);
        drone.setCurrentPosition(pathToClient.get(pathToClient.size()-1).getLongLat());
        fullPath.addAll(pathToClient);
        deliverableOrders.add(foodOrder);
        deliveryPaths.put(foodOrder, pathToClient);
        pathToHome = pathFromClientToHome;
        fulfilledOrderValue += foodOrder.getDeliveryCost();
        fullfiledOrderCount += 1;
      } else {
        canDeliver = false;
      }
    }
    setLastNodeUsage(pathToHome, Node.NodeUsage.HOME);

    //TODO replaced execute with add to path.
    //executeMoves(drone, pathToHome);
    pathToHome.remove(0);
    fullPath.addAll(pathToHome);
    smoothedPath = pathSmoothing.smoothenPath(getFullPath());
    createDatabaseEntries();
  }

  public void executeMoves(Drone drone, List<Node> pathNodes) {
    synchronized (lock) {
      LinkedList<FoodOrder> foodOrderList = (LinkedList<FoodOrder>) deliverableOrders.clone();
      for(FoodOrder f: foodOrderList) {
        System.out.println("delivery loc: " + f.getDeliveryW3wAddress());
      }
      FoodOrder foodOrder = foodOrderList.poll();
      drone.setCurrentFoodOrder(foodOrder);
      for (Node node : pathNodes) {
        switch (node.getUsage()) {
          case PICKUP:
            drone.hoverDrone();
            drone.loadItems();
            drone.flyDrone();

            DatabaseIO.FlightPath pickUpFlightPath = new DatabaseIO.FlightPath();
            pickUpFlightPath.angle = Settings.getDefaultHoverAngle();
            pickUpFlightPath.toLatitude = node.getLongLat().latitude;
            pickUpFlightPath.toLongitude = node.getLongLat().longitude;
            pickUpFlightPath.fromLatitude = node.getLongLat().latitude;
            pickUpFlightPath.fromLongitude = node.getLongLat().longitude;
            pickUpFlightPath.orderNo = drone.getCurrentFoodOrder().getOrderNo();



            flightPaths.add(pickUpFlightPath);
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

            foodOrder = foodOrderList.peek() == null ? foodOrder : foodOrderList.poll();
            drone.setCurrentFoodOrder(foodOrder);

            break;
          case HOME:
            drone.returnToHome();
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
              drone.moveTo(node.getLongLat());
            }
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
    FeatureCollection featureCollection = FeatureCollection.fromFeature(generatePathLineString());
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
    return features;
  }

  public Feature generatePathLineString() {
    Feature feature = Feature.fromGeometry(LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(smoothedPath)));
    return feature;
  }

  public LinkedList<FoodOrder> getDeliverableOrders() {
    return deliverableOrders;
  }

  public HashMap<FoodOrder, List<Node>> getDeliveryPaths() {
    return deliveryPaths;
  }

  public LinkedList<DatabaseIO.Delivery> getDeliveries() {
    return deliveries;
  }

  public LinkedList<DatabaseIO.FlightPath> getFlightPaths() {
    return flightPaths;
  }
  public List<Node> getFullPath() {
    return fullPath;
  }

  public HashMap<LongLat, LongLat> getMapping() {
    return graph.getNodeLonglatToTargetLonglat();
  }

  public void generateDeliveryReport() {
    System.out.println(
            "\nDelivery date: " + deliveryDate +
                    "\nTotal product value delivered: " + totalOrderValue +
            "\nTotal product count ordered: " + totalOrderCount +
            "\nFulfilled order value: " + fulfilledOrderValue +
            "\nFulfilled Order Count: " + fullfiledOrderCount +
            "\nPercentage of orders delivered: " + (fulfilledOrderValue/totalOrderValue) * 100 +
            "\nFulfiled order ratio: " + fullfiledOrderCount + "/" + totalOrderCount +
    "\n full delivery path size: " + fullPath.size() +
    "\n smoothed path size: " + smoothedPath.size());
  }
}
