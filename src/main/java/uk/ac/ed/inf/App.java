package uk.ac.ed.inf;

import java.util.List;

/** Hello world! */
public class App {
  public static void main(String[] args) {
    DatabaseIO db = new DatabaseIO("localhost", "1527");
    List<DatabaseIO.DatabaseOrderDetails> orders =
            db.queryOrderDetails("","");
    System.out.println(orders);
  }
}