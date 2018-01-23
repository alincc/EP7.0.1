@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment total
	As a Shopper
	I want to see the total for a shipment
	so that I paid in total for that shipment

	Background:
		Given I login as a registered user

	Scenario: View shipment total
		When I add item Portable TV to the cart
		And I add item bundle with physical and multisku items to the cart
		And I select shipping option Canada Post Express
		And I make a purchase
		When I go to the purchases
		And I navigate to shipment
		And I follow the shipment total link
		Then I see the cost field has amount: 574.19, currency: CAD and display: $574.19
		And I can follow a link back to the shipment

