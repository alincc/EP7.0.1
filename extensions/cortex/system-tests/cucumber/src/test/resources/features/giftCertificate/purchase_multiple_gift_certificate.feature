@GiftCertificate
Feature: Purchase multiple gift certificates

  Scenario Outline: Purchase multiple quantity of gift certificate and ensure purchase is successful and displays correct purchase amount
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
    And purchase item monetary total has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

  Examples:
  | ITEMCODE         | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    |  AMOUNT | CURRENCY  | DISPLAY_AMOUNT |
  | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 2   | COMPLETED |  40.0   | CAD       | $40.00         |

  Scenario Outline: Purchase multiple gift certificates and ensure purchase is successful and displays correct purchase amount
    Given I login as a public user
#   Adding first gift certificate
    When I look up an item with code <ITEMCODE_1>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |
    Then the cart lineitem with itemcode <ITEMCODE_1> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |

#   Adding second gift certificate
    When I look up an item with code <ITEMCODE_2>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |
    Then the cart lineitem with itemcode <ITEMCODE_2> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |

    When I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    And I go to the purchases
    Then I should see purchase status <STATUS>
    And purchase item monetary total has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEMCODE_1 | ITEMCODE_2      | MESSAGE     | RECIPIENT_EMAIL_1            | RECIPIENT_EMAIL_2 | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    | AMOUNT | CURRENCY  | DISPLAY_AMOUNT |
      | berries_20 | hummingbird_100 | Hello World | harry.potter@elasticpath.com | test@test.com     | Harry Potter   | MOBEE tester | 1   | COMPLETED | 120.0  | CAD       | $120.00        |