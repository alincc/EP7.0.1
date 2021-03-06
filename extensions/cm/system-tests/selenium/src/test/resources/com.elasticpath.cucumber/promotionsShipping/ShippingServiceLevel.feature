@smoketest @promotionsShipping @shippingService
Feature: Shipping Service Level

  Background:
	Given I sign in to CM as admin user

  Scenario: Search shipping service level
	When I go to Promotions and Shipping
	And I click Search button in Shipping Service Levels tab
	Then Shipping Service Level Search Results should contain following service level codes
	  | FedExExpress |
	  | RM011        |

  Scenario Outline: Create, read, update and delete shipping service level
	When I go to Promotions and Shipping
	And I create shipping service level with following values
	  | store           | SearchStore           |
	  | shipping region | USA                   |
	  | carrier         | Fed Ex                |
	  | name            | Test Shipping Service |
	  | property value  | 25                    |
	Then I verify newly created shipping service level exists
	When I open the newly created shipping service level
	And I edit the shipping service level name to <UPDATED_NAME>
	And I click Search button in Shipping Service Levels tab
	Then Shipping Service Level Search Results should contain following service level names
	  | <UPDATED_NAME> |
	When I delete the newly created shipping service level
	Then I verify shipping service level is deleted

	Examples:
	  | UPDATED_NAME            |
	  | Edited Shipping Service |