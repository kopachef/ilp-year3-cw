package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import uk.ac.ed.inf.graph.Graph;
import uk.ac.ed.inf.graph.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DeliveryPlanner {

    private final Date deliveryDate;
    private LinkedList<FoodOrder> deliverableOrders = new LinkedList<>();
    private HashMap<FoodOrder, List<Node>> deliveryPaths = new HashMap<>();
    private Drone drone = new Drone(1, 1500);
    private List<Node> pathToHome = new LinkedList<>();
    private OrderDeliveryWorker orderDeliveryWorker;
    private Graph graph = new Graph(
            Settings.getDefaultNorthwestBoundLongitude(),
            Settings.getDefaultNorthwestBoundLatitude(),
            Settings.getDefaultSoutheastBoundLongitude(),
            Settings.getDefaultSoutheastBoundLatitude(),
            Settings.GRID_GRANULARITY);

    public DeliveryPlanner(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
        this.orderDeliveryWorker = new OrderDeliveryWorker(drone, deliveryDate);
        this.orderDeliveryWorker.populateFoodOrders();
        updateFoodDeliveryList();
    }

    private void updateFoodDeliveryList() {
        boolean canDeliver = true;
        orderDeliveryWorker.updateFoodOrders(drone, orderDeliveryWorker.getFoodOrderQueue());
        while(canDeliver && orderDeliveryWorker.getFoodOrderQueueSize() > 0) {
            FoodOrder foodOrder = orderDeliveryWorker.getFoodOrder();
            List<LongLat> pickUpLocations = foodOrder.getPickUpLocations();
            List<Node> pathToClient = new ArrayList<>();
            LongLat startLocation = drone.getCurrentPosition();
            System.out.println("pickp locations");
            for (LongLat longlat : pickUpLocations) {
                pathToClient.addAll(graph.getShortestPath(startLocation, longlat));
                startLocation = longlat;
                System.out.println("reached a node!!!");
            }
            graph.printDistanceBetweenNodes(graph.smoothenPath(pathToHome));
            List<Node> pathFromClientToHome =
                    graph.getShortestPath(foodOrder.getDeliveryLocationLongLat(), Settings.getDefaultHomeLocation());
            double travelDistance = graph.distanceBetweenNodes(pathToClient) + graph.distanceBetweenNodes(pathFromClientToHome);
            int batteryUsage = drone.calculateMovementStepCost(travelDistance);
            if(drone.getBatteryLevel() - batteryUsage > 0) {
                drone.setCurrentPosition(foodOrder.getDeliveryLocationLongLat());
                drone.setBatteryLevel(drone.getBatteryLevel() - batteryUsage);
                drone.hoverDrone();
                System.out.println(drone.getBatteryLevel() + "\nHovered drone");
                drone.flyDrone();
                deliverableOrders.add(foodOrder);
                deliveryPaths.put(foodOrder, pathToClient);
                pathToHome = pathFromClientToHome;
            } else {
                canDeliver = false;
            }
        }
        drone.setBatteryLevel((int) (drone.getBatteryLevel() - graph.distanceBetweenNodes(pathToHome)));
    }

    public LinkedList<FoodOrder> getDeliverableOrders() {
        return deliverableOrders;
    }

    public HashMap<FoodOrder, List<Node>> getDeliveryPaths() {
        return deliveryPaths;
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
//        features.addAll(
//                GeoJsonManager.generatePointsFromNodes(graph.getAllNodes()).stream()
//                .map(x -> Feature.fromGeometry((Geometry) x))
//                .collect(toList()));
        int counter = 0;
        System.out.println(deliverableOrders.size());
        for(FoodOrder foodOrder : deliverableOrders) {
            Point deliveryPoint = GeoJsonManager.createPointFromLongLat(foodOrder.getDeliveryLocationLongLat());
            LineString lineString =
                    LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(deliveryPaths.get(foodOrder)));
            if (counter == 30) {
                System.out.println(foodOrder.getOrderItems() + "\n" + foodOrder.getDeliveryCost()+"\n" + foodOrder.getDeliveryW3wAddress() + "\n" + foodOrder.getDeliveryLocationLongLat());
                features.add(Feature.fromGeometry((Geometry) deliveryPoint));
                features.add(Feature.fromGeometry((Geometry) lineString));
            }
            counter += 1;
        }
        //Add final path to home and home marker.
        //Point homeMarker = GeoJsonManager.createPointFromLongLat(Settings.getDefaultHomeLocation());
        LineString lineStringToHome = LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(pathToHome));
        //features.add(Feature.fromGeometry((Geometry) lineStringToHome));
        //features.add(Feature.fromGeometry((Geometry) homeMarker));
        Point homeMarker = GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition());
        Point test = GeoJsonManager.createPointFromLongLat(drone.getCurrentPosition().nextPosition(10));
        LineString ls = LineString.fromLngLats(List.of(homeMarker, test));
        features.add(Feature.fromGeometry((Geometry) ls));
        features.add(Feature.fromGeometry((Geometry) test));
        features.add(Feature.fromGeometry((Geometry) homeMarker));
        return features;
    }
}
