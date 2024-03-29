Feature: User removes items from the buy list

  Scenario: Remove a single item from the buy list
    Given an anonymous user with the following buy list:
      | 1            | 1        |
    When the user removes product with id "1" from the buy list
    Then the buy list should be:
      |

  Scenario: Remove multiple items of the same commodity from the buy list
    Given an anonymous user with the following buy list:
      | 2            | 3        |
    When the user removes product with id "2" from the buy list
    Then the buy list should be:
      | 2            | 2        |

  Scenario: Attempt to remove an item not present in the buy list
    Given an anonymous user with the following buy list:
      | 3            | 2        |
    When the user removes product with id "1" from the buy list
    Then a CommodityIsNotInBuyList exception should be thrown

  Scenario: Attempt to remove an item with quantity greater than 1
    Given an anonymous user with the following buy list:
      | 4            | 5        |
    When the user removes product with id "4" from the buy list
    Then the buy list should be:
      | 4            | 4        |
