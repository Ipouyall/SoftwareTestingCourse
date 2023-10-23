package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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

    @ParameterizedTest
    @CsvSource({"100.47f,54f", "65f,45.5f", "0f,50f", "100000f,0f", "99999f,1.25f"})
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
    @ValueSource(floats = {-45f, -10.5f, -99999, -1})
    public void addCreditWithInvalidCreditTest(float increment) {
        User user = createAnonymousUser();

        // execute and verify
        assertThrows(InvalidCreditRange.class, () -> {
            user.addCredit(increment);
        });
    }

    @ParameterizedTest
    @CsvSource({"100.47f,54f", "65f,45.5f", "50f,50f", "100000f,0f", "99999f,1.25f"})
    public void withdrawCreditWithValidCreditTest(float initial_value, float decrease) throws InsufficientCredit {
        User user = createUserWithCredit("test", "123", "adas@test.com", "2000-01-01",
                "tehran", initial_value);
        assumeTrue(initial_value >= decrease);

        user.withdrawCredit(decrease);

        float after_decrease_credit = user.getCredit();
        assertEquals(decrease, initial_value - after_decrease_credit);
    }

    @ParameterizedTest
    @CsvSource({"25.5f,54f", "64f,145.5f", "0f,50f", "100000f,9999999f", "99999f,99999.1f"})
    public void withdrawCreditWithInvalidCreditTest(float initial_value, float decrease) {
        User user = createUserWithCredit("test", "123", "adas@test.com", "2000-01-01",
                "tehran", initial_value);
        assumeTrue(initial_value < decrease);

        assertThrows(InsufficientCredit.class, () -> {
            user.withdrawCredit(decrease);
        });
    }

    @RepeatedTest(5)
    public void addBuyItemNewItemTest(RepetitionInfo repetitionInfo) {
        User user = createAnonymousUser();
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);

        logger.log(Level.INFO, "Repetition #" + repetitionInfo.getCurrentRepetition() +
                ": commodity_id = " + commodity_id);

        user.addBuyItem(commodity);

        Map<String, Integer> userBuyList = user.getBuyList();
        assertEquals(1, userBuyList.get(commodity_id));
    }

    @ParameterizedTest
    @ValueSource(ints = {778, 1, 99999, 0})
    public void addBuyItemExistItemTest(int quantity) {
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, quantity);
        logger.log(Level.INFO, "quantity = " + quantity + ", commodity_id = " + commodity_id);

        user.addBuyItem(commodity);

        assertEquals(quantity + 1, user.getBuyList().get(commodity_id));
    }

    @ParameterizedTest
    @ValueSource(ints = {547, 1, 99999, 100, 1402, -10})
    public void addPurchasedItemNewItemTest(int quantity) {
        String id = RandomStringUtils.randomAlphanumeric(10);
        User user = createAnonymousUser();
        logger.log(Level.INFO, "quantity = " + quantity + ", id = " + id);

        user.addPurchasedItem(id, quantity);

        assertEquals(quantity, user.getPurchasedList().get(id));
    }

    @ParameterizedTest
    @CsvSource({"421,700", "1,1000", "0,10", "99999,99999", "5000,429", "440,-7"})
    public void addPurchasedItemExistItemTest(int initial_quantity, int increment_quantity) {
        String id = RandomStringUtils.randomAlphanumeric(10);
        User user = createUserWithPurchasedItem("test", "123", "adas@test.com",
                "2000-01-01", "tehran", id, initial_quantity);
        logger.log(Level.INFO, "initial_quantity = " + initial_quantity +
                ", increment_quantity = " + increment_quantity + ", id = " + id);

        user.addPurchasedItem(id, increment_quantity);

        assertEquals(initial_quantity + increment_quantity, user.getPurchasedList().get(id));
    }

    @RepeatedTest(5)
    public void removeItemFromBuyListNewItemTest(RepetitionInfo repetitionInfo) {
        User user = createAnonymousUser();
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);

        logger.log(Level.INFO, "Repetition #" + repetitionInfo.getCurrentRepetition() +
                ": commodity_id = " + commodity_id);

        assertThrows(CommodityIsNotInBuyList.class, () -> {
            user.removeItemFromBuyList(commodity);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {123, 2, 99999, 100, 1402})
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

    @RepeatedTest(5)
    public void removeItemFromBuyListExistItemWithQuantity1Test(RepetitionInfo repetitionInfo) throws CommodityIsNotInBuyList {
        String commodity_id = RandomStringUtils.randomAlphanumeric(10);
        Commodity commodity = new Commodity();
        commodity.setId(commodity_id);
        User user = createUserWithBuyItem("test", "123", "adas@test.com", "2000-01-01",
                "tehran", commodity_id, 1);
        logger.log(Level.INFO, "Repetition #" + repetitionInfo.getCurrentRepetition() +
                ", quantity = " + 1 + ", commodity_id = " + commodity_id);

        user.removeItemFromBuyList(commodity);

        assertFalse(user.getBuyList().containsKey(commodity_id));
    }


}
