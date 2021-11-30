package uk.ac.ed.inf.dataio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.utils.Settings;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonObjectManager {

  private static final GsonBuilder gsonBuilder = new GsonBuilder();
  private static final Gson gson = gsonBuilder.create();

  /**
   * Parses the contents returned from reading the given W3W(What Three Words) location. When
   * reformatted, the generated URL points to a 'details.json' file and this method loads its
   * contents into a predefined W3WObject static class object.
   *
   * <p>Example of a W3W(What Three Words) formatted location is: "butter.climb.talk".
   *
   * @param location String representing the location formatted as a W3W address String.
   * @return W3WObject created from the reading the contents of the W3W address.
   */
  public static W3WObject parseW3WObject(String location) {
    String addressUrl =
        Settings.getDefaultServerUrlProtocol()
            + Settings.getDefaultServerHost()
            + ":"
            + Settings.getDefaultServerPort()
            + Settings.getDefaultW3wContentRootDirectory()
            + location.replace(".", "/")
            + "/"
            + Settings.getDefaultW3wContentFilename();
    return gson.fromJson(UrlDownloadManager.loadUrlContents(addressUrl), W3WObject.class);
  }

  /**
   * Parses the contents of reading a 'menus.json' file. Default parameters are read from the
   * Settings file which includes the updated host, port and full menu file path. Content read from
   * the menu is loaded into a menu ArrayList and returned.
   *
   * @return Returns an ArrayList of 'Menu' objects generated from reading the 'menus.json' file.
   */
  public static ArrayList<Menu> parseMenuObject() {
    String jsonMenuListUrl =
        Settings.getDefaultServerUrlProtocol()
            + Settings.getDefaultServerHost()
            + ":"
            + Settings.getDefaultServerPort()
            + Settings.getDefaultMenusAddressResource();
    Type restaurantMenuTypes = new TypeToken<ArrayList<Menu>>() {}.getType();
    return gson.fromJson(UrlDownloadManager.loadUrlContents(jsonMenuListUrl), restaurantMenuTypes);
  }

  /**
   * Utility function to convert the Coord attribute of a W3WObjet into a valid LongLat object.
   *
   * @param coords Coord class under the static W3WObject class holding two string values
   *     representing the longitude and latitude values of W3W location.
   * @return returns a valid LongLat object with longitude and latitude attribute values being equal
   *     to the W3WObject.Coords class object given as an argument.
   */
  public static LongLat coordToLonglat(W3WObject.Coords coords) {
    double longitude = Double.valueOf(coords.lng);
    double latitude = Double.valueOf(coords.lat);
    return new LongLat(longitude, latitude);
  }

  /**
   * Static class acting as a blueprint for a generic object created from parsing a 'menus.json'
   * file containing a list of menus associated with individual restaurants.
   */
  public static class Menu {
    public String name;
    public String location;
    public ArrayList<Item> menu;

    public static class Item {
      public String item;
      public int pence;
    }
  }

  /**
   * Static class acting a blueprint for a generic W3W(What Three Words) object created from parsing
   * a 'details.json' file containing details associated with a specified W3W addresses.
   */
  public static class W3WObject {
    public String country;
    public Square square;
    public String nearestPlace;
    public Coords coordinates;
    public String words;
    public String language;
    public String map;

    public static class Coords {
      public String lng;
      public String lat;
    }

    public static class Square {
      public Coords southwest;
      public Coords northeast;
    }
  }
}
