@GiftCertificate
Feature: Delete gift certificate line item

  Scenario Outline: Delete gift certificate from cart lineitems
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
    When I delete item <ITEM_NAME> from my cart
    Then the list of cart lineitems is empty

    Examples:
      | ITEMCODE        | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | ITEM_NAME        |
      | berries_20      | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | Gift Certificate |
