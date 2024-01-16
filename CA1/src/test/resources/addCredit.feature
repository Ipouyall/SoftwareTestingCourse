Feature: User's addCredit steps

  Scenario: Add valid positive credit amount
    Given a user with a credit balance of 100.0
    When the user adds 50.0 to their current credit balance
    Then the new credit balance should be 150.0

  Scenario: Attempt to add invalid negative credit amount
    Given a user with a credit balance of 100.0
    When the user adds -50.0 to their current credit balance
    Then an InvalidCreditRange exception should be thrown

  Scenario: Add valid zero credit amount (shouldn't change anything)
    Given a user with a credit balance of 100.0
    When the user adds 0.0 to their current credit balance
    Then the new credit balance should be 100.0
