package uk.ac.ed.inf;

import uk.ac.ed.inf.dataio.JsonObjectManager;

import java.util.Objects;

public class MenuItem {

  /**
   * This class provides a blueprint for the definition of a MenuItem object. This object is meant
   * to represent a physical item listed on a restaurant menu and has been abstracted in this way to
   * make it easier to lookup items on menus without having to deal with complexity that comes with
   * the fact that we have multiple different menus.
   *
   * <p>Further utility functions are provided within the class to enable easier operations on an
   * instances of this class</>
   */
  private final String name;

  private final String location;
  private final int price;
  private final String restaurantName;

  public MenuItem(String name, String location, int price, String restaurantName) {
    this.name = name;
    this.location = location;
    this.price = price;
    this.restaurantName = restaurantName;
  }

  /**
   * Returns the name of the MenuItem instance it is called on.
   *
   * <p>The name in this case is simply the name of the item from a restaurant menu as shown in the
   * example below e.g. "Strawberry macho latte".
   *
   * @return String representing the name attribute of the MenuItem object.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the W3W(What Three Words) location of the current instance of the MenuItem it is called
   * on.
   *
   * <p>The location attribute in this case represents a W3W location address as shown in the
   * example below. e.g. "sleep.time.collect".
   *
   * @return String representing W3W location address.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Return the value of the price attribute of the current MenuItem instance it is called on.
   *
   * @return double representing the price of attribute of the MenuItem object.
   */
  public int getPrice() {
    return price;
  }

  /**
   * Returns the restaurant name on the menu from which this item was obtained.
   *
   * @return String representing the name of the restaurant that listed this item on their menu.
   */
  public String getRestaurantName() {
    return restaurantName;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object
   *     <p>Returns true if given MenuItem object represents same attributes as current instance of
   *     MenuItem.
   * @param menuItem MenuItem object to compare against.
   * @return True if given instance represents the same set of attributes.
   */
  @Override
  public boolean equals(Object menuItem) {
    boolean result;
    if ((menuItem == null) || (getClass() != menuItem.getClass())) {
      result = false;
    } else {
      MenuItem inputMenuItem = (MenuItem) menuItem;
      result =
          name.equals(inputMenuItem.getName())
              && location.equals(inputMenuItem.getLocation())
              && price == inputMenuItem.getPrice()
              && restaurantName.equals(inputMenuItem.getRestaurantName());
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object
   *     <p>Returns a hash of the MenuItem object.
   * @return hash value of MenuItem object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, location, price, restaurantName);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object
   *     <p>Returns a String representation of a MenuItem object.
   * @return String representing the current MenuItem object.
   */
  @Override
  public String toString() {
    JsonObjectManager.W3WObject w3wObject = JsonObjectManager.parseW3WObject(location);
    return "Name: "
        + name
        + "\nLocation: "
        + location
        + "\nPrice: "
        + price
        + "\nRestaurant Name: "
        + restaurantName
        + "\n\nFormatted Location: "
        + "\n\tCountry: "
        + w3wObject.country
        + "\n\tNearest Place: "
        + w3wObject.nearestPlace
        + "\n\tWords: "
        + w3wObject.words
        + "\n\tLanguage: "
        + w3wObject.language
        + "\n\tMap: "
        + w3wObject.map
        + "\n\tSquare: "
        + "\n\t\tNortheast: longitude: "
        + w3wObject.square.northeast.lng
        + " latitude: "
        + w3wObject.square.northeast.lat
        + "\n\t\tSouthwest: longitude: "
        + w3wObject.square.southwest.lng
        + " latitude: "
        + w3wObject.square.southwest.lat
        + "\n\tCoords: "
        + "\n\t\tLongitude: "
        + w3wObject.coordinates.lng
        + "\n\t\tLatitude: "
        + w3wObject.coordinates.lat;
  }

  /** Dumps current state and attributes of the MenuItem object it is called on. */
  public void dump() {
    System.out.println(this);
  }
}
