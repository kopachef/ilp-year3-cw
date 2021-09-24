package uk.ac.ed.inf;

public class Menus {

    private String name;
    private String port;

    public Menus(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public int getDeliveryCost(String... items) {
        //TODO implement me.
        //return the totla cost fo all the items in triungs in aditiont to standard charge
        int cost = 0;
        for(String item : items) {
            cost += 1;
        }
        return cost;
    }
}
