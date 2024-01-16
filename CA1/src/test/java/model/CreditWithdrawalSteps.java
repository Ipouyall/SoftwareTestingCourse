package model;

import exceptions.InsufficientCredit;
import exceptions.InvalidWithdrawAmount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;


public class CreditWithdrawalSteps {
    private Exception exception;

    private User user;

    public static User createAnonymousUser() {
        return new User("ali", "123", "ali.n.hodaei@gmail.com",
                "2002-01-02 00:00:00", "Iran, Tehran");
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
}