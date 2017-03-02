package q3;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Connor Lewis on 2/28/2017.
 */
public class OnlineStore {
    private final HashMap<String, Integer> inventory;
    private final HashMap<Integer, Order> customers;
    private final HashMap<String, List<Order>> customerOrders;
    private final AtomicInteger orderNumber;


    public OnlineStore(String inventoryFile) throws IOException {
        inventory = new HashMap<>();
        customers = new HashMap<Integer, Order>();
        customerOrders = new HashMap<String, List<Order>>();
        orderNumber = new AtomicInteger(1);

        Scanner sc;
        sc = new Scanner(new FileReader(inventoryFile));
        while (sc.hasNextLine()) {
            String product = sc.nextLine();

            String[] tokens = product.split(" ");
            if (tokens.length != 2) {
                continue;
            }
            inventory.put(tokens[0], Integer.parseInt(tokens[1]));
        }
    }

    public synchronized String purchase(String userName, String productName, int quantity){
        Integer amtInStock = (Integer) inventory.get(productName);

        if(amtInStock == null){
            return "Not Available at this time";
        }

        else if (amtInStock < quantity){
            return "Sorry there is not enough inventory to complete this order";
        }

        int id = orderNumber.getAndIncrement();

        Order order = new Order(id, userName, productName, quantity);
        customers.put(id, order);
        inventory.replace(productName, amtInStock - quantity);

        if(customerOrders.containsKey(userName)){
            List orderList = customerOrders.get(userName);
            orderList.add(order);
            customerOrders.replace(userName, orderList);
        }
        else{
            List orderList = new ArrayList();
            orderList.add(order);
            customerOrders.put(userName, orderList);
        }

        return "Order placed" + order.getId() + " " +
                order.getUserName() + " " +
                order.getProductName() + " " +
                order.getQuantity();
    }

    public synchronized String cancel(int id){
        if(!customers.containsKey(id)){
            return id + " not found";
        }

        Order order = customers.get(id);
        Integer amtInStock = inventory.get(order.getProductName());
        inventory.replace(order.getProductName(), order.getQuantity() + amtInStock);
        List<Order> customerOrder = customerOrders.get(order.getUserName());
        customerOrder.remove(order);
        return "Order " + id + " cancelled";
    }

    public synchronized String search(String customer){
        StringBuilder message = new StringBuilder();
        if(!customers.containsKey(customer)){
            return customer + " has not placed an order";
        }
        List<Order> orders = customerOrders.get(customer);
        for(Order order : orders){
            message.append("ID: " + order.getId() + " ");
            message.append(" User Name: " + order.getUserName());
            message.append(" Product Name: " + order.getProductName());
            message.append(" Quantity: " + order.getQuantity() + "\n");
        }
        return message.toString();
    }

    public synchronized String list(){
        StringBuilder inventoryList = new StringBuilder();
        for(Map.Entry<String, Integer> inventoryItems: inventory.entrySet()){
            String item = inventoryItems.getKey();
            Integer numAvailable = inventoryItems.getValue();
            inventoryList.append(item + ": " + numAvailable + "\n");
        }
        return inventoryList.toString();
    }
}
