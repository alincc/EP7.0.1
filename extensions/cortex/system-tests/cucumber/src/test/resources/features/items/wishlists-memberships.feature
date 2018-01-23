@Items @Wishlists
Feature: Wishlist memberships
  As a client developer,
  I can retrieve wishlist memberships for an item,
  so I can display if this item is included in any wishlists

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario: An item always has a link to wishlistmemberships, even if the item is not in my default wishlist
    Given item with name digitalProduct is not found in my default wishlist
    When I retrieve the item details for digitalProduct
    Then there is a wishlistmemberships link
    And the list of wishlistmemberships has 0 elements

  Scenario: wishlistmemberships list is not empty if an item is in a wishlist
    Given I add item with name digitalProduct to my default wishlist
    When I retrieve the item details for digitalProduct
    Then there is a wishlistmemberships link
    And the list of wishlistmemberships has 1 element
