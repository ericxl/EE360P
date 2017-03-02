/**
 * Created by Eric on 3/1/17.
 */
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
public class UnitTest {
    private Server getEmpty(){
        HashMap<String, Integer> inv = new HashMap<>();
        inv.put("xbox", 4);
        inv.put("phone", 6);
        inv.put("pc", 10);
        Server s = new Server(inv);
        return s;
    }

    private Server getPurchased(){
        HashMap<String, Integer> inv = new HashMap<>();
        inv.put("xbox", 4);
        inv.put("phone", 6);
        inv.put("pc", 10);
        Server s = new Server(inv);
        ArrayList<Integer> o = new ArrayList<>();
        o.add(new Integer(1));
        s.user_orders.put("eric", o);
        s.orderid = 2;
        s.orders.put(1, "eric phone 3");
        return s;
    }

    @Test
    public void testPurchaseItemNotExists() {
        Server s = getEmpty();
        String response = s.debug("purchase eric xbo 2");
        assertEquals("Not Available - We do not sell this product", response);
    }

    @Test
    public void testPurchaseNotEnoughItems() {
        Server s = getEmpty();
        String response = s.debug("purchase eric xbox 5");
        assertEquals("Not Available - Not enough items", response);
        assertEquals(new Integer(4),s.inventory.get("xbox"));
        assertTrue(s.orders.isEmpty());
        assertTrue(s.user_orders.isEmpty());
    }

    @Test
    public void testPurchaseSuccess() {
        Server s = getEmpty();
        String response1 = s.debug("purchase eric xbox 3");
        assertEquals("You order has been placed, 1 eric xbox 3", response1);
        assertEquals(new Integer(1),s.inventory.get("xbox"));
        assertEquals("eric xbox 3", s.orders.get(new Integer(1)));
        assertTrue(s.user_orders.get("eric").contains(new Integer(1)));
        assertEquals(2, s.orderid);

        String response2 = s.debug("purchase eric phone 3");
        assertEquals("You order has been placed, 2 eric phone 3", response2);
        assertEquals(new Integer(3),s.inventory.get("phone"));
        assertEquals("eric phone 3", s.orders.get(new Integer(2)));
        assertTrue(s.user_orders.get("eric").contains(new Integer(2)));
        assertEquals(3, s.orderid);

        String response3 = s.debug("purchase chris phone 3");
        assertEquals("You order has been placed, 3 chris phone 3", response3);
        assertEquals(new Integer(0),s.inventory.get("phone"));
        assertEquals("chris phone 3", s.orders.get(new Integer(3)));
        assertTrue(s.user_orders.get("chris").contains(new Integer(3)));
        assertEquals(4, s.orderid);
    }

    @Test
    public void testCancelNotExists() {
        Server s = getPurchased();
        String response = s.debug("cancel 2");
        assertEquals("2 not found, no such order", response);
        assertEquals(new Integer(6),s.inventory.get("phone"));
        assertEquals("eric phone 3",s.orders.get(1));
        assertTrue(s.user_orders.get("eric").contains(new Integer(1)));
    }
}
