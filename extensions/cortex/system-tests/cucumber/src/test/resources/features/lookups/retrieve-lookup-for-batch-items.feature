@Lookups
Feature: Retrieve Lookup a batch of Items

  Scenario: Can find a batch of Items from a list of codes.
	Given I am logged in as a public user
    And I retrieve the batch items lookup form
    When I submit a batch of codes
    Then I find a batch of items
