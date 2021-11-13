package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menus {
  /**
   * This class provides the main functionality of the system and associated with aggregating menu's and providing an
   * entry point to access different menu items and their associated attributes.
   *
   * <h1>Functions</h1>
   * <p>
   * <li> * Core function of this class include reading menu's from the server and looding them into the system.
   * <li> * Estimating delivery cost of an order
   * <li> *
   * </p>
   *
   */

  private static final HashMap<String, MenuItem> menuItemHashMap = new HashMap<>();
  private static ArrayList<JsonObjectManager.Menu> menus;

  public Menus(String host, String port) {
    Settings.setDefaultServerHost(host);
    Settings.setDefaultServerPort(port);
    menus = JsonObjectManager.parseMenuObject();
    reloadMenuCache();
  }

  /**
   * Looks up and return the cost of an item in pence.
   *
   * @param itemName String representing the exact name of an item.
   * @return int value representing the cost of the specified item in pence.
   */
  public int getItemPrice(String itemName) {
    return menuItemHashMap.get(itemName).getPrice();
  }

  /**
   * Looks up and returns a W3W(What Three Words) formatted location of a menu item.
   *
   * <p>A W3W formatted is defined by a period-separated trio of 3 words e.g. "cheese.potato.river"
   *
   * @param itemName String representing the exact name of the item whose name we are looking up.
   * @return String representing location formatted as a W3W address.
   */
  public String getItemLocation(String itemName) {
    return menuItemHashMap.get(itemName).getLocation();
  }

  /**
   * Returns an aggregated list of Strings representing the names of the entire catalogue of items
   * from all restaurants.
   *
   * @return Returns list of all the names of items from all the menus.
   */
  public List<String> getAvailableItems() {
    return new ArrayList<>(menuItemHashMap.keySet());
  }

  /**
   * Calculates and returns the cost of all the provided items in our String array with the delivery
   * cost included.
   *
   * @param items String array of items in our order.
   * @return int value representing the total cost of items including the delivery cost.
   */
  public int getDeliveryCost(String... items) {
    int cost = 0;
    for (String item : items) {
      cost += getItemPrice(item);
    }
    // we do not add the delivery cost if no items were provided.
    return cost == 0 ? 0 : cost + Settings.getDefaultStandardCharge();
  }

  /**
   * Utility function to look up MenuItem object just based on  the name.
   * @param name name of MenuItem to return
   * @return return MenuItem object bearing the specified name.
   */
  public MenuItem getMenuItem(String name) {
    return menuItemHashMap.get(name);
  }

  /**
   * Updates our item lookup cache by iterating through json-unpacked menu items ArrayList and adding or
   * updating the items in our item hashmap with their respective MenuItem objects created from the
   * ArrayList items.
   */
  private void reloadMenuCache() {
    for (JsonObjectManager.Menu menu : menus) {
      for (JsonObjectManager.Menu.Item menuItem : menu.menu) {

        String itemName = menuItem.item;
        String itemLocation = menu.location;
        int itemPrice = menuItem.pence;
        String restaurantName = menu.name;

        //Create MenuItem object with current attributes.
        MenuItem menuItemObject = new MenuItem(itemName, itemLocation, itemPrice, restaurantName);

        // Check if we already have the item in the hashmap or if the cached item is different.
        if ((menuItemHashMap.get(itemName)) == null || !menuItemHashMap.get(itemName).equals(menuItemObject)) {
          menuItemHashMap.put(itemName, menuItemObject);
        }
      }
    }
  }
}
