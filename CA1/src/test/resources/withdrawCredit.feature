Feature: Credit Withdrawal Feature

  Scenario: Withdrawal with Sufficient Credit
    Given a user with a credit balance of 1000.0
    When the user withdraws 500.0
    Then the credit balance should be 500.0

  Scenario: Withdrawal with Insufficient Credit
    Given a user with a credit balance of 200.0
    When the user withdraws 500.0
    Then an InsufficientCredit exception should be thrown

  Scenario: Withdrawal with Negative Amount
    Given a user with a credit balance of 1000.0
    When the user withdraws -200.0
    Then an InvalidWithdrawAmount exception should be thrown
