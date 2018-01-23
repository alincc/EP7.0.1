@Carts
Feature: Cart Lineitems Ordering
  The order in which items are added to the cart is preserved

  Background:
    Given I login as a public user

  Scenario: Cart items are ordered from oldest to newest
    When I add single item firstProductAddedToCart to the cart
    And I add single item secondProductAddedToCart to the cart
    And I add single item thirdProductAddedToCart to the cart
    Then the items in the cart are ordered as follows
      | firstProductAddedToCart  |
      | secondProductAddedToCart |
      | thirdProductAddedToCart  |

  Scenario: Cart items are ordered from oldest to newest in zoomed result
    When I add single item firstProductAddedToCart to the cart
    And I add single item secondProductAddedToCart to the cart
    And I add single item thirdProductAddedToCart to the cart
    Then the items in the zoomed cart are ordered as follows
      | firstProductAddedToCart  |
      | secondProductAddedToCart |
      | thirdProductAddedToCart  |
