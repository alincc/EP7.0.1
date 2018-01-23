@Coupons
Feature: Remove Coupon From Order
  As a client developer
  I want to be able to remove an applied coupon from a customer's order
  so that I could enable customers the ability to remove coupons they no longer want applied on their order

  Background: 
    Given I have no coupons applied to an order

  Scenario Outline: Can remove applied coupon from order
    Given I apply a coupon code <COUPON> to the order
    When I remove the coupon <COUPON> from the order
    And I retrieve the coupon info for the order
    Then there is exactly <NUMBER_OF_COUPONS> applied to the order

    Examples:
      | COUPON                                                   | NUMBER_OF_COUPONS |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 0                 |
