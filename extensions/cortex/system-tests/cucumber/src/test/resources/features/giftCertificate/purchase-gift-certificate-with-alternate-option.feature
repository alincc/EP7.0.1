@GiftCertificate
Feature: Purchase gift certificate with alternate option

  Scenario Outline: Purchase gift certificate with different design and amount and ensure the selected options retain at purchase.

    Given I login as a public user
    When I search for item name <ITEM_NAME>
    And I change the multi sku selection by <OPTION_1> and select choice <VALUE_1>
    And I change the multi sku selection by <OPTION_2> and select choice <VALUE_2>
    Then the item code is <GIFT_ITEM_SKU_CODE>
    When I follow a link back to the item
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <GIFT_ITEM_SKU_CODE> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    Then I view purchase line item option <OPTION_1>
    And I should see item option value is <VALUE_1>
    Then I view purchase line item option <OPTION_2>
    And I should see item option value is <VALUE_2>

    Examples:
      | ITEM_NAME        | OPTION_1  | VALUE_1 | OPTION_2 | VALUE_2     | GIFT_ITEM_SKU_CODE | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
      | Gift Certificate | Amount    | 100     | Design   | Hummingbird | hummingbird_100    | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |
