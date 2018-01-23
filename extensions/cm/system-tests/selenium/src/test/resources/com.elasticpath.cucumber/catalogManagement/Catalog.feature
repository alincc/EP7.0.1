@smoketest @catalogManagement @catalog
Feature: Create Catalog

  Background:
	Given I sign in to CM as admin user

  Scenario Outline: Create new catalog
	When I go to Catalog Management
	And I create new catalog <CATALOG_NAME> with langauge <language>
	Then newly created catalog is in the list
	When I select newly created catalog in the list
	And I can delete newly created catalog
	Then I verify newly created catalog is deleted

	Examples:
	  | CATALOG_NAME  | language |
	  | ATest Catalog | English  |

  Scenario Outline: Add / Edit / Delete catalog attribute
	And I go to Catalog Management
	And I create new catalog <CATALOG_NAME> with langauge <language>
	And I open the newly created catalog editor
	When I select Attributes tab in the Catalog Editor
	And I create a new catalog attribute with name Prod Description for Product of type Short Text with required true
	Then newly created catalog attribute is in the list
	When I edit the catalog attribute name
	Then updated catalog attribute is in the list
	When I close the editor and try to delete the newly created catalog
	Then I should see following error messages
	  | <error-message-1> |
	  | <error-message-2> |
	When I open the newly created catalog editor
	And I delete the newly created catalog attribute
	Then I verify newly created catalog attribute is deleted
	When I close the editor and delete the newly created catalog
	Then I verify newly created catalog is deleted

	Examples:
	  | CATALOG_NAME  | language | error-message-1                        | error-message-2       |
	  | ATest Catalog | English  | Unable to delete the following catalog | The catalog is in use |

  Scenario Outline: Add / Edit / Delete category type
	And I go to Catalog Management
	And I create new catalog <CATALOG_NAME> with langauge <language>
	And I open the newly created catalog editor
	When I select CategoryTypes tab in the Catalog Editor
	And I create a new category type Cat Type with following attributes
	  | Category Description |
	  | Name                 |
	Then newly created category type is in the list
	When I edit the category type name
	Then updated category type is in the list
	When I close the editor and try to delete the newly created catalog
	Then I should see following error messages
	  | <error-message-1> |
	  | <error-message-2> |
	When I open the newly created catalog editor
	And I delete the newly created category type
	Then I verify newly created category type is deleted
	When I close the editor and delete the newly created catalog
	Then I verify newly created catalog is deleted

	Examples:
	  | CATALOG_NAME  | language | error-message-1                        | error-message-2       |
	  | ATest Catalog | English  | Unable to delete the following catalog | The catalog is in use |