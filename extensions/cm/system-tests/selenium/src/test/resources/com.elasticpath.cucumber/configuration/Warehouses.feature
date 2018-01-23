@smoketest @configuration @warehouse
Feature: Warehouse

  Scenario: Create and delete warehouse
	Given I sign in to CM as admin user
	And I go to Configuration
	And I go to Warehouses
	And I create warehouse with following values
	  | warehouse name | Test            |
	  | address line 1 | 123 Main Street |
	  | city           | Los Angeles     |
	  | state          | California      |
	  | zip            | 12345           |
	  | country        | United States   |
	And I delete newly created warehouse
	Then newly created warehouse no longer exists

