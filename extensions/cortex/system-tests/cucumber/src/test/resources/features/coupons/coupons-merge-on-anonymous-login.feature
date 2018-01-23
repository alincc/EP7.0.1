@Coupons
Feature: Coupons merge on anonymous to registered transition

  Background:
	Given I login as a public user

  Scenario Outline: Shopper applies coupon as anonymous then logs in as registered
	Given I apply a coupon code <COUPON> to the order
    When I transition to registered user
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was applied

    Examples:
      | COUPON                                                   | NUMBER_OF_COUPONS |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 1                 |
