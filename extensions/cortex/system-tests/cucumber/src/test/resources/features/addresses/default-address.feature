@Addresses
Feature: No default addresses for public or newly registered user

  Scenario: No Billing address found for public user
    Given I login as a public user
    When I get the default billing address
    Then the HTTP status is not found

  Scenario: No Billing address found for newly registered user
    Given I have authenticated as a newly registered shopper
    When I get the default billing address
    Then the HTTP status is not found

  Scenario: No shipping address found for public user
    Given I login as a public user
    When I get the default shipping address
    Then the HTTP status is not found

  Scenario: No shipping address found for newly registered user
    Given I have authenticated as a newly registered shopper
    When I get the default shipping address
    Then the HTTP status is not found

  Scenario: Can get default billing address
    Given I authenticate as a registered customer harry.potter@elasticpath.com with the default scope
    When I get the default billing address
    Then the field address contains value 1234 Hogwarts Avenue

  Scenario: Can get default shipping address
    Given I authenticate as a registered customer harry.potter@elasticpath.com with the default scope
    When I get the default shipping address
    Then the field address contains value 1234 Hogwarts Avenue