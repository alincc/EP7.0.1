@changeset @cscatalog
Feature: Virtual catalog in change set

  Background:
	Given I sign in to CM as admin user

  @lockAndFinalize
  Scenario Outline: Add delete virtual catalog with change set
	And I go to Change Set
	And I create a new change set <change_set_name>
	And I go to Catalog Management
	And I select newly created change set
	When I create new virtual catalog <catalog_name> with langauge <language>
	Then I should see newly created virtual catalog in the change set
	When I lock the latest change set
	Then the change set status should be Locked
	When I finalize the latest change set
	Then the change set status should be Finalized
	When I create a new change set <change_set_name>
	And I go to Catalog Management
	And I select newly created change set
	And I select newly created virtual catalog in the list
	And I click add item to change set button
	And I select newly created virtual catalog in the list
	And I can delete newly created virtual catalog
	Then I should see deleted virtual catalog in the change set

	Examples:
	  | catalog_name          | language | change_set_name |
	  | ATest Virtual Catalog | English  | ChangeSetCI     |