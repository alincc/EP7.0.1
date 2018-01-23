@Carts
Feature: Cart access is restricted to the owner
  As a client developer
  I want to restrict access to carts
  so that users cannot view another user's cart

  Background:
    Given I login as a registered user
    And capture the uri of the registered user's cart

  Scenario: Cart access is restricted to the logged in user - public user
    When I am logged in as a public user
    And I attempt to view another user's cart
    Then I am not able to view the cart

  Scenario: Cart access is restricted to the logged in user - registered user
    When I have authenticated as a newly registered shopper
    And I attempt to view another user's cart
    Then I am not able to view the cart

  Scenario: Add to cart is restricted to the logged in user
    When I am logged in as a public user
    And attempt to add to another user's cart
    Then I am not able to view the cart