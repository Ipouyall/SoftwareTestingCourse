package domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EngineTest {
    private Engine engine;

    @BeforeEach
    public void init() {
        engine = new Engine();
    }

    public static Order creatOrder(int id, int customer, int price, int quantity) {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setPrice(price);
        order.setQuantity(quantity);
        return order;
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with empty orderHistory field")
    public void getAverageOrderQuantityByCustomerEmptyOrderHistory() {
        int customerId = 1;
        Assertions.assertEquals(0, engine.getAverageOrderQuantityByCustomer(customerId));
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with non empty orderHistory field and also Customer Id exist in it")
    public void getAverageOrderQuantityByCustomerNonEmptyOrderHistoryExistCustomerId() {
        int customerId = 1;
        int price1 = 100;
        int price2 = 200;
        int quantity1 = 10;
        int quantity2 = 15;
        engine.orderHistory.add(creatOrder(1, customerId, price1, quantity1));
        engine.orderHistory.add(creatOrder(2, customerId, price2, quantity2));
        engine.orderHistory.add(creatOrder(3, customerId + 1, 200, 10));
        Assertions.assertEquals((price1 + price2) / (quantity2 + quantity1),
                engine.getAverageOrderQuantityByCustomer(customerId));
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with non empty orderHistory field and also Customer Id not exist in it")
    public void getAverageOrderQuantityByCustomerNonEmptyOrderHistoryNonExistCustomerId() {
        int customerId = 1;
        int price = 100;
        int quantity = 10;
        engine.orderHistory.add(creatOrder(1, customerId, price, quantity));
        Assertions.assertThrows(ArithmeticException.class, () ->
        {
            engine.getAverageOrderQuantityByCustomer(5);
        });
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with empty orderHistory field")
    public void getQuantityPatternByPriceEmptyOrder() {
        int price = 100;
        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(price));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with non empty orderHistory field that has right patter")
    public void getQuantityPatternByPriceNonEmptyOrderHistoryPatterExist() {
        int customerId = 1;
        int targetPrice = 100;
        int startQuantity = 10;
        int diffQuantity = 5;
        engine.orderHistory.add(creatOrder(1, customerId, targetPrice, startQuantity));
        engine.orderHistory.add(creatOrder(2, customerId, targetPrice, startQuantity + 1 * diffQuantity));
        engine.orderHistory.add(creatOrder(3, customerId, 10, 200));
        engine.orderHistory.add(creatOrder(4, customerId + 1, targetPrice, startQuantity + 2 * diffQuantity));
        Assertions.assertEquals(diffQuantity,
                engine.getQuantityPatternByPrice(targetPrice));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with non empty orderHistory field that has not right patter")
    public void getQuantityPatternByPriceNonEmptyOrderHistoryPatterNotExist() {
        int targetPrice = 100;
        int startQuantity = 10;
        int diffQuantity = 5;
        engine.orderHistory.add(creatOrder(1, 1, targetPrice, startQuantity));
        engine.orderHistory.add(creatOrder(2, 1, targetPrice, 75));
        engine.orderHistory.add(creatOrder(3, 1, 10, 200));
        engine.orderHistory.add(creatOrder(4, 2, targetPrice, startQuantity + 2 * diffQuantity));
        Assertions.assertEquals(0,
                engine.getQuantityPatternByPrice(targetPrice));
    }

    @Test
    @DisplayName("Test getCustomerFraudulentQuantity with above average price")
    public void getCustomerFraudulentQuantityAboveAverageTest() {
        int quantity1 = 100;
        int quantity2 = 56;
        int averageQuantity = (quantity1 + quantity2) / 2;
        int diffQuantity = 5;
        Order order1 = creatOrder(1, 1, 100, quantity1);
        Order order2 = creatOrder(2, 1, 100, quantity2);
        Order argumanOrder = creatOrder(3, 1, 100, averageQuantity + diffQuantity);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(diffQuantity, engine.getCustomerFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test getCustomerFraudulentQuantity with less average price")
    public void getCustomerFraudulentQuantityLessAverageTest() {
        int quantity1 = 100;
        int quantity2 = 56;
        int averageQuantity = (quantity1 + quantity2) / 2;
        int diffQuantity = 5;
        Order order1 = creatOrder(1, 1, 100, quantity1);
        Order order2 = creatOrder(2, 1, 100, quantity2);
        Order argumanOrder = creatOrder(3, 1, 100, averageQuantity - diffQuantity);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(0, engine.getCustomerFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test getCustomerFraudulentQuantity with equal average price")
    public void getCustomerFraudulentQuantityEqualAverageTest() {
        int quantity1 = 100;
        int quantity2 = 56;
        int averageQuantity = (quantity1 + quantity2) / 2;
        Order order1 = creatOrder(1, 1, 100, quantity1);
        Order order2 = creatOrder(2, 1, 100, quantity2);
        Order argumanOrder = creatOrder(3, 1, 100, averageQuantity);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(0, engine.getCustomerFraudulentQuantity(argumanOrder));
    }


    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with containing order")
    public void addOrderAndGetFraudulentQuantityEmptyOrderHistory() {
        int quantity = 100;
        Order argumanOrder = creatOrder(2, 1, 100, quantity);

        engine.orderHistory.add(argumanOrder);
        Assertions.assertEquals(0, engine.addOrderAndGetFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with containing order")
    public void addOrderAndGetFraudulentQuantityContainingOrder() {
        int quantity = 100;
        Order order1 = creatOrder(1, 1, 100, quantity / 2);
        Order argumanOrder = creatOrder(2, 1, 100, quantity);
        engine.orderHistory.add(argumanOrder);
        engine.orderHistory.add(order1);

        Assertions.assertEquals(0, engine.addOrderAndGetFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with above average price")
    public void addOrderAndGetFraudulentQuantityAboveAverageTest() {
        int quantity1 = 100;
        int quantity2 = 56;
        int quantity3 = 200;
        int diffQuantity = quantity3 - ((quantity1 + quantity2) / 2);
        Order order1 = creatOrder(1, 1, 100, quantity1);
        Order order2 = creatOrder(2, 1, 100, quantity2);
        Order argumanOrder = creatOrder(3, 1, 100, quantity3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(diffQuantity, engine.addOrderAndGetFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with less average price and Patter exist")
    public void addOrderAndGetFraudulentQuantityLessAveragePatternExistTest() {
        int targetPrice = 100;
        int startQuantity = 10;
        int diffQuantity = 5;
        engine.orderHistory.add(creatOrder(1, 1, targetPrice, startQuantity));
        engine.orderHistory.add(creatOrder(2, 1, targetPrice, startQuantity + 1 * diffQuantity));
        engine.orderHistory.add(creatOrder(3, 1, 10, 200));
        engine.orderHistory.add(creatOrder(4, 1 + 1, targetPrice, startQuantity + 2 * diffQuantity));
        Order argumanOrder = creatOrder(5, 1, targetPrice, 1);
        Assertions.assertEquals(diffQuantity,
                engine.addOrderAndGetFraudulentQuantity(argumanOrder));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with equal average price and Patter not exist")
    public void addOrderAndGetFraudulentQuantityLessAveragePatternNotExistTest() {
        int targetPrice = 100;
        int startQuantity = 10;
        int diffQuantity = 5;
        engine.orderHistory.add(creatOrder(1, 1, targetPrice, startQuantity));
        engine.orderHistory.add(creatOrder(2, 1, targetPrice, 75));
        engine.orderHistory.add(creatOrder(3, 1, 10, 200));
        engine.orderHistory.add(creatOrder(4, 2, targetPrice, startQuantity + 2 * diffQuantity));
        Order argumanOrder = creatOrder(5, 1, targetPrice, 1);
        Assertions.assertEquals(0,
                engine.addOrderAndGetFraudulentQuantity(argumanOrder));
    }

}
