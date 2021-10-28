package uk.ac.ed.inf;

import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import uk.ac.ed.inf.DatabaseIO.DatabaseOrder;
import uk.ac.ed.inf.DatabaseIO.DatabaseOrderDetails;

public class OrderDeliveryWorker {
    /**
     * priority queue to greeedily keep te order we have in check
     */
    private Drone drone;
    private PriorityQueue<FoodOrder> foodOrderQueue;
    private Date orderProcessingDate;
    private DatabaseIO databaseAccess;

    public OrderDeliveryWorker(Drone drone, Date orderProcessingDate) {
        this.orderProcessingDate = orderProcessingDate;
        this.drone = drone;
        this.databaseAccess = new DatabaseIO(Settings.getDefaultDatabaseHost(),
                Settings.getDefaultDatabasePort());

    }
    public void recreatePriorityQueue(LongLat startLocation) {
        this.foodOrderQueue = new PriorityQueue<FoodOrder>(new Comparator<FoodOrder>() {
            @Override
            public int compare(FoodOrder o1, FoodOrder o2) {
                double straightDistanceToO1 = startLocation.distanceTo(o1.getDeliveryLocationLongLat());
                double straightDistanceToO2 = startLocation.distanceTo(o2.getDeliveryLocationLongLat());
                return Double.compare(
                        (o1.getDeliveryCost()/straightDistanceToO1),(o2.getDeliveryCost()/straightDistanceToO2));
            }
        });
    }

    public void populateFoodOrders(Date order){
        List<DatabaseOrder> dbOrders = databaseAccess.queryOrders("",order,"","");
        if(!dbOrders.isEmpty()){
            List<DatabaseOrderDetails> dbOrderdetails =
                    databaseAccess.queryOrderDetails(dbOrders.get(0).orderNo, "");

        }
    }

    public List<FoodOrder> generateFoodOrders(
            List<DatabaseOrder> dbOrders,List<DatabaseOrderDetails> dbOrderDetails)  {
        return null;
    }


}
