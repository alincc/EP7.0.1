@smoketest @catalogManagement @product
Feature: Create Product

  Background:
	Given I sign in to CM as admin user

  Scenario: Create new digital product for existing category
	When I go to Catalog Management
	When I create new product with following attributes
	  | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | shippableType | priceList               | listPrice |
	  | Mobile Catalog | Accessories | Product     | Movies      | DIGITAL | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | Digital Asset | Mobile Price List (CAD) | 111.00    |

	When I delete the newly created product
	Then I verify product is deleted