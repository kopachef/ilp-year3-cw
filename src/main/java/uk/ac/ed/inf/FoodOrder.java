package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodOrder {

    private List<MenuItem> orderItems;
    private List<LongLat> deliveryPath;
    private String customer;
    private Date deliveryDate;
    private String deliveryW3wAddress;
    private String orderNo;
    private double deliveryCost;
    private boolean hasBeenDelivered;
    private LongLat deliveryLocationLongLat;

    public FoodOrder(List<MenuItem> items,
                     String orderNo,
                     String customer,
                     Date deliveryDate,
                     LongLat deliveryLocationLongLat,
                     String deliveryW3wAddress,
                     double deliveryCost) {
        this.orderItems = items;
        this.customer = customer;
        this.deliveryDate = deliveryDate;
        this.deliveryW3wAddress = deliveryW3wAddress;
        this.orderNo = orderNo;
        this.deliveryCost = deliveryCost;
        this.hasBeenDelivered = false;
        this.deliveryLocationLongLat = deliveryLocationLongLat;
        this.deliveryPath = new ArrayList<>();
    }

    private List<LongLat> generateDeliveryPath(List<MenuItem> menuItems) {
        List<LongLat> result = new ArrayList<>();
        for(MenuItem item : menuItems) {
            result.add(
                    JsonObjectManager.coordToLonglat(
                            JsonObjectManager.parseW3WObject(item.getLocation()).coordinates));
        }
        return result;
    }

    public List<MenuItem> getOrderItems() {
        return orderItems;
    }

    public double calculateTravelDistance(LongLat startLocation) {
        double totalDistance = 0;
        LongLat currentLocation = startLocation;
        for(MenuItem item: this.orderItems) {
            LongLat destination = JsonObjectManager.coordToLonglat(
                    JsonObjectManager.parseW3WObject(item.getLocation()).coordinates);
            totalDistance += startLocation.distanceTo(destination);
            currentLocation = destination;
        }
    return totalDistance += currentLocation.distanceTo(this.getDeliveryLocationLongLat());
    }

    public List<LongLat> getPickUpLocations() {
        return getOrderItems().stream().map(x -> JsonObjectManager.coordToLonglat(
                JsonObjectManager.parseW3WObject(x.getLocation()).coordinates)).collect(Collectors.toList());
    }

    public String getCustomer() {
        return customer;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliveryW3wAddress() {
        return deliveryW3wAddress;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public boolean isHasBeenDelivered() {
        return hasBeenDelivered;
    }

    public LongLat getDeliveryLocationLongLat() {
        return deliveryLocationLongLat;
    }
}
