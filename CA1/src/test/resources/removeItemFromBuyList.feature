Feature: User's removeItemFromBuyList steps

  Scenario: Remove multiple items from the buy list
    Given a shopping cart with the following buy list:
      | Commodity ID | Quantity |
      | 1            | 2        |
      | 2            | 3        |
    When the user removes 2 "Product A" from the buy list
    Then the buy list should be:
      | Commodity ID | Quantity |
      | 1            | 0        |
      | 2            | 3        |

  Scenario: Remove all items of a commodity from the buy list
    Given a shopping cart with the following buy list:
      | Commodity ID | Quantity |
      | 1            | 2        |
      | 2            | 3        |
    When the user removes 3 "Product B" from the buy list
    Then the buy list should be:
      | Commodity ID | Quantity |
      | 1            | 2        |
      | 2            | 0        |

  Scenario: Attempt to remove more items than available in the buy list
    Given a shopping cart with the following buy list:
      | Commodity ID | Quantity |
      | 1            | 2        |
      | 2            | 3        |
    When the user attempts to remove 4 "Product A" from the buy list
    Then a CommodityIsNotInBuyList exception should be thrown

  Scenario: Attempt to remove items of a commodity not present in the buy list
    Given a shopping cart with the following buy list:
      | Commodity ID | Quantity |
      | 1            | 2        |
      | 2            | 3        |
    When the user attempts to remove 2 "Product C" from the buy list
    Then a CommodityIsNotInBuyList exception should be thrown
