@Billings @HeaderAuth @Purchases
Feature: View purchase billing address
	As a Shopper
	I want to see the address a shipment was dispatched to
	so that I Know where to collect my goods

	Background:
		Given I login as a registered user

	Scenario: View billing address
		When I add item Portable TV to the cart
		And I add item bundle with physical and multisku items to the cart
		And I select shipping option Canada Post Express
		And I make a purchase
		When I go to the purchases
		And I navigate to the billing address
		Then I see billing address
		And I can follow a link back to the purchase

