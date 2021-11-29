package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.checkerframework.checker.units.qual.A;
import uk.ac.ed.inf.algorithm.Graph;
import uk.ac.ed.inf.algorithm.Node;
import uk.ac.ed.inf.algorithm.PathSmoothing;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App {

  public static void main(String[] args) {
    boolean resetDatabase = false;
    if(resetDatabase) {
      DatabaseIO.recreateFlightAndDeliveryTables();
    }

    DeliveryPlanner deliveryPlanner = new DeliveryPlanner(Date.valueOf("2022-12-19"));
    deliveryPlanner.generatePathMap();
    deliveryPlanner.generateDeliveryReport();
    writeFlightAndDeliveryData(deliveryPlanner.getFlightPaths(),deliveryPlanner.getDeliveries());
  }

  public static void writeFlightAndDeliveryData(LinkedList<DatabaseIO.FlightPath> flightPathLinkedList,
                                                LinkedList<DatabaseIO.Delivery> deliveryLinkedList) {
    int writeBufferSize = Settings.getDefaultInsertBufferSize();
    boolean writeComplete = false;

    while(!writeComplete) {
      List<DatabaseIO.FlightPath> flightPaths = new ArrayList<>();
      List<DatabaseIO.Delivery> deliveryList = new ArrayList<>();

      for(int i = 0; i < writeBufferSize; i++) {
        if(!flightPathLinkedList.isEmpty()) {
          flightPaths.add(flightPathLinkedList.poll());
        }
        if(!deliveryLinkedList.isEmpty()){
          deliveryList.add(deliveryLinkedList.poll());
        }
      }
      if(!deliveryList.isEmpty()) {
        DatabaseIO.insertDelivery(deliveryList);
      }
      if(!flightPaths.isEmpty()) {
        DatabaseIO.insertFLightPath(flightPaths);
      }
      writeComplete = flightPathLinkedList.isEmpty() && deliveryLinkedList.isEmpty();
    }
  }
}