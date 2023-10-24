package model;

import exceptions.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    public static Logger logger = Logger.getAnonymousLogger();


    public static User createAnonymousUser() {
        return new User("ali", "123", "ali.n.hodaei@gmail.com",
                "2002-01-02 00:00:00", "Iran, Tehran");
    }

    public static User createUserWithCredit(String username, String password, String email, String birthDate,
                                            String address, float credit) {
        User user = new User(username, password, email, birthDate, address);
        user.setCredit(credit);
        return user;
    }

    public static User createUserWithBuyItem(String username, String password, String email, String birthDate,
                                             String address, String id, int quantity) {
        User user = new User(username, password, email, birthDate, address);
        Map<String, Integer> buyList = new HashMap<>();
        buyList.put(id, quantity);
        user.setBuyList(buyList);
        return user;
    }

    public static User createUserWithPurchasedItem(String username, String password, String email, String birthDate,
                                                   String address, String id, int quantity) {
        User user = new User(username, password, email, birthDate, address);
        Map<String, Integer> purchasedList = new HashMap<>();
        purchasedList.put(id, quantity);
        user.setPurchasedList(purchasedList);
        return user;
    }

    public static Commodity createFakeCommodityWithInStock(String id, int inStock) {
        Commodity fake_commodity = new Commodity();
        fake_commodity.setInStock(inStock);
        fake_commodity.setId(id);
        return fake_commodity;
    }


    @ParameterizedTest
    @CsvSource({
            "65f, 45.5f",
            "0f, 50f",
            "100000f, 0f"
    })
    @DisplayName("Test addCredit with multiple valid credit")
    public void addCreditWithValidCreditTest(float initial_value, float increment) throws InvalidCreditRange {
        // fixture setup
        User user = createUserWithCredit("asd", "123", "adas@test.com", "2000-01-01",
                "tehran", initial_value);
        // execute
        user.addCredit(increment);
        // verify
        float after_increment_credit = user.getCredit();
        assertEquals(increment, after_increment_credit - initial_value);
    }

    @ParameterizedTest
    @ValueSource(floats = {-45, -10.5f, -Float.MAX_VALUE})
    @DisplayName("Test addCredit with multiple Invalid credit")
    public void addCreditWithInvalidCreditTest(float increment) {
        User user = createAnonymousUser();

        // execute and verify
        assertThrows(InvalidCreditRange.class, () -> {
            user.addCredit(increment);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "100.47f, 0f",
            "65f, 45.5f",
            "50f, 50f"
    })
    @DisplayName("Test withdrawCredit with multiple initial value and valid amount to decrease")
    public void withdrawCreditWithValidCreditTest(float initial_value, float decrease) throws InsufficientCredit {
        User user = createUserWithCredit("test", "123", "adas@test.com", "2000-01-01",
                "tehran", initial_value);

        user.withdrawCredit(decrease);

        float after_decrease_credit = user.getCredit();
        assertEquals(decrease, initial_value - after_decrease_credit);
    }

    @ParameterizedTest
    @CsvSource({
            "0f, 50f",
            "99999f, 99999.1f"
    })
    @DisplayName("Test withdrawCredit with multiple initial value and Invalid amount to decrease")
    public void withdrawCreditWithInvalidCreditTest(float initial_value, float decrease) {
        User user = createUserWithCredit("test", "123", "adas@test.com", "2000-01-01",
                "tehran", initial_value);

        assertThrows(InsufficientCredit.class, () -> {
            user.withdrawCredit(decrease);
        });
    }

    @Test
    @DisplayName("Test addBuyItem with commodity that not added before")
    public void addBuyItemNewItemTest() throws NotInStock {
        User user = createAnonymousUser();
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity fake_commodity = createFakeCommodityWithInStock(commodity_id, 1);
        logger.log(Level.INFO, "Commodity_id = " + commodity_id);

        user.addBuyItem(fake_commodity);

        Map<String, Integer> userBuyList = user.getBuyList();
        assertEquals(1, userBuyList.get(commodity_id));
    }

    @ParameterizedTest
    @ValueSource(ints = {778, 1, Integer.MAX_VALUE})
    @DisplayName("Test addBuyItem with commodity and quantity that exist before")
    public void addBuyItemExistItemTest(int quantity) throws NotInStock {
        int commodity_in_stock = 1;
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = createFakeCommodityWithInStock(commodity_id, commodity_in_stock);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, quantity);
        logger.log(Level.INFO, "quantity = " + quantity + ", commodity_id = " + commodity_id);

        user.addBuyItem(commodity);

        assertEquals(quantity + 1, user.getBuyList().get(commodity_id));
    }

    @ParameterizedTest
    @ValueSource(ints = {778, 1, Integer.MAX_VALUE})
    @DisplayName("Test addBuyItem with commodity that is out of stock")
    public void addBuyItemInvalidZeroInStockTest(int quantity) throws NotInStock {
        int commodity_in_stock = 0;
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);
        commodity.setInStock(commodity_in_stock);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, quantity);
        logger.log(Level.INFO, "quantity = " + quantity + ", commodity_id = " + commodity_id);

        assertThrows(NotInStock.class, () -> {
            user.addBuyItem(commodity);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1564, Integer.MIN_VALUE, 0})
    @DisplayName("Test addPurchasedItem with invalid quantity")
    public void addPurchasedItemInvalidQuantityTest(int quantity) {
        String id = RandomStringUtils.randomAlphanumeric(10);
        User user = createAnonymousUser();
        logger.log(Level.INFO, "quantity = " + quantity + ", id = " + id);

        assertThrows(InvalidQuantity.class, () -> {
            user.addPurchasedItem(id, quantity);
        });
    }


    @ParameterizedTest
    @ValueSource(ints = {547, 1, Integer.MAX_VALUE})
    @DisplayName("Test addPurchasedItem with valid quantity and item not exist before")
    public void addPurchasedItemNewItemTest(int quantity) throws InvalidQuantity {
        String id = RandomStringUtils.randomAlphanumeric(10);
        User user = createAnonymousUser();
        logger.log(Level.INFO, "quantity = " + quantity + ", id = " + id);

        user.addPurchasedItem(id, quantity);

        assertEquals(quantity, user.getPurchasedList().get(id));
    }

    @ParameterizedTest
    @CsvSource({
            "421,700",
            "0,10",
            "2147483646,1"
    })
    @DisplayName("Test addPurchasedItem with valid quantity and initial quantity for item exist before")
    public void addPurchasedItemExistItemTest(int initial_quantity, int increment_quantity) throws InvalidQuantity {
        String id = RandomStringUtils.randomAlphanumeric(10);
        User user = createUserWithPurchasedItem("test", "123", "adas@test.com",
                "2000-01-01", "tehran", id, initial_quantity);
        logger.log(Level.INFO, "initial_quantity = " + initial_quantity +
                ", increment_quantity = " + increment_quantity + ", id = " + id);

        user.addPurchasedItem(id, increment_quantity);

        assertEquals(initial_quantity + increment_quantity, user.getPurchasedList().get(id));
    }

    @Test
    @DisplayName("Test removeItemFromBuyList with item not exist before")
    public void removeItemFromBuyListNewItemTest() {
        User user = createAnonymousUser();
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);

        logger.log(Level.INFO, "Commodity_id = " + commodity_id);

        assertThrows(CommodityIsNotInBuyList.class, () -> {
            user.removeItemFromBuyList(commodity);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {123, 2, Integer.MAX_VALUE})
    @DisplayName("Test removeItemFromBuyList with item that has quantity (except 1) and exist before")
    public void removeItemFromBuyListExistItemNot1QuantityTest(int quantity) throws CommodityIsNotInBuyList {
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, quantity);
        logger.log(Level.INFO, "quantity = " + quantity + ", commodity_id = " + commodity_id);

        user.removeItemFromBuyList(commodity);

        assertEquals(quantity - 1, user.getBuyList().get(commodity_id));
    }

    @Test
    @DisplayName("Test removeItemFromBuyList with item that has quantity = 1 and exist before")
    public void removeItemFromBuyListExistItemWithQuantity1Test() throws CommodityIsNotInBuyList {
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, 1);
        logger.log(Level.INFO, "Quantity = " + 1 + ", commodity_id = " + commodity_id);

        user.removeItemFromBuyList(commodity);

        assertFalse(user.getBuyList().containsKey(commodity_id));
    }

}
