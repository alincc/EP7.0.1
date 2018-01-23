@smoketest @promotionsShipping @promotion
Feature: Promotion Browse

  Background:
	Given I sign in to CM as admin user

  Scenario: Browse promotion
	When I go to Promotions and Shipping
	And I click Search button in Promotion tab
	Then Promotion Search Results should contain following promotions
	  | bogo5off      |
	  | Free Shipping |


