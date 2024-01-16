package model;

import exceptions.InvalidCreditRange;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AddCreditSteps {
    private Exception exception;
    private User user;

    // Helper method to create an anonymous user
    public static User createAnonymousUser() {
        return new User("pouya", "123", "pouya.sadeghi@mail.com",
                "2002-01-02 00:00:00", "Iran, Tehran");
    }

    @Given("a user with a credit balance of {float}")
    public void aUserWithCredit(float initialCredit) {
        user = createAnonymousUser();
        user.setCredit(initialCredit);
    }

    @When("the user adds {float} to their credit")
    public void userAddsToCredit(float amount) {
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
