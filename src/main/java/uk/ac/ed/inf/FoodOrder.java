package uk.ac.ed.inf;

import java.sql.Date;
import java.util.List;

public class FoodOrder {

    private List<MenuItem> orderItems;
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
    }

    public List<MenuItem> getOrderItems() {
        return orderItems;
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
