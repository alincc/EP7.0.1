@discounts
Feature: Cart discounts
  As a client developer
  I want to retrieve the cart total discount
  so that I could display to the customer how much they are saving

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario Outline: Adding an item which is set for fifty percent off and then Ensure Cart Total Discount are displayed correctly.
    Given I add item <ITEM_NAME> to the cart
    When the line item triggering fifty percent off has purchase price of $47.47
    Then the cart discount fields has amount: 23.74, currency: CAD and display: $23.74

  Examples:
  | ITEM_NAME                                 |
  | triggerprodforfiftyoffentirepurchasepromo |

  Scenario Outline: Shipping discount does not affect Cart Total
    Given I add item <ITEM_NAME> to the cart
   When the line item triggering shipping discount has purchase price of $10.00
   Then the cart total it is unaffected by the shipping discount and has value $10.00
   Then the cart discount fields has amount: 0.0, currency: CAD and display: $0.00

   Examples:
   | ITEM_NAME                          |
   | productTriggering20PercentShipping |

Scenario: Can traverse back to cart from discount
   When I view a cart discount
   Then I can traverse back to the cart following a link