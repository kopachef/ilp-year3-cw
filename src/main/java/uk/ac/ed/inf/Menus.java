package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

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
   * <p><li>Core function of this class include unpacking json into content in separate object classes</li>
   *    <li>Estimating delivery cost of of an order</li>
   *    <li>parsing W3W(What Three Words) addresses into a java object</li>
   * </p>
   *
   */

  public static ArrayList<Menu> menus;
  private final Settings settings;
  private static HashMap<String, MenuItem> menuItemHashMap;
  GsonBuilder gsonBuilder;
  Gson gson;
  Type restaurantMenuTypes;

  public Menus(String host, String port) {
    settings = new Settings();
    gsonBuilder = new GsonBuilder();
    gson = gsonBuilder.create();
    menuItemHashMap = new HashMap<>();
    restaurantMenuTypes = new TypeToken<ArrayList<Menu>>() {}.getType();
    String jsonMenuUrl = settings.getDefaultUrlPrefix() + host + ":" + port + settings.getDefaultMenusAddressUrl();
    menus = gson.fromJson(UrlDownLoader.loadUrlContents(jsonMenuUrl), restaurantMenuTypes);
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
   * <p> A W3W formatted is defined by a period-separated trio of 3 words e.g. "cheese.potato.river" </p>
   *
   * @param itemName String representing the exact name of the item whose name we are looking up.
   * @return        String representing location formatted as a W3W address.
   */
  public String getItemLocation(String itemName) {
    return menuItemHashMap.get(itemName).getLocation();
  }

  /**
   * Returns an aggregated list of Strings representing the names of the entire catalogue of items from all restaurants.
   * @return        Returns list of all the names of items from all the menus.
   */
  public List<String> getAvailableItems() {
    List<String> menuItems = new ArrayList<>(menuItemHashMap.keySet());
    return menuItems;
  }

  /**
   * Updates our item lookup cache by iterating through json-unpacked menu items and adding or updating the items
   * in our item hashmap with their respective MenuItem objects.
   */
  private void reloadMenuCache() {
    for (Menu menu : menus) {
      for (Menu.Item menuItem : menu.menu) {

        String itemName = menuItem.item;
        String itemLocation = menu.location;
        int itemPrice = menuItem.pence;
        String restaurantName = menu.name;

        // Check if we already have the item in the list
        if ((menuItemHashMap.get(itemName)) == null) {
          menuItemHashMap.put(itemName, new MenuItem(itemName, itemLocation, itemPrice, restaurantName));
        }
      }
    }
  }

  /**
   * Calculates and returns the cost of all the provided items in our String array with the delivery cost
   * included.
   *
   * @param items String array of items in our order.
   * @return int value representing the total cost of items including the delivery cost.
   */
  public int getDeliveryCost(String... items) {
    int cost = 0;
    for (String item : items) {
      cost += getItemPrice(item);
    }
    return cost == 0 ? 0 : cost + settings.getDefaultStandardCharge();
  }

  /**
   * Parses the contents of reading the given url, which points to a 'details.json' file and loads its contents into
   * a predefined W3WObject static class.
   *
   * @param localhost localhost address pointing to our server address.
   * @param port  port pointing to our server access port.
   * @param locations String representing the location formatted as a W3W address String.
   * @return          W3WObject created from the reading the contents of the W3W address.
   */
  public W3WObject parseW3WObject(String localhost, String port, @NotNull String locations) {
    String addressUrl = settings.getDefaultUrlPrefix() + localhost + ":" + port + settings.getDefaultW3wContentRootDirectory() +
            locations.replace(".","/") + "/" + settings.getDefaultW3wContentFilename();
    return gson.fromJson(UrlDownLoader.loadUrlContents(addressUrl), W3WObject.class);

  }

  /**
   * Static class acting a blueprint for a generic W3W details file containing details associated
   * with a specified W3W address.
   */
  static class W3WObject {
    String country;
    Square square;
    String nearestPlace;
    Coords coordinates;
    String words;
    String language;
    String map;

    class Square {
      Coords southwest;
      Coords northwest;
    }

    static class Coords {
      String lng;
      String lat;
    }
  }

  /**
   * Static class acting a blueprint for a generic 'menus.json' file containing a list of menus associated
   * with individual restaurants.
   */
  static class Menu {
    public String name;
    public String location;
    public ArrayList<Item> menu;

    static class Item {
      public String item;
      public int pence;
    }
  }
}
