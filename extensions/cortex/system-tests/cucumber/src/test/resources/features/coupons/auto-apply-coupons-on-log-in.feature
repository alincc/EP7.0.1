@Coupons
Feature: Coupons auto apply when shopper logs in.

  Scenario Outline: Auto apply saved coupon on login as registered user
    When I login to the registered user with email ID <AUTO_APPLIED_COUPON_CUSTOMER_ID>
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was auto applied

  Examples:
  | AUTO_APPLIED_COUPON_CUSTOMER_ID            | COUPON       | NUMBER_OF_COUPONS |
  | itest.auto.applied.coupons@elasticpath.com | AUTO_APPLIED | 1                 |

  Scenario Outline: Auto apply saved coupon on login from public to registered
    Given I login as a public user
    When I transition to the registered user with email ID <AUTO_APPLIED_COUPON_CUSTOMER_ID>
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was auto applied

    Examples:
      | AUTO_APPLIED_COUPON_CUSTOMER_ID            | COUPON       | NUMBER_OF_COUPONS |
      | itest.auto.applied.coupons@elasticpath.com | AUTO_APPLIED | 1                 |

  Scenario Outline: Auto apply saved coupon on new cart orders.
    Given I login to the registered user with email ID <AUTO_APPLIED_COUPON_CUSTOMER_ID>
    And I create a purchase for item <PURCHASED_ITEM> with saved coupon
    When I retrieve the coupon info for the new order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was auto applied

    Examples:
      | AUTO_APPLIED_COUPON_CUSTOMER_ID            | PURCHASED_ITEM            | COUPON       | NUMBER_OF_COUPONS |
      | itest.auto.applied.coupons@elasticpath.com | Product With No Discounts | AUTO_APPLIED | 1                 |