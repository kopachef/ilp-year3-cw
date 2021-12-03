package uk.ac.ed.inf;

import uk.ac.ed.inf.dataio.JsonObjectManager;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodOrder {

  /** Class representing a single food order and all the attributes associated with it. */
  private final List<MenuItem> orderItems;

  private final String customer;
  private final Date deliveryDate;
  private final String deliveryW3wAddress;
  private final String orderNo;
  private final int deliveryCost;
  private final boolean hasBeenDelivered;
  private final LongLat deliveryLocationLongLat;

  public FoodOrder(
      List<MenuItem> items,
      String orderNo,
      String customer,
      Date deliveryDate,
      LongLat deliveryLocationLongLat,
      String deliveryW3wAddress,
      int deliveryCost) {
    this.orderItems = items;
    this.customer = customer;
    this.deliveryDate = deliveryDate;
    this.deliveryW3wAddress = deliveryW3wAddress;
    this.orderNo = orderNo;
    this.deliveryCost = deliveryCost;
    this.hasBeenDelivered = false;
    this.deliveryLocationLongLat = deliveryLocationLongLat;
    List<LongLat> deliveryPath = new ArrayList<>();
  }

  /**
   * A food order may contain multiple items. This function calculates the total travel distance of
   * picking up each item in our food order and delivering it to the customer.
   *
   * @param startLocation reference location which represents the current location of the drone.
   * @return returns the total distance travelled to deliver this order.
   */
  public double calculateTravelDistance(LongLat startLocation) {
    double totalDistance = 0;
    LongLat currentLocation = startLocation;
    for (MenuItem item : this.orderItems) {
      LongLat pickUpLocation =
          JsonObjectManager.coordToLonglat(
              JsonObjectManager.parseW3WObject(item.getLocation()).coordinates);
      totalDistance += startLocation.distanceTo(pickUpLocation);
      currentLocation = pickUpLocation;
    }
    return totalDistance + currentLocation.distanceTo(this.getDeliveryLocationLongLat());
  }

  /**
   * Returns a list of LongLat objects representing each pickup location for the items in order.
   *
   * @return List of LongLat objects.
   */
  public List<LongLat> getPickUpLocations() {
    return getOrderItems().stream()
        .map(
            x ->
                JsonObjectManager.coordToLonglat(
                    JsonObjectManager.parseW3WObject(x.getLocation()).coordinates))
        .collect(Collectors.toList());
  }

  /**
   * Returns all the MenuItems contained in this order.
   *
   * @return List of <MenuItem> objects.
   */
  public List<MenuItem> getOrderItems() {
    return orderItems;
  }

  /**
   * Returns the customer id for the customer associated with this order. In this case, the id is
   * just the students' student numbers pre-appended with an 's'.
   *
   * @return String representing the students' student number.
   */
  public String getCustomer() {
    return customer;
  }

  /**
   * Return the W3W(What 3 Words) formatted delivery address of this order.
   *
   * @return String representing what three words address of the delivery location.
   */
  public String getDeliveryW3wAddress() {
    return deliveryW3wAddress;
  }

  /**
   * Return the order number associated with this order.
   *
   * @return String representing the order number.
   */
  public String getOrderNo() {
    return orderNo;
  }

  /**
   * Returns the delivery cost associated with this order. This is essentially the total item value
   * of all the items in this order in addition to the delivery charge.
   *
   * @return double representing the total cost of this order.
   */
  public int getDeliveryCost() {
    return deliveryCost;
  }

  /**
   * Boolean representing whether an item has been delivered.
   *
   * @return True if the order has been delivered, fFalse otherwise.
   */
  public boolean isHasBeenDelivered() {
    return hasBeenDelivered;
  }

  /**
   * Returns a LongLat object representing the What Three Words delivery location of this order.
   *
   * @return LongLat object representing the delivery location.
   */
  public LongLat getDeliveryLocationLongLat() {
    return deliveryLocationLongLat;
  }
}
