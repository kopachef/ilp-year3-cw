package uk.ac.ed.inf;

import java.util.ArrayList;

class GeneratedJsonObjects {
    /**
     * Static class acting as a blueprint for a generic 'menus.json' file containing a list of menus
     * associated with individual restaurants.
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
     * Static class acting a blueprint for a generic W3W(What Three Words) 'details.json' file containing
     * details associated with a specified W3W addresses.
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
