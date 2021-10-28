package uk.ac.ed.inf;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseIO {

  public DatabaseIO(String host, String port) {
    Settings.setDefaultDatabaseHost(host);
    Settings.setDefaultDatabasePort(port);
  }
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
      System.out.println("Database connection established successfully!");
    } catch (SQLException e) {
      System.out.println(url);
      System.out.println("Could not establish database connection");
      System.exit(1);
    }
    return conn;
  }

  static class DatabaseOrder {
    String orderNo;
    Date deliveryDate;
    String customer;
    String deliverTo;
  }

  static class DatabaseOrderDetails {
    String orderNo;
    String item;
  }
}
