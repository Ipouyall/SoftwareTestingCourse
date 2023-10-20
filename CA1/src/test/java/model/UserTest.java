package model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class UserTest {

    public static User createAnonymousUser() {
        return new User("ali", "123", "ali.n.hodaei@gmail.com",
                "2002-01-02 00:00:00", "Iran, Tehran");
    }

    public static User createUserWithCredit(String username, String password , String email, String birthDate,
                                            String address, float credit) {
        User user = new User(username, password, email, birthDate, address);
        user.setCredit(credit);
        return user;
    }
    
    public static User createUserWithBuyItem(String username, String password , String email, String birthDate,
                                             String address, String id, int quantity) {
        User user = new User(username, password, email, birthDate, address);
        Map<String, Integer> buyList = new HashMap<>();
        buyList.put(id, quantity);
        user.setBuyList(buyList);
        return user;
    }

    public static User createUserWithPurchasedItem(String username, String password , String email, String birthDate,
                                             String address, String id, int quantity) {
        User user = new User(username, password, email, birthDate, address);
        Map<String, Integer> purchasedList = new HashMap<>();
        purchasedList.put(id, quantity);
        user.setPurchasedList(purchasedList);
        return user;
    }

    @Test
    public void addCreditWithValidCreditTest() {
        fail("Incomplete test.");
    }

    @Test
    public void addCreditWithInvalidCreditTest() {
        fail("Incomplete test.");
    }

    @Test
    public void WithdrawCreditWithValidCreditTest() {
        fail("Incomplete test.");
    }

    @Test
    public void WithdrawCreditWithInvalidCreditTest() {
        fail("Incomplete test.");
    }

    @Test
    public void addBuyItemNewItemTest() {
        fail("Incomplete test.");
    }

    @Test
    public void addBuyItemExistItemTest() {
        fail("Incomplete test.");
    }

    @Test
    public void addPurchasedItemNewItemTest() {
        fail("Incomplete test.");
    }

    @Test
    public void addPurchasedItemExistItemTest() {
        fail("Incomplete test.");
    }

    @Test
    public void removeItemFromBuyListNewItemTest() {
        fail("Incomplete test.");
    }

    @Test
    public void removeItemFromBuyListExistItemTest() {
        fail("Incomplete test.");
    }


}
