@smoketest @catalogManagement @bundle
Feature: Create Bundle

  Background:
	Given I sign in to CM as admin user

  Scenario: Create new bundle for existing category
	When I go to Catalog Management
	And I create new bundle with following attributes
	  | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductCode1 | bundleProductCode2 |
	  | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt64464fn          | tt0162661          |
	And I delete the newly created bundle
	Then I verify bundle is deleted
