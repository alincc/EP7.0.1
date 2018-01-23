package com.elasticpath.rest.ws.fixture.alias

import cucumber.api.DataTable

import static org.junit.Assert.assertTrue
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common cart commands that can be used as aliases.
 */
class Cart {

	def client

	def Cart(client) {
		this.client = client;
		client.alias(this.&addToCart)
		client.alias(this.&addTestItemToCart)
		client.alias(this.&clearCart)
		client.alias(this.&findCartElementByDisplayName)
		client.alias(this.&findCartElementBySkuCode)
		client.alias(this.&findCartElementBySkuCodeAndConfigurableFieldValues)
	}

	def addToCart(quantity) {
		client.addtocartform()
				.addtodefaultcartaction(quantity: quantity)
	}

	def addTestItemToCart(categoryName, displayName) {
		client.GET("/")
				.navigations()
				.findElement {
			category ->
				category["name"] == categoryName
		}
		.items()
				.findItemByDisplayName(displayName)
				.addToCart(1)
	}

	def clearCart() {
		client.GET("/")
				.defaultcart()
				.lineitems()
				.body.links.findAll {
			link ->
				if (link.rel == "element") {
					client.DELETE(link.uri)
				}
		}
		client.GET("/")
				.defaultcart()
	}

	def findCartElementByDisplayName(displayName) {
		def itemExists = false
		def elementResponse = null

		client.body.links.find {
			if (it.rel == "element") {
				client.GET(it.uri)
				elementResponse = client.save()
				client.item()
						.definition()
				if (client["display-name"] == displayName) {
					itemExists = true
				}
				//TODO Should also handle the failure case.
			}
		}
		assertThat(itemExists)
			.as("Item not found for item name - " + displayName,)
			.isEqualTo(true)
		client.resume(elementResponse)
	}

	def findCartElementBySkuCode(skuCode) {
		def itemExists = false
		def elementResponse = null

		client.body.links.find {
			if(it.rel == "element") {
				client.GET(it.uri)
				elementResponse = client.save()
				client.item()
						.code()
				if(client["code"] == skuCode) {
					itemExists = true
				} else {
					itemExists = false
				}
			}
		}
		assertThat(itemExists)
				.as("Item not found for skuCode - " + skuCode)
				.isTrue()
		client.resume(elementResponse)
	}
// This can be used in cart or wishlist
	def findCartElementBySkuCodeAndConfigurableFieldValues(skuCode, DataTable dataTable) {
		def itemExists = false
		def wishListElementResponse = null
		def configValueMatch = false
		def configMatch = true
		def failureKey
		def failureValue

		client.body.links.find {
			configMatch = true
			if (it.rel == "element") {
				client.GET(it.uri)
				wishListElementResponse = client.save()
				client.item()
				client.code()
				if (client["code"] == skuCode) {
					itemExists = true
				} else {
					itemExists = false
				}
			}
			assertThat(itemExists)
					.as("Item not found for skuCode - " + skuCode)
					.isTrue()

			client.resume(wishListElementResponse)
			def mapList = dataTable.asMap(String, String)

			for (def map : mapList) {
				configValueMatch = false;
				def key = map.getKey()
				def value = map.getValue()
				if (client.body.'configuration'."$key" == value) {
					configValueMatch = true
				} else {
					configValueMatch = false
					configMatch = false
					failureKey = key
					failureValue = value
				}
			}
			if(configValueMatch && configMatch)
				return true
		}
		assertTrue("unable to find configurable field: $failureKey or/and value: $failureValue", configMatch)
		assertTrue("Configurable field values not match", configValueMatch)
	}
}