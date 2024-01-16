Feature: User's addCredit steps

  Scenario: Use valid positive amount
    Given a user with a credit balance of 50
    When the user adds $30 to their credit
    Then the new credit balance should be 80

  Scenario: Use invalid negative amount
    Given a user with a credit balance of 50
    When the user adds -20 to their credit
    Then an InvalidCreditRange exception should be thrown

  Scenario: Use valid zero amount shouldn't change anything
    Given a user with a credit balance of 50
    When the user adds 0 to their credit
    Then the new credit balance should be 50
