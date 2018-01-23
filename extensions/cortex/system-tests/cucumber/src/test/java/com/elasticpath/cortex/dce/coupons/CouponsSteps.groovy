/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.coupons

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Given(~'^I have no coupons applied to an order$') { ->
	client.authAsAPublicUser()
			.stopIfFailure()
}

When(~'^I login to the registered user with email ID (.+)$') { String userEmailId ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, userEmailId)
			.stopIfFailure()
}

When(~'^I transition to the registered user with email ID (.+)$') { String userEmailId ->
	client.roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, userEmailId)
			.stopIfFailure()
}

And(~'^I create a purchase for item (.+) with saved coupon$') { String productName ->
	client.search(productName)
			.addToCart(1)
			.follow()
			.stopIfFailure()
	client.submitPurchase()
			.stopIfFailure();
}

//matches:
//Shopper applies a coupon that has not been previously applied to the order
//Shopper applies a coupon to the order
When(~'^I apply a coupon code (.+?) (?:that has not been previously applied )*to the order$') { String couponCode ->
	client.applyCoupon(couponCode)
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

When(~'^I apply a coupon code (.+) that has already been applied$') { String couponCode ->
	client.applyCoupon(couponCode)
			.stopIfFailure()

	client.applyCoupon(couponCode)
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(200)
}

When(~'^I apply an invalid coupon (.+) to the order') { String couponCode ->
	client.applyCoupon(couponCode)
			.stopIfFailure()
}

When(~'^I re-apply coupon code (.+) in lower case to the order') { String couponCode ->
	client.applyCoupon(couponCode)
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)

	client.applyCoupon(couponCode.toLowerCase())
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(200)
}


And(~'^I retrieve the coupon info for the (?:new )*order$') { ->
	client.getCouponInfo()
			.stopIfFailure()
}

Then(~'^there (?:is|are) exactly (.+) applied to the (?:order|purchase)$') { def expectedNumberOfCoupons ->
	if (expectedNumberOfCoupons.toInteger() == 0) {
		assertLinkDoesNotExist(client, "coupon")
	} else {
		def newNumberOfCoupons = client.body.links.findAll { link ->
			link.rel == "coupon"
		}.size()
		assertThat(newNumberOfCoupons)
				.as("Number of coupons is not as expected")
				.isEqualTo(expectedNumberOfCoupons.toInteger())
	}
}

Then(~'^the coupon is not accepted') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(409)
}
//matches:
//the COUPON is the one that was applied
//the COUPON is the one that was auto applied
And(~'^the coupon (.+) is the one that was (?:auto )*applied$') { String couponCode ->
	def criteria = { coupon ->
		coupon["code"] == couponCode
	}

	client.findLink("coupon", criteria)
			.stopIfFailure()
}

Then(~'^the code of the coupon (.+) is displayed$') { String couponCode ->
	assertThat(client["code"])
			.as("Coupon code is not as expected")
			.isEqualTo(couponCode)
}

When(~'^I remove the coupon (.+) from the order$') { String couponCode ->
	client.removeCoupon(couponCode)
			.stopIfFailure()
}


When(~'^I retrieve the coupon (.+) details of my order$') { String couponCode ->
	def criteria = { coupon ->
		coupon["code"] == couponCode
	}

	client.getCouponInfo()
			.findLink("coupon", criteria)
			.stopIfFailure()
}

When(~'^Shopper retrieves the coupon info of their purchase$') { ->
	client.follow()
			.coupons()
			.stopIfFailure()
}

When(~'^I retrieve the coupon (.+) details of my purchase$') { String couponCode ->
	client.follow()
			.coupons()
			.findElement { coupon ->
		coupon["code"] == couponCode
	}
	.stopIfFailure()
}

Given(~'^Shopper has coupon (.+) applied to their order$') { String couponCode ->
	client.authAsAPublicUser()
			.applyCoupon(couponCode)
			.stopIfFailure()
}

Given(~'^I have the product (.+) with coupon (.+) applied to their purchase$') { String productName, String couponCode ->

	client.authAsRegisteredUser()
			.stopIfFailure()

	removeExistingCoupons();

	// for a purchase to have a coupon it must trigger a promotion.
	client.search(productName)
			.addToCart(1)
			.follow()
			.stopIfFailure()

	client.applyCoupon(couponCode)
			.stopIfFailure()

	client.submitPurchase()
			.stopIfFailure()
}

private removeExistingCoupons() {
//clean up if coupons exist on the cart
	client.getCouponInfo()
			.stopIfFailure()

	def coupons = client.body.links.findAll {
		link ->
			link.rel == "coupon"
	}

	for (def coupon : coupons) {
		client.DELETE(coupon.href)
	}
}