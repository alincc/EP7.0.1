package com.elasticpath.cortex.dce.customerSegments

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def final CUSTOMER_SEGMENT_CONTENT_RELATIVE_LOCATION = "images/gift_bag.gif"
def final USER_TRAITS_HEADER = "x-ep-user-traits"

When(~/^I submit request header with the user traits (.+?)$/) { String valueUserTraits ->
	client.headers.put(USER_TRAITS_HEADER, valueUserTraits)
}

Then(~'^the customer segment promotion discount is (.+)$') { String expectedDiscountDisplay ->
	client.cart()
			.discount()
			.stopIfFailure()
	def actualCustSegDiscountDisplay = client.body.discount[0].display
	assertThat(actualCustSegDiscountDisplay)
			.as("The customer segment promotion discount is not as expected.")
			.isEqualTo(expectedDiscountDisplay)
}

Then(~'^I view the customer segment dynamic content (.+)$') { String dynamicContentName ->
	client.GET("/slots/${DEFAULT_SCOPE}")
			.findElement {
		content ->
			content["name"] == dynamicContentName
	}
}

Then(~'^the customer segment dynamic content is displayed') { ->
	assertThat(client.body.'relative-location')
			.as("The customer segment dynamic content is not displayed")
			.isEqualTo(CUSTOMER_SEGMENT_CONTENT_RELATIVE_LOCATION)
}

Then(~'^the customer segment dynamic content is not displayed') { ->
	assertThat(client.body.'relative-location')
			.as("the customer segment dynamic content is displayed")
			.isEqualTo(null)
}

When(~'^I go to item price') { ->
	client.price()
}

And(~/^the line-item has list amount: (.+?), currency: (.+?) and display: (.+?)$/) {
	String listAmount, String listCurrency, String listDisplay ->
		def listPrice = client.body.'list-price'
		assertCost(listPrice, listAmount, listCurrency, listDisplay)
}

And(~/^the line-item has purchase amount: (.+?), currency: (.+?) and display: (.+?)$/) {
	String purchaseAmount, String purchaseCurrency, String purchaseDisplay ->
		def purchasePrice = client.body.'purchase-price'
		assertCost(purchasePrice, purchaseAmount, purchaseCurrency, purchaseDisplay)
}

Then(~'^the item (.+) list and purchase price is (.+)$') { String itemName, String expectedDiscountDisplay ->
	client.GET("/")
			.defaultcart()
	client.lineitems()
	client.findCartElementByDisplayName(itemName)
	client.price()

	def listPrice = client.body.'list-price'[0].display
	def purchasePrice = client.body.'purchase-price'[0].display
	assertThat(listPrice)
			.as("The list price is not as expected")
			.isEqualTo(expectedDiscountDisplay)
	assertThat(purchasePrice)
			.as("The purchase price is not as expected")
			.isEqualTo(expectedDiscountDisplay)

}
