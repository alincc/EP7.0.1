@discountsInProgress @HeaderAuth
Feature: Purchase discounts
  As a client developer
  I want to retrieve the purchase total discount
  so that I could display to the customer how much they saved

  Background:
    Given I login as a registered user

  Scenario Outline: Adding an item which is set for fifty percent off and then Ensure Cart Total Discount are displayed correctly.
    Given I add item <ITEM_NAME> to the cart
    And the item <ITEM_NAME> triggering the fifty percent off cart total has a cart line item cost amount: 47.47 currency: CAD display: $47.47
    When I retrieve the purchase
    Then the line item <ITEM_NAME> that triggered the total discount has a line extension amount: 47.47 currency: CAD display: $47.47
    And the purchase discount amount: 23.74 currency: CAD display: $23.74
    And the purchase total reflects the discount and is amount: 26.58 currency: CAD display: $26.58

    Examples:
      | ITEM_NAME                                 |
      | triggerprodforfiftyoffentirepurchasepromo |


  Scenario Outline: Adding an item which has 10 percent line item discount.Ensure Line Item Discount are displayed correctyly
    Given I add item <ITEM_NAME> to the cart
    And the item <ITEM_NAME> that had a ten percent line item discount had a item list price of amount: 10.0 currency: CAD display: $10.00
    And the item <ITEM_NAME> that had a ten percent line item discount had a cart line item cost amount: 9.0 currency: CAD display: $9.00
    When I retrieve the purchase
    Then the line item <ITEM_NAME> that had a ten percent discount has a line extension amount: 9.0 currency: CAD display: $9.00
    And the purchase discount amount: 0.0 currency: CAD display: $0.00
    And the purchase total reflects the discount and is amount: 10.08 currency: CAD display: $10.08

    Examples:
      | ITEM_NAME                                 |
      | Product With Cart Lineitem Promo          |


  Scenario Outline: Adding 2 items, Item which is set for fifty percent off and item which has 10 percent line item discount.
  Ensure cart shows correct amount and Purchase also shows correct discount amounts
    Given I add item <ITEM_NAME> to the cart
    And I add item <ITEM_NAME2> to the cart
    And the item <ITEM_NAME> triggering the fifty percent off cart total has a cart line item cost amount: 47.47 currency: CAD display: $47.47
    And the item <ITEM_NAME2> that had a ten percent line item discount had a item list price of amount: 10.0 currency: CAD display: $10.00
    And the item <ITEM_NAME2> that had a ten percent line item discount had a cart line item cost amount: 9.0 currency: CAD display: $9.00
    When I retrieve the purchase
    Then the line item <ITEM_NAME> that triggered the total discount has a line extension amount: 47.47 currency: CAD display: $47.47
    Then the line item <ITEM_NAME2> that had a ten percent discount has a line extension amount: 9.0 currency: CAD display: $9.00
    And the purchase discount amount: 28.24 currency: CAD display: $28.24
    And the purchase total reflects the discount and is amount: 31.63 currency: CAD display: $31.63

    Examples:
      | ITEM_NAME                                 |  ITEM_NAME2                                |
      | triggerprodforfiftyoffentirepurchasepromo |  Product With Cart Lineitem Promo          |