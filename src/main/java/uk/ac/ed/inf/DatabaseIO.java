package uk.ac.ed.inf;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseIO {

  /**
   * Database IO object to act as an access point for our database.
   *
   * @param host host address to our database
   * @param port port to access our database.
   */
  public DatabaseIO(String host, String port) {
    Settings.setDefaultDatabaseHost(host);
    Settings.setDefaultDatabasePort(port);
  }

  /**
   * Inserts an order into our database.
   *
   * @param orderNo String value of order number to be added.
   * @param deliveryDate Date value of the associated order.
   * @param customer Customer to whom the order is to be  delivered to.
   * @param deliverTo What Three Words string representing delivery address.
   */
  public void insertOrder(String orderNo, Date deliveryDate, String customer, String deliverTo) {
    Connection conn = initialiseDBconnection();
    final String insertOrderQuery = "INSERT INTO orders (orderNo, deliveryDate, customer, deliverTo) VALUES (?,?,?,?)";
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
  public void insertOrderDetails(String orderNo, String item) {
    Connection conn = initialiseDBconnection();
    final String insertOrderDetailsQuery = "INSERT INTO orderDetails (orderNo, item) VALUES (?,?)";
    try {
      PreparedStatement psInsertQuery = conn.prepareStatement(insertOrderDetailsQuery);
      psInsertQuery.setString(1, orderNo);
      psInsertQuery.setString(2, item);
      int row = psInsertQuery.executeUpdate();
      System.out.println("Successfully insert into row: " + row);
      psInsertQuery.close();
      conn.close();
    } catch (SQLException e){
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
  public void deleteOrder(String orderNo, Date deliveryDate, String customer, String deliverTo){
    Connection conn = initialiseDBconnection();
    final String deleteQuery = "DELETE FROM orders WHERE orderNo=? AND deliveryDate=? AND customer=? AND deliverTo=?";
    try {
      PreparedStatement psDeleteQuery = conn.prepareStatement(deleteQuery);
      psDeleteQuery.setString(1,orderNo);
      psDeleteQuery.setDate(2,deliveryDate);
      psDeleteQuery.setString(3,customer);
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
  public void deleteOrderDetails(String orderNo, String item){
    Connection conn = initialiseDBconnection();
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
   * Allows to query our OrderDetails table for items matching the specified parameters. Returns the most matching
   * parameters i.e if only one parameter is specified for example 'orderNo' it would return all the order matching
   * this order number. Likewise, returns all our orderDetails if empty parameters are given.
   *
   * @param orderNo order number to be matched against.
   * @param item item name to be matched against.
   * @return  List of <DatabaseOrderDetails> objects.
   */
  public List<DatabaseOrderDetails> queryOrderDetails(String orderNo, String item) {
    Connection conn = initialiseDBconnection();
    List<DatabaseOrderDetails> databaseOrders = new ArrayList<>();
    ResultSet results;
    final String orderDetailsQuery = "SELECT * FROM orderDetails WHERE orderNo LIKE ? AND item LIKE ?";
    try {
      PreparedStatement psOrderDetails = conn.prepareStatement(orderDetailsQuery);
      psOrderDetails.setString(1, "%" + orderNo + "%");
      psOrderDetails.setString(2, "%" + item + "%");
      results = psOrderDetails.executeQuery();

      while(results.next()) {
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
   * Allows to query our Orders table for items matching the specified parameters. Returns the most matching items with
   * respect to the parameters i.e if only one parameter is specified for example 'orderNo' it would return all the
   * order matching this order number. Likewise, returns all our orders if empty parameters are given.
   *
   * @param orderNo order number to be matched against.
   * @param deliveryDate delivery date to be matched against.
   * @param customer customer to be matched agsinst.
   * @param deliverTo delivery location ot ve matched against.
   * @return List of <DatabaseOrders> objects.
   */
  public List<DatabaseOrder> queryOrders(
      String orderNo, Date deliveryDate, String customer, String deliverTo) {
    Connection conn = initialiseDBconnection();
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
   *  Creates the delivery and flightpath tables in the database.
   *
   * @throws SQLException if the database connection fails.
   *
   */
  public void recreateFlightAndDeliveryTables() {
    Connection conn = initialiseDBconnection();

    final String createDeliveryTableQuery =
        "create table " +
                "deliveries(orderNo char(8)," +
                "deliveredTo varchar(19), " +
                "costInPence int);";
    final String createFlightPathTable =
            "create table" +
                    "flightpath(orderNo char(8)," +
                    "fromLongitude double," +
                    "fromLatitude double," +
                    "angle integer," +
                    "toLongitude double," +
                    "toLatitude double)";
    final String dropDeliveryTableQuery = " drop if exists deliveries;";
    final String dropFlightPathTable = "drop if exists flightpath;";

    try {
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(dropDeliveryTableQuery);
      stmt.executeUpdate(dropFlightPathTable);
      stmt.executeUpdate(createDeliveryTableQuery);
      stmt.executeUpdate(createFlightPathTable);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }



  }

  /**
   * Initialises a connection to our database.
   *
   * @return returns database connection.
   */
  private Connection initialiseDBconnection() {
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

  /**
   * static classes representing transient DatabaseOrder objects returned from a query.
   */
  static class DatabaseOrder {
    String orderNo;
    Date deliveryDate;
    String customer;
    String deliverTo;
  }

  /**
   * static classes representing transient DatabaseOrderDetails objects returned from a query.
   */
  static class DatabaseOrderDetails {
    String orderNo;
    String item;
  }
}
