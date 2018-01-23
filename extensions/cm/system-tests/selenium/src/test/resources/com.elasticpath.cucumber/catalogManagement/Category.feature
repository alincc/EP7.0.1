@smoketest @catalogManagement @category
Feature: Create Category

  Background:
	Given I sign in to CM as admin user

  Scenario Outline: Create category in existing catalog
	When I go to Catalog Management
	And I create new category for <catalog> with following data
	  | categoryName   | categoryType | storeVisible | attrLongTextName     | attrLongTextValue | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue |
	  | ATest Category | Movies       | true         | Category Description | <attribute-1>     | Category Rating | <attribute-2>    | Name              | <attribute-3>      |
	When I expand <catalog> catalog
	And I verify newly created category exists
	And I select newly created category
	And I open newly created category in editor
	And I select editor's Attributes tab
	Then it should have following category attribute
	  | <attribute-1> |
	  | <attribute-2> |
	  | <attribute-3> |
	When I delete newly created category
	Then newly created category is deleted

	Examples:
	  | catalog        | attribute-1               | attribute-2 | attribute-3        |
	  | Mobile Catalog | Test Category Description | 5.50        | Test Category Name |