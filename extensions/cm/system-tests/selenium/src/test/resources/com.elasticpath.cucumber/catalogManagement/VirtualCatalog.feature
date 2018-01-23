@smoketest @catalogManagement @catalog
Feature: Create Virtual Catalog

  Background:
	Given I sign in to CM as admin user

  Scenario Outline: Create virtual catalog
	When I go to Catalog Management
	And I create new virtual catalog <CATALOG_NAME> with langauge <language>
	Then newly created virtual catalog is in the list
	When I select newly created virtual catalog in the list
	And I can delete newly created virtual catalog
	Then I verify newly created virtual catalog is deleted

	Examples:
	  | CATALOG_NAME          | language |
	  | ATest Virtual Catalog | English  |
