package model;

import exceptions.CommodityIsNotInBuyList;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveItemFromBuyListSteps {
    private Exception exception;
    private ShoppingCart shoppingCart;

    // method to create a shopping cart with initial buy list
    public static ShoppingCart createShoppingCartWithBuyList() {
        ShoppingCart cart = new ShoppingCart();
        Commodity commodity1 = new Commodity("1", "Product A", 10.0);
        Commodity commodity2 = new Commodity("2", "Product B", 15.0);
        cart.addItemToBuyList(commodity1, 2);
        cart.addItemToBuyList(commodity2, 3);
        return cart;
    }

    @Given("a shopping cart with the following buy list:")
    public void shoppingCartWithBuyList(String dataTable) {
        shoppingCart = new ShoppingCart();
        dataTable.asLists().forEach(row -> {
            String commodityId = row.get(0);
            int quantity = Integer.parseInt(row.get(1));
            String commodityName = row.get(2);
            double commodityPrice = Double.parseDouble(row.get(3));
            Commodity commodity = new Commodity(commodityId, commodityName, commodityPrice);
            shoppingCart.addItemToBuyList(commodity, quantity);
        });
    }


    @When("the user removes {int} {string} from the buy list")
    public void userRemovesFromBuyList(int quantity, String commodityId) {
        try {
            Commodity commodity = new Commodity(commodityId, "Product", 10.0);
            shoppingCart.removeItemFromBuyList(commodity, quantity);
        } catch (CommodityIsNotInBuyList e) {
            this.exception = e;
        } catch (Exception e) {
            fail();
        }
    }


    @Then("the buy list should be:")
    public void buyListShouldBe(String dataTable) {
        dataTable.asLists().forEach(row -> {
            String commodityId = row.get(0);
            int expectedQuantity = Integer.parseInt(row.get(1));
            assertEquals(expectedQuantity, shoppingCart.getBuyList().getOrDefault(commodityId, 0));
        });
    }

    @Then("a CommodityIsNotInBuyList exception should be thrown")
    public void commodityIsNotInBuyListExceptionShouldBeThrown() {
        assertNotNull(exception);
        assertTrue(exception instanceof CommodityIsNotInBuyList);
    }
}
