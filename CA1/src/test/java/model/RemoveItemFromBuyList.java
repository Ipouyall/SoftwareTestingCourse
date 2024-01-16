package model;

import exceptions.CommodityIsNotInBuyList;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveItemFromBuyListSteps {
    private Commodity commodity;
    private Exception exception;
    private User user;

    @Given("an anonymous user with the following buy list:")
    public void anonymousUserWithBuyList(List<List<String>> buyListData) {
        user = new User("username", "password", "email", "birthDate", "address");
        buyListData.forEach(row -> {
            String commodityId = row.get(0);
            int quantity = Integer.parseInt(row.get(1));
            for (int i = 0; i < quantity; i++) {
                Commodity commodity = new Commodity();
                commodity.setId(commodityId);
                user.addBuyItem(commodity);
            }
        });
    }


    @When("the user removes product with id {string} from the buy list")
    public void userRemovesFromBuyList(String commodityId) {
        this.commodity = new Commodity(commodityId, "Product", 10.0); // Assuming default values
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
        expectedBuyList.forEach(row -> {
            String commodityId = row.get(0);
            int expectedQuantity = Integer.parseInt(row.get(1));
            assertEquals(expectedQuantity, user.getBuyList().getOrDefault(commodityId, 0));
        });
    }

    @Then("a CommodityIsNotInBuyList exception should be thrown")
    public void commodityIsNotInBuyListExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof CommodityIsNotInBuyList);
    }
}
