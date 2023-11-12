package domain;

import org.junit.jupiter.api.*;

public class OrderTest {
    private Order order;

    @BeforeEach
    public void init() {
        order = new Order();
        order.setId(0);
    }

    @Test
    public void IdDifferentTest() {
        Order newOrder = new Order();
        newOrder.setId(1);
        Assertions.assertNotEquals(order, newOrder);
    }

    @Test
    public void IdEqualTest() {
        Order newOrder = new Order();
        newOrder.setId(0);
        newOrder.setCustomer(5);
        Assertions.assertEquals(order, newOrder);
    }

    @Test
    public void NotOrderObjectTest() {
        Object object = new Object();
        Assertions.assertNotEquals(order, object);
    }

    @Test
    public void GetterSetterTest() {
        int orderId = 5;
        int customerId = 7;
        int price = 4500;
        int quantity = 100;
        order.setId(orderId);
        order.setCustomer(customerId);
        order.setPrice(price);
        order.setQuantity(quantity);
        Assertions.assertEquals(orderId, order.getId());
        Assertions.assertEquals(customerId, order.getCustomer());
        Assertions.assertEquals(price, order.getPrice());
        Assertions.assertEquals(quantity, order.getQuantity());
    }
}
