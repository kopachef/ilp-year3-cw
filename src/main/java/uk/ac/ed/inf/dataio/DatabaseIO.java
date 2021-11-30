package uk.ac.ed.inf.dataio;

import uk.ac.ed.inf.deliveryutils.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseIO {

  /**
   * Inserts an order into our database.
   *
   * @param orderNo String value of order number to be added.
   * @param deliveryDate Date value of the associated order.
   * @param customer Customer to whom the order is to be delivered to.
   * @param deliverTo What Three Words string representing delivery address.
   */
  public static void insertOrder(
      String orderNo, Date deliveryDate, String customer, String deliverTo) {
    Connection conn = initialiseDBConnection();
    final String insertOrderQuery =
        "INSERT INTO orders (orderNo, deliveryDate, customer, deliverTo) VALUES (?,?,?,?)";
    try {
      PreparedStatement psInsertQuery = conn.prepareStatement(insertOrderQuery);
      psInsertQuery.setString(1, orderNo);
      psInsertQuery.setDate(2, deliveryDate);
      psInsertQuery.setString(3, customer);
      psInsertQuery.setString(4, deliverTo);
      int row = psInsertQuery.executeUpdate();
      System.out.println("Successfully insert into row: " + row);
      psInsertQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allows us to insert order details into the order details table of our database.
   *
   * @param orderNo Order number to be inserted.
   * @param item Item associated with the order number.
   */
  public static void insertOrderDetails(String orderNo, String item) {
    Connection conn = initialiseDBConnection();
    final String insertOrderDetailsQuery = "INSERT INTO orderDetails (orderNo, item) VALUES (?,?)";
    try {
      PreparedStatement psInsertQuery = conn.prepareStatement(insertOrderDetailsQuery);
      psInsertQuery.setString(1, orderNo);
      psInsertQuery.setString(2, item);
      int row = psInsertQuery.executeUpdate();
      System.out.println("Successfully insert into row: " + row);
      psInsertQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allowed us to delete an order in teh database.
   *
   * @param orderNo order number associated with item tob e deleted.
   * @param deliveryDate delivery date associated with item to be deleted.
   * @param customer customer associated with item to be deleted.
   * @param deliverTo delivery location associated with item to be deleted.
   */
  public static void deleteOrder(
      String orderNo, Date deliveryDate, String customer, String deliverTo) {
    Connection conn = initialiseDBConnection();
    final String deleteQuery =
        "DELETE FROM orders WHERE orderNo=? AND deliveryDate=? AND customer=? AND deliverTo=?";
    try {
      PreparedStatement psDeleteQuery = conn.prepareStatement(deleteQuery);
      psDeleteQuery.setString(1, orderNo);
      psDeleteQuery.setDate(2, deliveryDate);
      psDeleteQuery.setString(3, customer);
      psDeleteQuery.setString(4, deliverTo);
      int row = psDeleteQuery.executeUpdate();
      System.out.println("Successfully deleted entry at row: " + row);
      psDeleteQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allows us to delete order details in our database.
   *
   * @param orderNo order number associated with item to be deleted.
   * @param item item name associated with item to be deleted.
   */
  public static void deleteOrderDetails(String orderNo, String item) {
    Connection conn = initialiseDBConnection();
    final String deleteQuery = "DELETE FROM orders WHERE orderNo=? AND item=?";
    try {
      PreparedStatement psDeleteQuery = conn.prepareStatement(deleteQuery);
      psDeleteQuery.setString(1, orderNo);
      psDeleteQuery.setString(2, item);
      int row = psDeleteQuery.executeUpdate();
      System.out.println("Successfully deleted entry at row: " + row);
      psDeleteQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allows to query our OrderDetails table for items matching the specified parameters. Returns the
   * most matching parameters i.e if only one parameter is specified for example 'orderNo' it would
   * return all the order matching this order number. Likewise, returns all our orderDetails if
   * empty parameters are given.
   *
   * @param orderNo order number to be matched against.
   * @param item item name to be matched against.
   * @return List of <DatabaseOrderDetails> objects.
   */
  public static List<DatabaseOrderDetails> queryOrderDetails(String orderNo, String item) {
    Connection conn = initialiseDBConnection();
    List<DatabaseOrderDetails> databaseOrders = new ArrayList<>();
    ResultSet results;
    final String orderDetailsQuery =
        "SELECT * FROM orderDetails WHERE orderNo LIKE ? AND item LIKE ?";
    try {
      PreparedStatement psOrderDetails = conn.prepareStatement(orderDetailsQuery);
      psOrderDetails.setString(1, "%" + orderNo + "%");
      psOrderDetails.setString(2, "%" + item + "%");
      results = psOrderDetails.executeQuery();

      while (results.next()) {
        DatabaseOrderDetails databaseOrderDetails = new DatabaseOrderDetails();
        databaseOrderDetails.orderNo = results.getString(1);
        databaseOrderDetails.item = results.getString(2);
        databaseOrders.add(databaseOrderDetails);
      }
      psOrderDetails.close();
      results.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return databaseOrders;
  }

  /**
   * Allows to query our Orders table for items matching the specified parameters. Returns the most
   * matching items with respect to the parameters i.e if only one parameter is specified for
   * example 'orderNo' it would return all the order matching this order number. Likewise, returns
   * all our orders if empty parameters are given.
   *
   * @param orderNo order number to be matched against.
   * @param deliveryDate delivery date to be matched against.
   * @param customer customer to be matched agsinst.
   * @param deliverTo delivery location ot ve matched against.
   * @return List of <DatabaseOrders> objects.
   */
  public static List<DatabaseOrder> queryOrders(
      String orderNo, Date deliveryDate, String customer, String deliverTo) {
    Connection conn = initialiseDBConnection();
    List<DatabaseOrder> DatabaseOrders = new ArrayList<>();
    ResultSet results;
    final String ordersQuery =
        Optional.ofNullable(deliveryDate).isPresent()
            ? "SELECT * FROM orders WHERE orderNo LIKE ? AND deliveryDate = ? AND customer LIKE ? AND deliverTo LIKE ?"
            : "SELECT * FROM orders WHERE orderNo LIKE ? AND deliveryDate >= ? AND customer LIKE ? AND deliverTo LIKE ?";
    try {
      PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
      Date deliveryDate_ =
          Optional.ofNullable(deliveryDate).isPresent()
              ? deliveryDate
              : new Date(Integer.MIN_VALUE);
      psOrdersQuery.setString(1, "%" + orderNo + "%");
      psOrdersQuery.setDate(2, deliveryDate_);
      psOrdersQuery.setString(3, "%" + customer + "%");
      psOrdersQuery.setString(4, "%" + deliverTo + "%");
      results = psOrdersQuery.executeQuery();

      while (results.next()) {
        DatabaseOrder databaseOrderObject = new DatabaseOrder();
        databaseOrderObject.orderNo = results.getString(1);
        databaseOrderObject.deliveryDate = results.getDate(2);
        databaseOrderObject.customer = results.getString(3);
        databaseOrderObject.deliverTo = results.getString(4);
        DatabaseOrders.add(databaseOrderObject);
      }
      psOrdersQuery.close();
      results.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return DatabaseOrders;
  }

  /**
   * Creates the delivery and flightpath tables in the database.
   *
   * @throws SQLException if the database connection fails.
   */
  public static void recreateFlightAndDeliveryTables() {
    Connection conn = initialiseDBConnection();
    final String createDeliveryTableQuery =
        "create table "
            + "deliveries(orderNo char(8),"
            + "deliveredTo varchar(19), "
            + "costInPence int)";
    final String createFlightPathTable =
        "create table "
            + "flightpath(orderNo char(8),"
            + "fromLongitude double,"
            + "fromLatitude double,"
            + "angle integer,"
            + "toLongitude double,"
            + "toLatitude double)";
    final String dropTable = "drop table ";
    final String flightPathTable = "flightpath";
    final String deliveriesTable = "deliveries";

    try {
      Statement stmt = conn.createStatement();
      if (tableExists(flightPathTable)) {
        stmt.executeUpdate(dropTable + flightPathTable);
      }
      if (tableExists(deliveriesTable)) {
        stmt.executeUpdate(dropTable + deliveriesTable);
      }
      stmt.executeUpdate(createDeliveryTableQuery);
      stmt.executeUpdate(createFlightPathTable);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method checks if a table exists in a database.
   *
   * @param tableName
   * @return
   * @throws SQLException
   */
  public static boolean tableExists(String tableName) throws SQLException {
    Connection conn = initialiseDBConnection();

    DatabaseMetaData dbdm = conn.getMetaData();
    ResultSet rs = dbdm.getTables(null, null, tableName.toUpperCase(), null);
    return rs.next();
  }

  /**
   * This function inserts a new deliveries into the table of deliveries made. The deliveries are
   * given as Delivery objects as outlined by our Delivery static class.
   *
   * @param deliveries list of deliveries ot be added.
   */
  public static void insertDelivery(List<Delivery> deliveries) {
    Connection conn = initialiseDBConnection();
    StringBuffer insertQuery =
        new StringBuffer(
            "insert into deliveries (orderNo, deliveredTo, costInPence) values (?, ?, ?)");
    for (int i = 0; i < deliveries.size() - 1; i++) {
      insertQuery.append(", (?, ?, ?)");
    }
    try {
      PreparedStatement deliveryInsertQuery = conn.prepareStatement(insertQuery.toString());
      for (int i = 0; i < deliveries.size(); i++) {
        deliveryInsertQuery.setString((2 * i) + i + 1, deliveries.get(i).orderNo);
        deliveryInsertQuery.setString((2 * i) + i + 2, deliveries.get(i).deliveredTo);
        deliveryInsertQuery.setInt((2 * i) + i + 3, deliveries.get(i).costInPence);
      }
      int row = deliveryInsertQuery.executeUpdate();
      System.out.println("Successfully inserted deliveries count: " + row);
      deliveryInsertQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of deliveries for a given order number. Empty string can be given to query all
   * orders.
   *
   * @param orderNo The order number to lookup.
   * @return A list of deliveries for the given order number.
   * @throws SQLException if there is a problem inserting the data into the database
   */
  public static List<Delivery> getDeliveries(String orderNo) {
    Connection conn = initialiseDBConnection();
    StringBuffer query = new StringBuffer();
    List<Delivery> deliveries = new ArrayList<>();
    query.append("SELECT * FROM deliveries WHERE orderNo LIKE ?");
    try {
      PreparedStatement deliveryQuery = conn.prepareStatement(query.toString());
      deliveryQuery.setString(1, "%" + orderNo + "%");
      ResultSet rs = deliveryQuery.executeQuery();
      while (rs.next()) {
        Delivery delivery = new Delivery();
        delivery.orderNo = rs.getString(1);
        delivery.deliveredTo = rs.getString(2);
        delivery.costInPence = rs.getInt(3);
        deliveries.add(delivery);
      }
      rs.close();
      deliveryQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return deliveries;
  }

  /**
   * Inserts a new flight paths into the database.
   *
   * @param flightPaths list of flight paths to be added.
   * @throws SQLException if there is a problem inserting the data into the database
   */
  public static void insertFLightPath(List<FlightPath> flightPaths) {
    Connection conn = initialiseDBConnection();
    StringBuffer insertQuery =
        new StringBuffer(
            "insert into flightpath (orderNo, fromLongitude, fromLatitude, angle, toLongitude, toLatitude)"
                + " values (?, ?, ?, ?, ?, ?)");
    for (int i = 0; i < flightPaths.size() - 1; i++) {
      insertQuery.append(", (?, ?, ?, ?, ?, ?)");
    }
    try {
      PreparedStatement flightInsertQuery = conn.prepareStatement(insertQuery.toString());
      for (int i = 0; i < flightPaths.size(); i++) {
        flightInsertQuery.setString((5 * i) + i + 1, flightPaths.get(i).orderNo);
        flightInsertQuery.setDouble((5 * i) + i + 2, flightPaths.get(i).fromLongitude);
        flightInsertQuery.setDouble((5 * i) + i + 3, flightPaths.get(i).fromLatitude);
        flightInsertQuery.setInt((5 * i) + i + 4, flightPaths.get(i).angle);
        flightInsertQuery.setDouble((5 * i) + i + 5, flightPaths.get(i).toLongitude);
        flightInsertQuery.setDouble((5 * i) + i + 6, flightPaths.get(i).toLatitude);
      }
      int row = flightInsertQuery.executeUpdate();
      System.out.println("Successfully inserted flights count: " + row);
      flightInsertQuery.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * The getFLightPaths() method retrieves a list of FlightPaths from a given order number. An empty
   * string can be used to query all flight paths.
   *
   * @param orderNo the order number to search for.
   * @return a list of FlightPath objects
   * @throws SQLException if there is a problem inserting the data into the database
   */
  public static List<FlightPath> getFLightPaths(String orderNo) {
    Connection conn = initialiseDBConnection();
    StringBuffer query = new StringBuffer();
    List<FlightPath> flightPaths = new ArrayList<>();
    query.append(
        "SELECT orderNo, fromLongitude, fromLatitude, angle, toLongitude, toLatitude "
            + " FROM flightpath "
            + " WHERE orderNo = LIKE ?");
    try {
      PreparedStatement flightQuery = conn.prepareStatement(query.toString());
      flightQuery.setString(1, "%" + orderNo + "%");
      ResultSet rs = flightQuery.executeQuery();
      while (rs.next()) {
        FlightPath fp = new FlightPath();
        fp.orderNo = rs.getString(1);
        fp.fromLongitude = rs.getDouble(2);
        fp.fromLatitude = rs.getDouble(3);
        fp.angle = rs.getInt(4);
        fp.toLongitude = rs.getDouble(5);
        fp.toLatitude = rs.getDouble(6);
        flightPaths.add(fp);
      }
      rs.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return flightPaths;
  }

  /**
   * Initialises a connection to our database.
   *
   * @return returns database connection.
   * @throws SQLException if there is a problem inserting the data into the database
   */
  private static Connection initialiseDBConnection() {
    Connection conn = null;
    String url =
        Settings.getDefaultDatabaseProtocol()
            + Settings.getDefaultDatabaseHost()
            + ":"
            + Settings.getDefaultDatabasePort()
            + "/"
            + Settings.getDefaultDatabaseAddress();
    try {
      conn = DriverManager.getConnection(url);
    } catch (SQLException e) {
      System.out.println(url);
      System.out.println("Could not establish database connection");
      e.printStackTrace();
      System.exit(1);
    }
    return conn;
  }

  /** static classes representing transient DatabaseOrder objects returned from a query. */
  public static class DatabaseOrder {
    public String orderNo;
    public Date deliveryDate;
    public String customer;
    public String deliverTo;
  }

  /** static classes representing transient DatabaseOrderDetails objects returned from a query. */
  public static class DatabaseOrderDetails {
    String orderNo;
    public String item;
  }

  /** static class representing transient Delivery objects returned from a database query. */
  public static class Delivery {
    public String orderNo;
    public String deliveredTo;
    public int costInPence;
  }

  /** static class representing transient FlightPath objects returned from a database query. */
  public static class FlightPath {
    public String orderNo;
    public double fromLongitude;
    public double fromLatitude;
    public int angle;
    public double toLongitude;
    public double toLatitude;
  }
}
