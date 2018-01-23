@GiftCertificate
Feature: Update Gift Certificate details

  Scenario Outline: Update Gift Certificate details and ensure details persist
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
    When I update Gift Certificate Details: Message:<UPDATED_MESSAGE>, RecipientEmail:<UPDATED_RECIPIENT_EMAIL>, RecipientName:<UPDATED_RECIPIENT_NAME>, SenderName:<UPDATED_SENDER_NAME> and Quantity:<UPDATED_QTY>
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <UPDATED_QTY> and configurable fields as:
      | giftCertificate.message        | <UPDATED_MESSAGE>         |
      | giftCertificate.recipientEmail | <UPDATED_RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <UPDATED_RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <UPDATED_SENDER_NAME>     |
#    The following data tests:
#    Able to update all fields with PUT request and the updated values retain
#    Able to update individual field and the values retain
#    Able to update just the quantity field and not affecting the configurable items fields
#    Able to update fields without any change via PUT and the values retain the same
  Examples:
  | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY  | UPDATED_MESSAGE     | UPDATED_RECIPIENT_EMAIL       | UPDATED_RECIPIENT_NAME | UPDATED_SENDER_NAME | UPDATED_QTY |
  | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1    | Updated Hello World | oliver.harris@elasticpath.com | Oliver Harris          | tester MOBEE        | 2           |
  | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1    | Updated Hello World | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 1           |
  | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1    | Hello World         | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 2           |
  | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1    | Hello World         | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 1           |
