@Addresses
Feature: Address created as public user is transferred to the newly created user

  Scenario:
    Given I login as a public user
    When I add item Twilight to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    And I register and transition to a new user
    Then I should see 1 element on addresses
    And address element 1 is identical to the public user's address