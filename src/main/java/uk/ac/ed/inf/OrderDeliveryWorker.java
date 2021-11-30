package uk.ac.ed.inf;

import uk.ac.ed.inf.dataio.DatabaseIO;
import uk.ac.ed.inf.dataio.DatabaseIO.DatabaseOrder;
import uk.ac.ed.inf.dataio.DatabaseIO.DatabaseOrderDetails;
import uk.ac.ed.inf.dataio.JsonObjectManager;
import uk.ac.ed.inf.utils.Settings;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class OrderDeliveryWorker {

  /**
   * OrderDeliveryWorker objects manages essential food Queue ordering functionality outlined below.
   * - Reads food orders for a specified day and loads them into a priority queue - Provides
   * overridden priority queue that orders our food orders based on the ratio of delivery distance
   * and delivery cost hence allowing us maximum the value of orders delivered. - Allows us to
   * update food delivery priority queue based on a custom provided LongLat object.
   */
  private final Drone drone;

  private final Date orderProcessingDate;
  private PriorityQueue<FoodOrder> foodOrderQueue;
  private double totalOrderValue = 0;

  public OrderDeliveryWorker(Drone droneObject, Date orderProcessingDate) {
    this.orderProcessingDate = orderProcessingDate;
    this.drone = droneObject;
    this.foodOrderQueue =
        new PriorityQueue<FoodOrder>(
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

  /**
   * Reads the data from our database and extracts orders matching the provided date. It then
   * creates FoodOrders objects with these details which are then added to our priority queue.
   */
  public void populateFoodOrders() {
    List<DatabaseOrder> dbOrders = DatabaseIO.queryOrders("", this.orderProcessingDate, "", "");
    List<FoodOrder> foodOrders = new ArrayList<>();
    Menus menus = new Menus(Settings.getDefaultServerHost(), Settings.getDefaultServerPort());
    if (!dbOrders.isEmpty()) {
      for (DatabaseOrder dbOrder : dbOrders) {
        List<MenuItem> items = new ArrayList<>();
        List<DatabaseOrderDetails> dbOrderDetails =
            DatabaseIO.queryOrderDetails(dbOrder.orderNo, "");
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
        totalOrderValue += currentFoodOrder.getDeliveryCost();
        foodOrders.add(currentFoodOrder);
      }
      this.foodOrderQueue.clear();
      this.foodOrderQueue.addAll(foodOrders);
    }
  }

  /**
   * Updates the order of food items in our queue based on the current location of the drone. As
   * noted above, it to prioritise orders that have the highest value per distance travelled.
   *
   * @param drone drone object
   * @param currentFoodOrderQueue current priority queue to be reordered.
   */
  public void updateFoodOrders(Drone drone, PriorityQueue<FoodOrder> currentFoodOrderQueue) {
    this.foodOrderQueue =
        new PriorityQueue<FoodOrder>(
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

  /**
   * Returns the food order Priority Queue.
   *
   * @return returns the priority queue containing our food orders.
   */
  public PriorityQueue<FoodOrder> getFoodOrderQueue() {
    return foodOrderQueue;
  }

  /**
   * Returns the size of the food order queue.
   *
   * @return int value representing the current size of the food order queue.
   */
  public int getFoodOrderQueueSize() {
    return this.foodOrderQueue.size();
  }

  /**
   * Pulls a food order out of priority and queue returns this. As this uses .poll(), takes not of
   * the fact that modifies the state of the food order queue.
   *
   * @return FoodOrder object
   */
  public FoodOrder getFoodOrder() {
    return this.foodOrderQueue.poll();
  }

  /**
   * Return total cost of orders
   *
   * @return
   */
  public double getTotalOrderValue() {
    return totalOrderValue;
  }
}
