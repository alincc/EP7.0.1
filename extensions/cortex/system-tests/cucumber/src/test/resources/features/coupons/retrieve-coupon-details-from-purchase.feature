@Coupons @HeaderAuth
Feature: Retrieve Coupon Details From Purchase
  As a client developer
  I want to retrieve details of a coupon used on my purchase
  so that I could display the information to the customer

  Scenario Outline: Retrieve coupon details from a purchase
    Given I have the product <ACTIVE_PROMOTION_ITEM> with coupon <COUPON> applied to their purchase
    When I retrieve the coupon <COUPON> details of my purchase
    Then the code of the coupon <COUPON> is displayed

  Examples:
  | COUPON                                                   | ACTIVE_PROMOTION_ITEM              |
  | CouponWillApply10PercentOffTheProductWith10PercentCoupon | Product With 10 Percent Off Coupon |