@GiftCertificate
Feature: Purchase one gift certificate and ensure purchase is successful

	Scenario Outline: Purchase one gift certificate and ensure purchase is successful and displays correct gift certificate field values
		Given I login as a public user
		When I look up an item with code <ITEMCODE>
		And I go to add to cart form
		And I add the item to the cart with quantity <QTY> and configurable fields:
			| giftCertificate.message        | <MESSAGE>         |
			| giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
			| giftCertificate.recipientName  | <RECIPIENT_NAME>  |
			| giftCertificate.senderName     | <SENDER_NAME>     |
		Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
			| giftCertificate.message        | <MESSAGE>         |
			| giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
			| giftCertificate.recipientName  | <RECIPIENT_NAME>  |
			| giftCertificate.senderName     | <SENDER_NAME>     |
		When I fill in email needinfo
		And I fill in payment methods needinfo
		And I fill in billing address needinfo
		And I make a purchase
		And I go to the purchases
		Then I should see purchase status <STATUS>
		And I should not see any element under Shipment
		Then I should see purchase line item configurable fields for item <ITEM_NAME> as:
			| giftCertificate.message        | <MESSAGE>         |
			| giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
			| giftCertificate.recipientName  | <RECIPIENT_NAME>  |
			| giftCertificate.senderName     | <SENDER_NAME>     |

		Examples:
			| ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    | ITEM_NAME        |
			| berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | COMPLETED | Gift Certificate |
