@Items @Carts
Feature: Carts memberships
  As a client developer,
  I can retrieve cart memberships for an item,
  so I can display if this item is included in any carts

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario: An item always has a link to cartmemberships, even if the item is not in my default cart
    Given My default cart does not contain item name digitalProduct
    When I retrieve the item details for digitalProduct
    Then there is a cartmemberships link
    And the list of cartmemberships has 0 elements

  Scenario: cartmemberships list is not empty if an item is in a cart
    Given I add digitalProduct to my default cart with quantity 1
    When I retrieve the item details for digitalProduct
    Then there is a cartmemberships link
    And the list of cartmemberships has 1 element
