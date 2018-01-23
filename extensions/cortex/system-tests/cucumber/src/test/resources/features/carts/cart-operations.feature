@Carts
Feature: Cart operations
  As a shopper
  I want to be able to modify the contents of my shopping cart
  So that the cart only contains the items I want to purchase

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario: Shopper can add an item to their cart
    When I add item firstProductAddedToCart to the cart with quantity 2
    Then the cart total-quantity is 2
    And the cart lineitem quantity for firstProductAddedToCart is 2
    When I add item firstProductAddedToCart to the cart with quantity 1
    Then the cart total-quantity is 3
    And the cart lineitem quantity for firstProductAddedToCart is 3
    When I add item secondProductAddedToCart to the cart with quantity 1
    Then the cart total-quantity is 4
    And the cart lineitem quantity for secondProductAddedToCart is 1

  Scenario: Shopper can increment and decrement the quantity of a lineitem
    Given I add item firstProductAddedToCart to the cart with quantity 2
    When I change the lineitem quantity for firstProductAddedToCart to 1
    Then the cart total-quantity is 1
    And the cart lineitem quantity for firstProductAddedToCart is 1
    When I change the lineitem quantity for firstProductAddedToCart to 3
    Then the cart total-quantity is 3
    And the cart lineitem quantity for firstProductAddedToCart is 3

  Scenario: Changing the lineitem quantity to 0 deletes the item from the cart
    Given I add item firstProductAddedToCart to the cart with quantity 1
    When I change the lineitem quantity for firstProductAddedToCart to 0
    Then the list of cart lineitems is empty

  Scenario: Shopper can delete a lineitem from the cart
    Given I add item firstProductAddedToCart to the cart with quantity 10
    When I delete item firstProductAddedToCart from my cart
    Then the list of cart lineitems is empty

  Scenario: Shopper cannot add to cart a calculated bundle containing an item without price
    Given item SmartPhones Bundle does not have a price
    When I view SmartPhones Bundle in the catalog
    Then I am not able to add the item to my cart

  Scenario Outline: Shopper cannot add to cart with an invalid quantity
    When I add attempt to add firstProductAddedToCart with invalid quantity <QUANTITY>
    Then the HTTP status is bad request
    And the cart total-quantity is 0

    Examples:
      | QUANTITY      |
      | 0             |
      | -2            |
      | 0.1           |
      | invalidFormat |
      | 2147483648    |

  Scenario Outline: Shopper cannot update a cart lineitem quantity an invalid quantity
    Given I add item firstProductAddedToCart to the cart with quantity 1
    When I attempt to change the lineitem quantity for firstProductAddedToCart to <INVALID_QUANTITY>
    Then the HTTP status is bad request
    And the cart total-quantity is 1

    Examples:
      | INVALID_QUANTITY |
      | -1               |
      | 0.1              |
      | invalidFormat    |
      | 2147483648       |

  Scenario: An item can be added to cart with a max quantity value of 2^31-1
    Given I search for item name Hugo
    And I go to add to cart form
    When I add to cart with quantity of 2147483647
    Then the HTTP status is OK, created
    And the cart total-quantity is 2147483647

  Scenario: An item can be updated in the cart with a max quantity value of 2^31-1
    Given I search for item name Hugo
    And I go to add to cart form
    And I add to cart with quantity of 1
    And the HTTP status is OK, created
    And the cart total-quantity is 1
    When I attempt to change the lineitem quantity for Hugo to 2147483647
    Then the HTTP status is no content
    And the cart total-quantity is 2147483647