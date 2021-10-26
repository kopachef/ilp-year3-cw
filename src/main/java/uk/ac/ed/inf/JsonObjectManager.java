package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Polygon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class JsonObjectManager {

    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();

    /**
     * Parses the contents returned from reading the given W3W(What Three Words) location. When reformatted, the
     * generated URL points to a 'details.json' file and this method loads its contents into a predefined
     * W3WObject static class object.
     *
     * Example of a W3W(What Three Words) formatted location is: "butter.climb.talk".
     * @param locations String representing the location formatted as a W3W address String.
     * @return W3WObject created from the reading the contents of the W3W address.
     */
    public static W3WObject parseW3WObject(String locations) {
      String addressUrl =
          Settings.getDefaultServerUrlProtocol()
              + Settings.getDefaultServerHost()
              + ":"
              + Settings.getDefaultServerPort()
              + Settings.getDefaultW3wContentRootDirectory()
              + locations.replace(".", "/")
              + "/"
              + Settings.getDefaultW3wContentFilename();
      return gson.fromJson(UrlDownloadManager.loadUrlContents(addressUrl), W3WObject.class);
    }

    /**
     * Parses the contents of reading a 'menus.json' file. Default parameters are read from the Settings file which
     * includes the updated host, port and full menu file path. Content read from the menu is loaded into a menu
     * ArrayList and returned.
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
        Type restaurantMenuTypes =
                new TypeToken<ArrayList<Menu>>() {}.getType();
        return gson.fromJson(UrlDownloadManager.loadUrlContents(jsonMenuListUrl), restaurantMenuTypes);
    }

    /**
     * Static class acting as a blueprint for a generic object created from parsing a 'menus.json' file containing a
     * list of menus associated with individual restaurants.
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

    /**
     * Static class acting a blueprint for a generic W3W(What Three Words) object created from parsing a
     * 'details.json' file containing details associated with a specified W3W addresses.
     */
    static class W3WObject {
        String country;
        Square square;
        String nearestPlace;
        Coords coordinates;
        String words;
        String language;
        String map;

        static class Coords {
            String lng;
            String lat;
        }

        static class Square {
            Coords southwest;
            Coords northeast;
        }
    }
}
