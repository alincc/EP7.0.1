@smoketest @promotionsShipping @promotion
Feature: Catalog Promotion

  Background:
	Given I sign in to CM as admin user

  Scenario: Create catalog promotion
	When I go to Promotions and Shipping
	And I create catalog promotion with following values
	  | catalog             | Search Catalog                   |
	  | name                | 10% off Catalog Promotion        |
	  | display name        | 10% off Mobile Catalog Promotion |
	  | condition menu item | Brand is []                      |
	  | discount menu item  | Get [] % off when currency is [] |
	  | discount value      | 10                               |
	Then I verify newly created catalog promotion exists
	When I disable newly created category promotion
	Then catalog promotion state should be Expired


