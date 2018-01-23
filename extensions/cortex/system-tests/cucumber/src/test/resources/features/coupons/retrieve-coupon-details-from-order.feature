@Coupons
Feature: Retrieve Coupon Details From Order
  As a client developer
  I want to retrieve details of a coupon
  so that I could display the information to the customer

  Background: 
    Given I have no coupons applied to an order

  Scenario Outline:
    Given I apply a coupon code <COUPON> to the order
    When I retrieve the coupon <COUPON> details of my order
    Then the code of the coupon <COUPON> is displayed

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |