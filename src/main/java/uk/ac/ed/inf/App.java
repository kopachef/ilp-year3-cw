package uk.ac.ed.inf;

import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;

import java.sql.Date;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/** Hello world! */
public class App {

  public static void main(String[] args) {
    int granularity = 54;

    LongLat home = Settings.getDefaultHomeLocation();
    LongLat devloc = new LongLat(-3.187837, 55.943497);

    LongLat end = new LongLat(-3.1898600, 55.942931);
    LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190, 55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);

    Graph g =
            new Graph(
                Settings.getDefaultNorthWestBound().longitude,
                Settings.getDefaultNorthWestBound().latitude,
                Settings.getDefaultSouthEastBound().longitude,
                Settings.getDefaultSouthEastBound().latitude,
                granularity);

     DeliveryPlanner deliveryPlanner = new DeliveryPlanner(Date.valueOf("2023-12-19"));
     deliveryPlanner.generatePathMap();
     //System.out.println(deliveryPlanner.getDeliveryPaths());
    }
}