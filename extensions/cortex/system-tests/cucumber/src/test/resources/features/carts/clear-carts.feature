@Carts @HeaderAuth
Feature: Clear Cart Items
  As the developer
  I want to ensure the customer cart is empty at the start of any new transactional session
  so that no unintended items are in the customers cart at checkout

  Background:
    Given I login as a registered user

  Scenario: Clear cart with single line item
    Given I add item firstProductAddedToCart to the cart with quantity 3
    And the total quantity in the cart is 3
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart

  Scenario: Clear cart with multiple line items
    Given I add single item firstProductAddedToCart to the cart
    And I add single item secondProductAddedToCart to the cart
    And I add single item thirdProductAddedToCart to the cart
    And the total quantity in the cart is 3
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart

  Scenario Outline: Clear cart with cart promotion item
    Given I add item <ITEM_NAME> to the cart
    And I go to my default cart
    And the list of applied promotions contains promotion <PROMOTION>
    And the cart total has amount: 47.47, currency: CAD and display: $47.47
    And the total quantity in the cart is 1
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart
    And the list of applied promotions is empty
    And the cart total has amount: 0.0, currency: CAD and display: $0.00

    Examples:
      | ITEM_NAME                                  | PROMOTION              |
      | triggerprodforfiftyoffentirepurchasepromo  | FiftyOffEntirePurchase |


  Scenario: Only the cart owner can clear the cart
    Given I add item firstProductAddedToCart to the cart with quantity 3
    And the total quantity in the cart is 3
    When I save the cart URI and login in as another user
    And I attempt to clear the first user's cart
    Then the delete will fail with a 403 status
    And the first user's cart is not cleared

