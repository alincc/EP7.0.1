@discounts
Feature: Cart sub total discount by adding an item which is set for 10% off
  As a client developer
  I want to retrieve the cart discount which is 10% off on cart subtotal
  so that I could display to the customer how much they are saving

  Scenario Outline:  Can Retrieve Cart Total Discount and applied promotion element
    Given I am logged in as a public user
    When I look up and add item code <ITEM_SKU> to my cart
    Then the cart discount fields has amount: 5.55, currency: CAD and display: $5.55
    And I can see applied promotion shows <PROMOTION_DISPLAY_NAME>

    Examples:
      | ITEM_SKU    | PROMOTION_DISPLAY_NAME  |
      | sony_bt_sku | 10% off Cart Total      |