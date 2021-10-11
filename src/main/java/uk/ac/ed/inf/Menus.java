package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menus {
  /**
   * This class provides the core functionality of the system and aggregates the functionality
   * provided by a majority of the other predefined classes.
   *
   * <h1>Functions</h1>
   * <p>
   * <li>Core function of this class include unpacking json into content in separate object classes
   * <li> * Estimating delivery cost of an order
   * <li> * Parsing W3W(What Three Words) addresses into a java object
   * </p>
   *
   */

  private static final HashMap<String, MenuItem> menuItemHashMap = new HashMap<>();
  private static final GsonBuilder gsonBuilder = new GsonBuilder();
  private static final Gson gson = gsonBuilder.create();

  private static ArrayList<GeneratedJsonObjects.Menu> menus;

  public Menus(String host, String port) {
    Settings.setDefaultHost(host);
    Settings.setDefaultPort(port);
    String jsonMenuListUrl =
            Settings.getDefaultUrlPrefix()
            + host
            + ":"
            + port
            + Settings.getDefaultMenusAddressUrl();
    Type restaurantMenuTypes =
            new TypeToken<ArrayList<GeneratedJsonObjects.Menu>>() {}.getType();
    menus = gson.fromJson(UrlDownLoader.loadUrlContents(jsonMenuListUrl), restaurantMenuTypes);
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
   * Updates our item lookup cache by iterating through json-unpacked menu items ArrayList and adding or
   * updating the items in our item hashmap with their respective MenuItem objects.
   */
  private void reloadMenuCache() {
    for (GeneratedJsonObjects.Menu menu : menus) {
      for (GeneratedJsonObjects.Menu.Item menuItem : menu.menu) {

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
   * Parses the contents of reading the given url, which points to a 'details.json' file and loads
   * its contents into a predefined W3WObject static class.
   *
   * Example of a W3W(What Three Words) formatted location is: "butter.climb.talk".
   * @param locations String representing the location formatted as a W3W address String.
   * @return W3WObject created from the reading the contents of the W3W address.
   */
  public static GeneratedJsonObjects.W3WObject parseW3WObject(String locations) {
    String addressUrl =
        Settings.getDefaultUrlPrefix()
            + Settings.getDefaultHost()
            + ":"
            + Settings.getDefaultPort()
            + Settings.getDefaultW3wContentRootDirectory()
            + locations.replace(".", "/")
            + "/"
            + Settings.getDefaultW3wContentFilename();
    return gson.fromJson(UrlDownLoader.loadUrlContents(addressUrl), GeneratedJsonObjects.W3WObject.class);
  }
}
