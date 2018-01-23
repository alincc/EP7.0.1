@Wishlists
Feature: Move item from wishlist to cart
  As a shopper
  I want to move items from my wishlist to cart
  so I can purchase them

  Background:
	Given I have authenticated as a newly registered shopper

  Scenario Outline: Move an item from default wishlist to cart
	Given I add item with code <ITEMCODE> to my default wishlist
	When I move item with code <ITEMCODE> to my cart with quantity 1
	Then item with code <ITEMCODE> is in my cart with quantity 1
	And item with code <ITEMCODE> is not found in my default wishlist

	Examples:
	  | ITEMCODE                                   |
	  #   multisku item
	  | portable_tv_hdrent_sku                     |
	  #   Singlesku item
	  | FocUSsku                                   |
	  #   Bundle item
	  | bundleWithPhysicalAndDigitalComponents_sku |
	  #   Digital item
	  | alien_sku                                  |
	  #   Dynamic Bundle
	  | tb_dyn12345sku                             |
	  # Back-Order item
	  | tt0926084_sku                              |
	  # Pre-Order item
	  | plantsVsZombies                            |

  Scenario Outline: Move an item from default wishlist to cart with valid quantity
	Given I add item with name digitalProduct to my default wishlist
	When I move item with name digitalProduct to my cart with quantity <QUANTITY>
	Then item with name digitalProduct is in my cart with quantity <QUANTITY>
	And item with name digitalProduct is not found in my default wishlist

	Examples:
	  | QUANTITY |
	  | 1        |
	  | 3        |

  Scenario Outline: Cannot move an item from wishlist to cart with an invalid quantity
	Given I add item with name digitalProduct to my default wishlist
	When I move item with name digitalProduct to my cart with quantity <QUANTITY>
	Then the HTTP status is bad request
	And I should see validation error message with error type, debug message, and field
	  | errorID                     | debugMessage                                                        | fieldName |
	  | field.invalid.minimum.value | 'quantity' value '<QUANTITY>' must be greater than or equal to '1'. | quantity  |
	And item with name digitalProduct is in my default wishlist

	Examples:
	  | QUANTITY |
	  | 0        |
	  | -1       |

  Scenario Outline: Cannot move an item from wishlist to cart with a non-integer quantity
	Given I add item with name digitalProduct to my default wishlist
	When I move item with name digitalProduct to my cart with quantity <QUANTITY>
	Then the HTTP status is bad request
	And I should see validation error message with error type, debug message, and field
	  | errorID                      | debugMessage                         | fieldName |
	  | field.invalid.integer.format | 'quantity' value must be an integer. | quantity  |
	And item with name digitalProduct is in my default wishlist

	Examples:
	  | QUANTITY      |
	  | 0.1           |
	  | invalidFormat |

  Scenario: Quantity of item moved from wishlist to cart is additive
	Given I add item with name digitalProduct to my default wishlist
	And item with name digitalProduct already exists in my cart with quantity 2
	When I move item with name digitalProduct to my cart with quantity 1
	Then item with name digitalProduct is in my cart with quantity 3
	And item with name digitalProduct is not found in my default wishlist


  Scenario Outline: Moving configurable item from wishlist to cart should be possible
	Given I add item with code <ITEMCODE> to my default wishlist
	When I view my default wishlist
	Then item with code <ITEMCODE> is in my default wishlist
	When I move configurable itemcode <ITEMCODE> from wishlist to my cart with quantity <QTY>
	  | giftCertificate.message        | <MESSAGE>         |
	  | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
	  | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
	  | giftCertificate.senderName     | <SENDER_NAME>     |
	Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
	  | giftCertificate.message        | <MESSAGE>         |
	  | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
	  | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
	  | giftCertificate.senderName     | <SENDER_NAME>     |
	And item with code <ITEMCODE> is not found in my default wishlist

	Examples:
	  | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
	  | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |

  Scenario Outline: Moving configurable item from wishlist to cart with invalid configuration should not be possible
	Given I add item with code <ITEMCODE> to my default wishlist
	When I view my default wishlist
	Then item with code <ITEMCODE> is in my default wishlist
	When I move configurable itemcode <ITEMCODE> from wishlist to my cart with quantity <QTY>
	  | giftCertificate.message        | <MESSAGE>         |
	  | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
	  | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
	  | giftCertificate.senderName     | <SENDER_NAME>     |
	Then the HTTP status is bad request
	And I should see validation error message with error type, debug message, and field
	  | errorID        | debugMessage                                        | fieldName                      |
	  | field.required | 'giftCertificate.recipientEmail' value is required. | giftCertificate.recipientEmail |
	  | field.required | 'giftCertificate.recipientName' value is required.  | giftCertificate.recipientName  |
	  | field.required | 'giftCertificate.senderName' value is required.     | giftCertificate.senderName     |

	Examples:
	  | ITEMCODE   | MESSAGE | RECIPIENT_EMAIL | RECIPIENT_NAME | SENDER_NAME | QTY |
	  | berries_20 |         |                 |                |             | 1   |

  Scenario Outline: Move an item with a promotion from default wishlist to cart
	Given I add item with code <ITEMCODE> to my default wishlist
	When I move item with code <ITEMCODE> to my cart with quantity 1
	Then item with code <ITEMCODE> is in my cart with quantity 1
	And item with code <ITEMCODE> is not found in my default wishlist
	And I go to my default cart
	And there is a list of applied promotions on the cart
  # the cart promotion
	And the list of applied promotions contains promotion <PROMOTION>

	Examples:
	  | ITEMCODE                                      | PROMOTION              |
	  | triggerprodforfiftyoffentirepurchasepromo_sku | FiftyOffEntirePurchase |