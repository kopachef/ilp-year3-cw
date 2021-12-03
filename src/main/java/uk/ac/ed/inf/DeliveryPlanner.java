package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;
import uk.ac.ed.inf.algorithm.PathSmoothing;
import uk.ac.ed.inf.dataio.DatabaseIO;
import uk.ac.ed.inf.dataio.GeoJsonManager;
import uk.ac.ed.inf.utils.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class DeliveryPlanner {

  private static final Object lock = new Object();
  /**
   * Delivery Planner manages drones delivery by keeping track of the following:
   * <li>Creates an instance of the OrderDeliveryWorker which is used to get FoodOrders.
   * <li>Checking if the drone can fulfill and order before deploying it(including checking that it
   *     will have enough battery capacity to get back home)
   * <li>Updating the FoodOrder Queue based on the drone's location.
   * <li>Generating the delivery path as json output file.
   * <li>Keeping a hashmap of the partial delivery path to avoid recalculation.
   * <li>Total order value and count delivered. This also provides further functionality associated
   *     with generating an output file for the path LineString.
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
  private final PathSmoothing pathSmoothing;
  private final Graph graph =
      new Graph(
          Settings.getDefaultNorthWestBound().longitude,
          Settings.getDefaultNorthWestBound().latitude,
          Settings.getDefaultSouthEastBound().longitude,
          Settings.getDefaultSouthEastBound().latitude,
          Settings.getGridGranularity());
  private List<Node> pathToHome = new LinkedList<>();
  private List<Node> smoothedPath;
  private int fulfilledOrderCount = 0;
  private double fulfilledOrderValue = 0;

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

  /**
   * This method creates a sequence of moves for a drone, flying it from its current location to the
   * end of the smoothedPath. This results in the creation of list of DatabaseIO.FlightPath and
   * DatabaseIO.Delivery objects which can be added to our database.
   */
  private void createDatabaseEntries() {
    drone.flyDrone();
    drone.resetBatterLevel();
    executeMoves(drone, smoothedPath);
  }

  /**
   * This method updates the list of food orders that can be delivered by the drone. The method uses
   * a while loop to keep checking if there are any orders that can be delivered and if the order
   * delivery worker's food order queue size is not 0. If so, the following is done:
   *
   * - The order delivery worker updates the food orders using the drone.
   * - A list of pickup locations is created from the food order's getPickUpLocations() method.
   * - A path is created from the start location to the first pickup location using the graph.getShortestPath() method.
   * - setLastNodeUsage() is called for the path with a Node.NodeUsage.PICKUP value.
   * - The end of the previous path is checked to see if it is the start of the current path. If it is, the end node is
   *   removed.
   * - The path is added to the pathToClient list.
   * - A path is created from the first pickup location to the final station using the graph.getShortestPath() method.
   * - setLastNodeUsage() is called for the path with a Node.NodeUsage.DROPOFF value.
   * - The start node is removed to avoid duplication.
   * - The path is added to the pathToFinalStation list.
   * - The pathFromClientToHome path is created using the graph.getShortestPath() method.
   * - The estimated travel distance and battery usage is calculated.
   * - If the battery usage will not be enough to complete the order and return home, the order is not delivered.
   * - The fullPath is created by adding the pathToClient and pathToFinalStation lists.
   * - The fulfilledOrderValue and fulfilledOrderCount are incremented.
   */
  private void updateFoodDeliveryList() {
    boolean canDeliver = true;
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

        // end location of the previous path is the start of the current path. If we directly add the new path to the
        // final path, we will be reduplicating the end node. We check for its existence and remove it if it exists.
        if (!pathToClient.isEmpty()
            && pathToClient.get(pathToClient.size() - 1).equals(path.get(0))) {
          path.remove(0);
        }
        pathToClient.addAll(path);
        startLocation = longlat;
        Node placeHolderNode = new Node(0, 0);
        placeHolderNode.setLongLat(longlat);

        //Keep a record of Nodes associated with our pickUp Locations.
        if (!pickupNodes.contains(placeHolderNode)) {
          pickupNodes.add(placeHolderNode);
        }
      }
      List<Node> pathToFinalStation = graph.getShortestPath(startLocation, deliveryLongLat);
      setLastNodeUsage(pathToFinalStation, Node.NodeUsage.DROPOFF);

      //Just like above, we remove the start node to avoid reduplication.
      pathToFinalStation.remove(0);
      pathToClient.addAll(pathToFinalStation);
      List<Node> pathFromClientToHome =
          graph.getShortestPath(deliveryLongLat, Settings.getDefaultHomeLocation());

      //Estimate the travel distance and battery usage to complete this delivery and get back home.
      double travelDistance =
          graph.distanceBetweenNodes(pathToClient)
              + graph.distanceBetweenNodes(pathFromClientToHome);
      int estimatedBatteryUsage = drone.calculateMovementStepCost(travelDistance);

      // if  battery won't be enough to complete this order and get back home then we don't deliver it.
      if (drone.getBatteryLevel() - estimatedBatteryUsage > 0) {
        drone.setCurrentFoodOrder(foodOrder);

        //Just like before, remove start node to avoid duplication.
        pathToClient.remove(0);
        drone.setCurrentPosition(pathToClient.get(pathToClient.size() - 1).getLongLat());
        fullPath.addAll(pathToClient);
        deliverableOrders.add(foodOrder);
        deliveryPaths.put(foodOrder, pathToClient);
        pathToHome = pathFromClientToHome;
        fulfilledOrderValue += foodOrder.getDeliveryCost();
        fulfilledOrderCount += 1;
      } else {
        canDeliver = false;
      }
    }
    setLastNodeUsage(pathToHome, Node.NodeUsage.HOME);
    pathToHome.remove(0);
    fullPath.addAll(pathToHome);
    smoothedPath = pathSmoothing.smoothenPath(getFullPath());
    createDatabaseEntries();
  }

  /**
   * The executeMoves() method synchronizes access to the deliverableOrders class variable, which is used to store
   * orders that are being delivered. It then sets the current target order and drone location, and for each node in
   * the path, determines the appropriate action based on the node's usage. If the node is a pickup, the drone will
   * hover, load the items, and fly to the next node. If the node is a dropoff, the drone will hover, unload the items,
   * and fly to the next node. If the node is the home, the drone will return to the home position. Further this,
   * method creates the list of moves and deliveries database entries.
   *
   * @param drone drone object
   * @param pathNodes path nodes
   */
  private void executeMoves(Drone drone, List<Node> pathNodes) {
    synchronized (lock) {
      //deliverable orders are permanently changed during this operation hence we clone to preserve orders in the
      // deliverableOrders class variable.
      @SuppressWarnings("unchecked")
      LinkedList<FoodOrder> foodOrderList = (LinkedList<FoodOrder>) deliverableOrders.clone();

      //Set the current target order and drone location.
      FoodOrder foodOrder = foodOrderList.poll();
      drone.setCurrentFoodOrder(foodOrder);
      drone.setCurrentPosition(pathNodes.get(0).getLongLat());

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

  /**
   * This method sets the last node usage on a list of nodes. The last node is the node at the end of the list.
   * The usage is the Node.NodeUsage object that specifies the usage information for the last node.
   *
   * @param nodes list of nodes to be modified.
   * @param usage usage to set the node to.
   */
  private void setLastNodeUsage(List<Node> nodes, Node.NodeUsage usage) {
    Node lastNode = null;
    try {
      lastNode = nodes.get(nodes.size() - 1).clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    Objects.requireNonNull(lastNode).setNodeUsage(usage);
    nodes.add(lastNode);
  }

  /**
   * Writes the FeatureCollection to a file.
   *
   */
  public void generatePathMap() {
    FeatureCollection featureCollection = FeatureCollection.fromFeature(generatePathLineString());
    String filePrefix = "src/main/java/uk/ac/ed/inf/";
    String[] dateParts = deliveryDate.toString().split("-");
    String dateString = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
    try {
      FileWriter myWriter = new FileWriter(filePrefix + "drone-" + dateString + ".geojson");
      myWriter.write(featureCollection.toJson());
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Gets a list of features that includes restricted areas and grid nodes. Can be used when want to incorporate
   * there features with a path.
   *
   * @return list of features
   */
  public List<Feature> createFeatureListOfNodesAndRestrictedAreas() {
    List<Feature> features = new ArrayList<>();
    features.addAll(GeoJsonManager.getRestrictedAreasFeatures());
    features.addAll(
        GeoJsonManager.generatePointsFromNodes(graph.getAllNodes()).stream()
            .map(Feature::fromGeometry)
            .collect(toList()));
    return features;
  }

  /**
   * The generatePathLineString() method constructs a Feature from a LineString created from a list of LongLats.
   *
   * @return feature
   */
  private Feature generatePathLineString() {
    List<LongLat> visitedLongLats = new ArrayList<>();
    visitedLongLats.add(
        new LongLat(flightPaths.get(0).fromLongitude, flightPaths.get(0).fromLatitude));

    for (DatabaseIO.FlightPath flightPath : flightPaths) {
      visitedLongLats.add(new LongLat(flightPath.toLongitude, flightPath.toLatitude));
    }
    return Feature.fromGeometry(
        LineString.fromLngLats(
            visitedLongLats.stream()
                .map(GeoJsonManager::createPointFromLongLat)
                .collect(toList())));
  }

  /**
   * This method returns a list of all the food orders that are currently deliverable.
   *
   * @return food orders
   */
  public LinkedList<FoodOrder> getDeliverableOrders() {
    return deliverableOrders;
  }

  /**
   * The getDeliveryPaths() method returns a HashMap<FoodOrder, List<Node>> object. The key in the HashMap is a
   * FoodOrder object and the value is a List<Node> object. The List<Node> object contains the nodes in the delivery
   * path for the specified FoodOrder.
   *
   * @return path mapping
   */
  public HashMap<FoodOrder, List<Node>> getDeliveryPaths() {
    return deliveryPaths;
  }

  /**
   * This method returns a list of DatabaseIO.Delivery objects.
   *
   * @return database deliveries
   */
  public LinkedList<DatabaseIO.Delivery> getDeliveries() {
    return deliveries;
  }

  /**
   * This method returns a list of FlightPaths.
   *
   * @return database flight paths
   */
  public LinkedList<DatabaseIO.FlightPath> getFlightPaths() {
    return flightPaths;
  }

  /**
   * This method returns the full delivery path.
   *
   * @return full path
   */
  public List<Node> getFullPath() {
    return fullPath;
  }

  /**
   * This method returns a HashMap that maps LongLat coordinates to LongLat coordinates.
   *
   * @return Node Longlat to original target Longlat.
   * @see uk.ac.ed.inf.algorithm.Graph;
   */
  public HashMap<LongLat, LongLat> getMapping() {
    return graph.getNodeLonglatToTargetLonglat();
  }

  /**
   * The generateDeliveryReport() method prints information about the delivery date, total product value delivered,
   * total product count ordered, fulfilled order value, fulfilled order count, percentage of orders delivered, and
   * fulfilled order ratio. Additionally, the method prints the full delivery path size and smoothed path size.
   */
  public void generateDeliveryReport() {
    System.out.println(
        "\nDelivery date: "
            + deliveryDate
            + "\nTotal product value delivered: "
            + totalOrderValue
            + "\nTotal product count ordered: "
            + totalOrderCount
            + "\nFulfilled order value: "
            + fulfilledOrderValue
            + "\nFulfilled Order Count: "
            + fulfilledOrderCount
            + "\nPercentage of orders delivered: "
            + (fulfilledOrderValue / totalOrderValue) * 100
            + "\nFulfilled order ratio: "
            + fulfilledOrderCount
            + "/"
            + totalOrderCount
            + "\n full delivery path size: "
            + fullPath.size()
            + "\n smoothed path size: "
            + smoothedPath.size());
  }
}
