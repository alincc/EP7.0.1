@smoketest @customerService @customer
Feature: Customer Search

  Background:
	Given I sign in to CM as CSR user

  Scenario Outline: Search for customer
	And I go to Customer Service
	And I select Customers tab
	When I search for customer with email ID <email-id>
	Then I should see customer with email ID <email-id> in result list

	Examples:
	  | email-id                     |
	  | harry.potter@elasticpath.com |