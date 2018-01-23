@smoketest @customerService @order
Feature: Order Return and Refund

  Scenario Outline: Return digital item
	Given I have an order for scope mobee with following skus
	  | skuCode    | quantity |
	  | <sku-code> | 1        |
	When I sign in to CM as admin user
	And I go to Customer Service
	And I search and open order editor for the latest order
	And I select Details tab in the Order Editor
	When I create return for digital item with quantity 1 for sku <sku-code>
	And I select Payments tab in the Order Editor
	Then I should see transaction type Credit in the Payment History
	Examples:
	  | sku-code        |
	  | plantsVsZombies |

  Scenario Outline: Return physical item
	Given I have an order for scope <scope> with following skus
	  | skuCode    | quantity |
	  | <sku-code> | 1        |
	When I sign in to CM as admin user
	And I go to Customer Service
	And I search and open order editor for the latest order
	And I complete the order shipment
	And I select Details tab in the Order Editor
	When I create return for physical item with quantity 1 for sku <sku-code>
	And I select Payments tab in the Order Editor
	Then I should see transaction type Credit in the Payment History

	Examples:
	  | scope | sku-code                |
	  | mobee | handsfree_shippable_sku |

  Scenario Outline: Create Refund
	Given I have an order for scope <scope> with following skus
	  | skuCode    | quantity |
	  | <sku-code> | 1        |
	And I sign in to CM as admin user
	And I go to Customer Service
	And I search and open order editor for the latest order
	And I complete the order shipment
	And I select Details tab in the Order Editor
	When I create a refund with following values
	  | Currency Code  | CAD             |
	  | Refund Amount  | 10.00           |
	  | Refund Note    | discount        |
	  | Payment Source | test token name |
	And I select Payments tab in the Order Editor
	Then I should see transaction type Credit in the Payment History

	Examples:
	  | scope | sku-code                |
	  | mobee | handsfree_shippable_sku |