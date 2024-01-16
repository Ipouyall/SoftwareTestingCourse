package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import exceptions.InvalidWithdrawAmount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserScenarioSteps {
    private Commodity commodity;
    private Exception exception;
    private User user;

    public static User createAnonymousUser() {
        return new User("ali", "123", "ali.n.hodaei@gmail.com",
                "2002-01-02 00:00:00", "Iran, Tehran");
    }


    @Given("an anonymous user with the following buy list:")
    public void anonymousUserWithBuyList(List<List<String>> buyListData) {
        user = new User("username", "password", "email", "birthDate", "address");
        Map<String, Integer> buyList = new HashMap<>();
        buyListData.forEach(row -> {
            String commodityId = row.get(0);
            int quantity = Integer.parseInt(row.get(1));
            buyList.put(commodityId, quantity);
//            for (int i = 0; i < quantity; i++) {
//                Commodity commodity = new Commodity();
//                commodity.setInStock(10);
//                commodity.setId(commodityId);
//                try {
//                    user.addBuyItem(commodity);
//                } catch (NotInStock e) {
//                    throw new RuntimeException(e);
//                }
//            }
        });
        user.setBuyList(buyList);
    }


    @When("the user removes product with id {string} from the buy list")
    public void userRemovesFromBuyList(String commodityId) {
        this.commodity = new Commodity(); // Assuming default values
        commodity.setId(commodityId);
        commodity.setPrice(10);
        commodity.setName("P");
        try {
            user.removeItemFromBuyList(commodity);
        } catch (CommodityIsNotInBuyList e) {
            this.exception = e;
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Then("the buy list should be:")
    public void buyListShouldBe(List<List<String>> expectedBuyList) {
        if (expectedBuyList.isEmpty())
            assertTrue(user.getBuyList().isEmpty());
        else
            expectedBuyList.forEach(row -> {
                String commodityId = row.get(0);
                int expectedQuantity = Integer.parseInt(row.get(1));
                assertEquals(expectedQuantity, user.getBuyList().get(commodityId));
            });
    }

    @Then("a CommodityIsNotInBuyList exception should be thrown")
    public void commodityIsNotInBuyListExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof CommodityIsNotInBuyList);
    }

    @Given("a user with a credit balance of {float}")
    public void aUserWithCredit(float initialCredit) {
        user = createAnonymousUser();
        user.setCredit(initialCredit);
    }

    @When("the user withdraws {float}")
    public void userWithdraws(float withdrawnAmount) {
        try {
            user.withdrawCredit(withdrawnAmount);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("the credit balance should be {float}")
    public void creditBalanceShouldBe(float expectedCredit) {
        float actualCredit = user.getCredit();
        assertEquals(expectedCredit, actualCredit);
    }

    @Then("an InsufficientCredit exception should be thrown")
    public void insufficientCreditExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof InsufficientCredit);
    }

    @Then("an InvalidWithdrawAmount exception should be thrown")
    public void invalidWithdrawAmountExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof InvalidWithdrawAmount);
    }

    @When("the user adds {float} to their current credit balance")
    public void userAddsToCurrentCreditBalance(float amount) {
        try {
            user.addCredit(amount);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("the new credit balance should be {float}")
    public void newCreditBalanceShouldBe(float expectedCredit) {
        float actualCredit = user.getCredit();
        assertEquals(expectedCredit, actualCredit);
    }

    @Then("an InvalidCreditRange exception should be thrown")
    public void invalidCreditRangeExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof InvalidCreditRange);
    }
}
