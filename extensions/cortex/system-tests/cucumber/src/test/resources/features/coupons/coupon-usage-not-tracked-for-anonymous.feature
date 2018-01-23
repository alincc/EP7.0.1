@Coupons
Feature: Coupons usage is not tracked against anonymous users.

  Background:
    Given I have no coupons applied to an order

  Scenario Outline: A coupon used by one anonymous user is not auto-applied to another
    Given I login as a public user
    And I apply a coupon code <COUPON> that has not been previously applied to the order
    When I retrieve the coupon info for the order
    Then there is exactly 1 applied to the order
    When I transition to a public shopper
    And I retrieve the coupon info for the order
    Then there are exactly 0 applied to the order

    Examples:
      | COUPON                                      |
      | couponCodeLimitedByUserFor25PercentDiscount |
