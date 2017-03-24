/**
 * Created by Connor Lewis on 3/1/2017.
 */
public class Order {
    private final int id;
    private final String userName;
    private final String productName;
    private final int quantity;

    public Order(int id, String userName, String productName, int quantity){
        this.id = id;
        this.userName = userName;
        this.productName = productName;
        this.quantity = quantity;
    }

    public int getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getProductName(){
        return productName;
    }

    public int getQuantity(){
        return quantity;
    }
}
