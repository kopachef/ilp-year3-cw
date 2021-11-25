package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toList;

public class DeliveryPlanner {

    /**
     * Delivery Planner manages drones delivery by keeping track of the following:
     *
     *   - Creates an instance of the OrderDeliveryWorker which is used to get FoodOrders.
     *   - Checking if the drone can fulfill and order before deploying it(including checking that it will have
     *      enough battery capacity to get back home)
     *   - Updating the FoodOrder Queue based on the drone's location.
     *   - Generating the delivery path as json output file.
     *   - Keeping a hashmap of the partial delivery path to avoid recalculation.
     */

    private final Date deliveryDate;
    private LinkedList<FoodOrder> deliverableOrders = new LinkedList<>();
    private HashMap<FoodOrder, List<Node>> deliveryPaths = new HashMap<>();
    private Drone drone = new Drone(1500);
    private List<Node> pathToHome = new LinkedList<>();
    private OrderDeliveryWorker orderDeliveryWorker;
    private double totalOrderValue = 0;
    private int totalOrderCount;
    //TODO remove ony for graphing purposes
    List<Node> pickupNodes = new ArrayList<>();
    private Graph graph =
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
        updateFoodDeliveryList();
    }


    private void updateFoodDeliveryList() {
        boolean canDeliver = true;
        while(canDeliver && orderDeliveryWorker.getFoodOrderQueueSize() > 0) {
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
                if(!pathToClient.isEmpty() &&
                        pathToClient.get(pathToClient.size() - 1).equals(path.get(0))) {
                    path.remove(0);
                        pathToClient.addAll(path);
                } else {
                    pathToClient.addAll(path);
                }
                startLocation = longlat;
                Node placeHolderNode  = new Node(0,0);
                placeHolderNode.setLongLat(longlat);
                if(!pickupNodes.contains(placeHolderNode)) {
                    pickupNodes.add(placeHolderNode);
                }
            }
            List<Node> pathToFinalStation = graph.getShortestPath(startLocation, deliveryLongLat);
            setLastNodeUsage(pathToFinalStation, Node.NodeUsage.DROPOFF);
            pathToClient.addAll(pathToFinalStation);
            List<Node> pathFromClientToHome =
                    graph.getShortestPath(deliveryLongLat, Settings.getDefaultHomeLocation());


            //graph.printDistanceBetweenNodes(pathFromClientToHome);

            //TODO can get rid of this print statement
            //graph.printDistanceBetweenNodes(testPath);

            //TODO modify this to be more reflective of what the dor eisactually doiubg.
            double travelDistance =
                    graph.distanceBetweenNodes(pathToClient) +
                            graph.distanceBetweenNodes(pathFromClientToHome);
            int estimatedBatteryUsage = drone.calculateMovementStepCost(travelDistance);
            if(drone.getBatteryLevel() - estimatedBatteryUsage > 0) {
                executeMoves(drone, pathToClient);
                //drone.setCurrentPosition(pathToClient.get(pathToClient.size()-1).getLongLat());
                deliverableOrders.add(foodOrder);
                deliveryPaths.put(foodOrder, pathToClient);
                pathToHome = pathFromClientToHome;
                //System.out.println("can deliver drone loc: " + drone.getCurrentPosition().calculateBearing(pathToClient.get(pathToClient.size()-1).getLongLat()));

            } else {
                canDeliver = false;
                //System.out.println(drone.getCurrentPosition() + "state is: " + drone.getDroneState());
            }
            setLastNodeUsage(pathToHome, Node.NodeUsage.HOME);
            executeMoves(drone, pathToHome);

        }
    }
    public void executeMoves(Drone drone, List<Node> pathNodes){
        for(Node node: pathNodes) {
            switch(node.getUsage()) {
                case PICKUP:
                    drone.hoverDrone();
                    drone.loadItems();
                    drone.flyDrone();
                    System.out.println("picked order     "  + node);
                    break;
                case DROPOFF:
                    drone.hoverDrone();
                    drone.unloadItems();
                    drone.flyDrone();
                    System.out.println("dropped order" + "   " + node);
                    break;
                case HOME:
                    drone.returnToHome();
                    System.out.println("got home safe      " + node);
                    break;
                default:
                    if (!drone.getCurrentPosition().equals(node.getLongLat())) {
                        drone.moveTo(node.getLongLat());
                        System.out.println("ordinary move      " + node);
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
        FeatureCollection featureCollection = GeoJsonManager.createFeatureCollection(convertPathsToGeojsonFeatures());
        String filePrefix = "src/main/java/uk/ac/ed/inf/";
        try {
            FileWriter myWriter = new FileWriter(filePrefix + "geojson-" + String.valueOf(deliveryDate) + ".geojson");
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
                .map(x -> Feature.fromGeometry((Geometry) x))
                .collect(toList()));
        int counter = 0;
        for(FoodOrder foodOrder : deliverableOrders) {
            Point deliveryPoint = GeoJsonManager.createPointFromLongLat(foodOrder.getDeliveryLocationLongLat());
            LineString lineString =
                    LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(deliveryPaths.get(foodOrder)));
            if (counter != 5) {
                //System.out.println(foodOrder.getOrderItems() + "\n" + foodOrder.getDeliveryCost()+"\n" + foodOrder.getDeliveryW3wAddress() + "\n" + foodOrder.getDeliveryLocationLongLat());
                features.add(Feature.fromGeometry((Geometry) deliveryPoint));
                features.add(Feature.fromGeometry((Geometry) lineString));
                graph.printDistanceBetweenNodes(deliveryPaths.get(foodOrder));
            }
            totalOrderValue += foodOrder.getDeliveryCost();
            counter += 1;

            //System.out.println("Delivery no: " +counter+"/"+ deliverableOrders.size()+"\nMonetary value: " +
            //        100*totalOrderValue/orderDeliveryWorker.getTotalOrderValue());
        }
        //Add final path to home and home marker.
        //Point homeMarker = GeoJsonManager.createPointFromLongLat(Settings.getDefaultHomeLocation());
        LineString lineStringToHome = LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(pathToHome));
        //features.add(Feature.fromGeometry((Geometry) lineStringToHome));
        //features.add(Feature.fromGeometry((Geometry) homeMarker));
        Point homeMarker = GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition());
        //Point test = GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition().nextPosition(10));
        //LineString ls = LineString.fromLngLats(List.of(homeMarker, test));
        //features.add(Feature.fromGeometry((Geometry) ls));
        //features.add(Feature.fromGeometry((Geometry) test));
        //features.add(Feature.fromGeometry((Geometry) homeMarker));
        return features;
    }

    public LinkedList<FoodOrder> getDeliverableOrders() {
        return deliverableOrders;
    }

    public HashMap<FoodOrder, List<Node>> getDeliveryPaths() {
        return deliveryPaths;
    }
}
