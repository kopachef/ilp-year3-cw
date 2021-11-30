package uk.ac.ed.inf;

import uk.ac.ed.inf.dataio.DatabaseIO;
import uk.ac.ed.inf.utils.Settings;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;


public class App {

  public static void main(String[] args) {

    int expectedCmdArgLength5 = 5;
    int expectedCmdArgLength3 = 3;

    Date deliveryDate;
    String webServerPort;
    String dbServerPort;

    if (args.length == expectedCmdArgLength5) {
      String day = args[0];
      String month = args[1];
      String year = args[2];

      webServerPort = args[3];
      dbServerPort = args[4];

      Settings.setDefaultServerPort(webServerPort);
      Settings.setDefaultDatabasePort(dbServerPort);
      deliveryDate = Date.valueOf(year + "-" + month + "-" + day);

      DeliveryPlanner deliveryPlanner = new DeliveryPlanner(deliveryDate);
      deliveryPlanner.generatePathMap();
      writeFlightAndDeliveryData(deliveryPlanner.getFlightPaths(), deliveryPlanner.getDeliveries());

      }
    else if(args.length == expectedCmdArgLength3) {

      String[] dateValues = args[0].split("/");
      String day = dateValues[0];
      String month = dateValues[1];
      String year = dateValues[2];

      deliveryDate = Date.valueOf(year + "-" + month + "-" + day);

      webServerPort = args[1];
      dbServerPort = args[2];

      Settings.setDefaultServerPort(webServerPort);
      Settings.setDefaultDatabasePort(dbServerPort);

      DeliveryPlanner deliveryPlanner = new DeliveryPlanner(deliveryDate);
      deliveryPlanner.generatePathMap();
      writeFlightAndDeliveryData(deliveryPlanner.getFlightPaths(), deliveryPlanner.getDeliveries());

    } else {
      System.err.println("Invalid command line argument length.\nExpected length: "
                      + expectedCmdArgLength5 + " or " + expectedCmdArgLength3 +
              "\nArgument length given: " + args.length);
      System.exit(1);
    }
  }

  /**
   * The writeFlightAndDeliveryData() function writes the flightPathLinkedList and the deliveryLinkedList to the
   * FlightPath and Delivery tables, respectively. The writeBufferSize is set to the default value of
   * Settings.getDefaultInsertBufferSize(). If the writeBufferSize is reached, the function will insert the
   * FlightPath and Delivery lists into the tables and reset the buffer to load more entries if available.
   *
   * @param flightPathLinkedList linked list of flight paths.
   * @param deliveryLinkedList linked list of deliveries.
   */

  public static void writeFlightAndDeliveryData(
      LinkedList<DatabaseIO.FlightPath> flightPathLinkedList,
      LinkedList<DatabaseIO.Delivery> deliveryLinkedList) {
    int writeBufferSize = Settings.getDefaultInsertBufferSize();
    DatabaseIO.recreateFlightAndDeliveryTables();
    boolean writeComplete = false;

    while (!writeComplete) {
      List<DatabaseIO.FlightPath> flightPaths = new ArrayList<>();
      List<DatabaseIO.Delivery> deliveryList = new ArrayList<>();

      for (int i = 0; i < writeBufferSize; i++) {
        if (!flightPathLinkedList.isEmpty()) {
          flightPaths.add(flightPathLinkedList.poll());
        }
        if (!deliveryLinkedList.isEmpty()) {
          deliveryList.add(deliveryLinkedList.poll());
        }
      }
      if (!deliveryList.isEmpty()) {
        DatabaseIO.insertDelivery(deliveryList);
      }
      if (!flightPaths.isEmpty()) {
        DatabaseIO.insertFLightPath(flightPaths);
      }
      writeComplete = flightPathLinkedList.isEmpty() && deliveryLinkedList.isEmpty();
    }
  }

  /**
   * This simulates orders being placed and calculates the runtime for each run. Selected dates are ordered
   * in increasing order of the number of orders placed on each day(4 to 27) and should give us a good picture of how
   * the algorithm scales as we increase the number of orders placed.
   */
  public static void generateRuntimeSummaryStatistics() {

    List<Date> dates = List.of(
            Date.valueOf("2022-01-01"),
            Date.valueOf("2022-02-04"),
            Date.valueOf("2022-03-12"),
            Date.valueOf("2022-04-17"),
            Date.valueOf("2022-05-05"),
            Date.valueOf("2022-06-26"),
            Date.valueOf("2022-07-05"),
            Date.valueOf("2022-08-07"),
            Date.valueOf("2022-09-12"),
            Date.valueOf("2022-10-19"),
            Date.valueOf("2022-11-30"),
            Date.valueOf("2022-12-13"),
            Date.valueOf("2023-01-22"),
            Date.valueOf("2023-02-18"),
            Date.valueOf("2023-03-15"),
            Date.valueOf("2023-04-05"),
            Date.valueOf("2023-05-01"),
            Date.valueOf("2023-06-02"),
            Date.valueOf("2023-07-24"),
            Date.valueOf("2023-08-14"),
            Date.valueOf("2023-09-24"),
            Date.valueOf("2023-10-15"),
            Date.valueOf("2023-11-04"),
            Date.valueOf("2023-12-31"));

    for(int i = 0; i < dates.size();i++) {

      LongSummaryStatistics longSummaryStatistics = new LongSummaryStatistics();
      int counter = 10;
      while(counter > 0) {
        long start = System.currentTimeMillis();

        DeliveryPlanner dp = new DeliveryPlanner(dates.get(i));
        dp.generatePathMap();
        writeFlightAndDeliveryData(dp.getFlightPaths(), dp.getDeliveries());

        long time = System.currentTimeMillis() - start;

        longSummaryStatistics.accept(time);
        counter -= 1;
      }

      int orderNumber = i+4;

      System.out.println(
              "*******************************\nNum of Orders made: " + orderNumber +
                      "\nCycle count: " + longSummaryStatistics.getCount() +
                      "\nMax runtime: " +longSummaryStatistics.getMax() +
                      "\nMin runtime: " + longSummaryStatistics.getMin() +
                      "\nAverage runtime: " + longSummaryStatistics.getAverage());
    }
  }
}
