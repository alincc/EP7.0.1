package com.elasticpath.cortex.dce.discounts

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost
import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def PURCHASE_URI

Then(~'the line item triggering (.+) has purchase price of (.+)$') { def discount, def value ->
	client.GET("/")
			.defaultcart()
			.total()
	assertThat(client.body.cost[0]["display"])
			.as("Purchase price is not as expected")
			.isEqualTo(value)
}

Then(~'^the cart discount fields has amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->
		client.GET("/")
				.defaultcart()
				.discount()

		def listDiscountElement = client.body.discount
		assertCost(listDiscountElement, expectedAmount, expectedCurrency, expectedDisplayName)

}

Then(~'the cart total it is unaffected by the shipping discount and has value (.+)$') { def value ->
	client.GET("/")
			.defaultcart()
			.total()

	assertThat(client.body.cost[0]["display"])
			.as("Cart total is not as expected")
			.isEqualTo(value)
}

Then(~'I view a cart discount$') { ->
	client.GET("/")
			.defaultcart()
			.discount()
			.stopIfFailure()
}

Then(~'I can traverse back to the cart following a link$') { ->
	client.cart()
			.stopIfFailure()
}

Then(~'I retrieve the purchase$') { ->
	client.submitPurchase()
			.follow()
			.stopIfFailure()
	PURCHASE_URI = client.body.self.uri
}

Then(~'the purchase discount (.+) has value (.+)$') { def field, def value ->
	client.GET(PURCHASE_URI)
	client.discount()
			.stopIfFailure()

	assertThat(client.body.discount[0][field])
			.as("Purchase discount value is not as expected")
			.isEqualTo(value)
}

Then(~'the purchase total reflects the discount and has (.+) of (.+)$') { def field, def value ->
	client.GET(PURCHASE_URI)
			.stopIfFailure()

	assertThat(client.body."monetary-total"[0][field])
			.as("Purchase total is not as expected")
			.isEqualTo(value)
}

Then(~'the purchase discount amount: (.+) currency: (.+) display: (.+)$') { def expectedAmount, def expectedCurrency, def expectedDisplayName ->
	client.GET(PURCHASE_URI)
	client.discount()
			.stopIfFailure()
	def costElement = client.body.'discount'
	assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the purchase total reflects the discount and is amount: (.+) currency: (.+) display: (.+)$') {
	def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET(PURCHASE_URI)
				.stopIfFailure()
		def costElement = client.body."monetary-total"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the line item (.+?) that triggered the total discount has a line extension amount: (.+) currency: (.+) display: (.+)$') {
	def itemName, def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET(PURCHASE_URI)
				.lineitems()
				.findElement {
			lineitem -> lineitem["name"] == itemName
		}
				.body
		def costElement = client.body."line-extension-amount"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the item (.+?) triggering the fifty percent off cart total has a cart line item cost amount: (.+) currency: (.+) display: (.+)$') {
	def itemName, def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET("/")
				.defaultcart()
				.lineitems()
				.findElement {
			lineitem ->
				def definition = lineitem.item().definition()
				definition["display-name"] == itemName
		}
		.total()
		def costElement = client.body."cost"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the item (.+?) that had a ten percent line item discount had a item list price of amount: (.+) currency: (.+) display: (.+)$') {
	def itemName, def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET("/")
				.defaultcart()
				.lineitems()
				.findElement {
			lineitem ->
				def definition = lineitem.item().definition()
				definition["display-name"] == itemName
		}
		.price()
		def costElement = client.body."list-price"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the item (.+?) that had a ten percent line item discount had a cart line item cost amount: (.+) currency: (.+) display: (.+)$') {
	def itemName, def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET("/")
				.defaultcart()
				.lineitems()
				.findElement {
			lineitem ->
				def definition = lineitem.item().definition()
				definition["display-name"] == itemName
		}
		.price()
		def costElement = client.body."purchase-price"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'the line item (.+?) that had a ten percent discount has a line extension amount: (.+) currency: (.+) display: (.+)$') {
	def itemName, def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		client.GET(PURCHASE_URI)
				.lineitems()
				.findElement {
			lineitem -> lineitem["name"] == itemName
		}
				.body
		def costElement = client.body."line-extension-amount"
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}