@Coupons @COR-2612
Feature: Apply Coupon to Order
  As a client developer
  I want to apply a coupon to an order
  so that I could allow my customer to apply coupons to qualify for certain promotion

  Background: 
    Given I have no coupons applied to an order

  Scenario Outline: Apply a new coupon to an order
    When I apply a coupon code <COUPON> that has not been previously applied to the order
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was applied

    Examples:
      | COUPON                                                   | NUMBER_OF_COUPONS |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 1                 |

  Scenario Outline: Apply an existing coupon to an order
    When I apply a coupon code <COUPON> that has already been applied
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was applied

    Examples:
      | COUPON                                                   | NUMBER_OF_COUPONS |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 1                 |

  Scenario Outline: Attempt to apply an invalid coupon to an order
    When I apply an invalid coupon <INVALID_COUPON> to the order
    Then the coupon is not accepted

    Examples:
      | INVALID_COUPON     |
      | invalidCouponCode  |

  Scenario Outline: Coupon code is not case sensitive
    When I re-apply coupon code <COUPON> in lower case to the order
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order
    And the coupon <COUPON> is the one that was applied

    Examples:
      | COUPON                                                   | NUMBER_OF_COUPONS |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 1                 |

