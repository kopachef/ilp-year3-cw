package uk.ac.ed.inf;

import uk.ac.ed.inf.DatabaseIO.DatabaseOrder;
import uk.ac.ed.inf.DatabaseIO.DatabaseOrderDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class OrderDeliveryWorker {
  /** priority queue to greeedily keep te order we have in check */
  private final Drone drone;

  public PriorityQueue<FoodOrder> getFoodOrderQueue() {
    return foodOrderQueue;
  }
  public Date getOrderProcessingDate() {
    return orderProcessingDate;
  }

  private PriorityQueue<FoodOrder> foodOrderQueue;
  private final Date orderProcessingDate;
  private final DatabaseIO databaseAccess;

  public OrderDeliveryWorker(Drone droneObject, Date orderProcessingDate) {
    this.orderProcessingDate = orderProcessingDate;
    this.drone = droneObject;
    this.databaseAccess =
        new DatabaseIO(Settings.getDefaultDatabaseHost(), Settings.getDefaultDatabasePort());
    this.foodOrderQueue = new PriorityQueue<FoodOrder>(
            new Comparator<FoodOrder>() {
              @Override
              public int compare(FoodOrder o2, FoodOrder o1) {
                double travelDistanceToO1 = o1.calculateTravelDistance(drone.getCurrentPosition());
                double travelDistanceToO2 = o2.calculateTravelDistance(drone.getCurrentPosition());
                return Double.compare(
                        (o1.getDeliveryCost() / travelDistanceToO1),
                        (o2.getDeliveryCost() / travelDistanceToO2));
              }
            });
  }

  public void populateFoodOrders() {
    List<DatabaseOrder> dbOrders = databaseAccess.queryOrders("", this.orderProcessingDate, "", "");
    List<FoodOrder> foodOrders = new ArrayList<>();
    Menus menus = new Menus(Settings.getDefaultServerHost(), Settings.getDefaultServerPort());
    if (!dbOrders.isEmpty()) {
      for (DatabaseOrder dbOrder : dbOrders) {
        List<MenuItem> items = new ArrayList<>();
        List<DatabaseOrderDetails> dbOrderDetails =
            databaseAccess.queryOrderDetails(dbOrder.orderNo, "");
        for (DatabaseOrderDetails databaseOrderDetails : dbOrderDetails) {
          String itemName = databaseOrderDetails.item;
          items.add(menus.getMenuItem(itemName));
        }
        LongLat deliveryLongLat =
            JsonObjectManager.coordToLonglat(
                JsonObjectManager.parseW3WObject(dbOrder.deliverTo).coordinates);
        FoodOrder currentFoodOrder =
            new FoodOrder(
                items,
                dbOrder.orderNo,
                dbOrder.customer,
                dbOrder.deliveryDate,
                deliveryLongLat,
                dbOrder.deliverTo,
                menus.getDeliveryCost(
                    items.stream()
                        .map(x -> x.getName())
                        .collect(Collectors.toList())
                        .toArray(String[]::new)));
        foodOrders.add(currentFoodOrder);
      }
      this.foodOrderQueue.clear();
      this.foodOrderQueue.addAll(foodOrders);
    }
  }
  public void updateFoodOrders(Drone drone, PriorityQueue<FoodOrder> currentFoodOrderQueue) {
    this.foodOrderQueue = new PriorityQueue<FoodOrder>(
            new Comparator<FoodOrder>() {
              @Override
              public int compare(FoodOrder o2, FoodOrder o1) {
                double travelDistanceToO1 = o1.calculateTravelDistance(drone.getCurrentPosition());
                double travelDistanceToO2 = o2.calculateTravelDistance(drone.getCurrentPosition());
                return Double.compare(
                        (o1.getDeliveryCost() / travelDistanceToO1),
                        (o2.getDeliveryCost() / travelDistanceToO2));
              }
            });
    this.foodOrderQueue.addAll(currentFoodOrderQueue);
  }

  public int getFoodOrderQueueSize() {
    return this.foodOrderQueue.size();
  }

  public FoodOrder getFoodOrder() {
    return this.foodOrderQueue.poll();
  }

}
