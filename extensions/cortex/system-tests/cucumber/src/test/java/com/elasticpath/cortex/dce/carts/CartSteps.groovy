package com.elasticpath.cortex.dce.carts

import cucumber.api.DataTable

import static com.elasticpath.cortex.dce.CommonMethods.verifyLineitemsNotContainElementWithDisplayName
import static com.elasticpath.cortex.dce.SharedConstants.*

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


When(~'I add single item (.+) to the cart$') { String productName ->
	addProductToCart(productName, 1)

	client.follow()
			.stopIfFailure()
}

When(~'I go to add to cart form$') { ->
	client.addtocartform()
			.stopIfFailure()

}

When(~'I add to cart with quantity of (.+)') { String quantity ->
	client.addtodefaultcartaction(
			["quantity": quantity
			])
			.stopIfFailure()
}

Given(~'^item (.+) does not have a price$') { String displayName ->
	client.search(displayName)
			.stopIfFailure()

	assertLinkDoesNotExist(client, "price")
}

When(~'I (?:add|have) item (.+) (?:to|in) the cart with quantity (.+)$') { String productName, int quantity ->
	addProductToCart(productName, quantity)
	client.follow()
			.stopIfFailure()
}

When(~'^I update (.+) Details: Message:(.+), RecipientEmail:(.+), RecipientName:(.+), SenderName:(.+) and Quantity:(.+)$') { String itemDisplayName, String msg, String recEmail, String recName, String senderName, String itemQty ->
	def lineitemUri = findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity     : itemQty,
			configuration: ["giftCertificate.message"       : msg,
							"giftCertificate.recipientEmail": recEmail,
							"giftCertificate.recipientName" : recName,
							"giftCertificate.senderName"    : senderName]
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}
When(~'^I change the lineitem quantity for (.+) to (.+)$') { String itemDisplayName, String newQuantity ->
	def lineitemUri = findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I delete item (.+) from my cart$') { String itemDisplayName ->
	def lineitemUri = findCartLineItemUriByDisplayName(itemDisplayName)
	client.DELETE(lineitemUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I view (.+) in the catalog$') { String itemDisplayName ->
	client.search(itemDisplayName)
}

When(~'^I add attempt to add (.+) with invalid quantity (.+)$') { String displayName, String InvalidQuantity ->
	client.search(displayName)
			.addtocartform()
			.stopIfFailure()

	def actionLink = client.body.links[0].uri

	client.POST(actionLink, [
			quantity: InvalidQuantity
	])
}

When(~'^I attempt to change the lineitem quantity for (.+) to (.+)$') { String itemDisplayName, String newQuantity ->
	def lineitemUri = findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])
}

Then(~'the items in the cart are ordered as follows$') { DataTable cartItemsTable ->
	def cartItems = cartItemsTable.asList(String)

	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	List elementLinks = new ArrayList();
	client.body.links.findAll {
		if (it.rel == "element") {
			elementLinks.add(it.uri)
		}
	}

	List<String> items = new ArrayList<>()
	for (String uri : elementLinks) {
		client.GET(uri)
				.item()
				.definition()
				.stopIfFailure()

		items.add((String) client.body[DISPLAY_NAME_FIELD])
	}
	assertThat(cartItems).containsExactlyElementsOf(items)
}

Then(~'the items in the zoomed cart are ordered as follows$') { DataTable cartItemsTable ->
	def cartItems = cartItemsTable.asList(String)

	client.GET(DEFAULT_CART_URL + CartConstants.ZOOM_LINE_ITEM_DEFINITION)
	List bodyElements = client.body._element

	List<String> items = new ArrayList<>()
	for (def element : bodyElements) {
		String itemName = element
				._item
				._definition[0]
				."display-name"[0]

		items.add(itemName)
	}
	assertThat(cartItems).containsExactlyElementsOf(items)


}

Then(~'capture the uri of the registered user\'s cart$') { ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	CART_URI = client.body.self.uri
}

Then(~'I attempt to view another user\'s cart$') { ->
	client.GET(CART_URI)
			.stopIfFailure()
}

Then(~'attempt to add to another user\'s cart$') { ->
	client.search("firstProductAddedToCart")
			.stopIfFailure()
	def itemURI = client.body.self.uri
	client.POST(CART_URI + "/lineitems" + itemURI, [
			quantity: 2
	])
}

Then(~'I am not able to view the cart$') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(403)
	client.follow()
}

Then(~'^the cart total-quantity is (.+)$') { String cartTotalQty ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	assertThat(client.body.'total-quantity'.toString())
			.as("Cart total quantity is not as expected")
			.isEqualTo(cartTotalQty)
}

And(~'^the cart lineitem quantity for (.+) is (.+)$') { String itemDisplayName, String quantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	client.findCartElementByDisplayName(itemDisplayName)
	assertThat(client.body.'quantity'.toString())
			.as("Cart line item quantity does not match.")
			.isEqualTo(quantity)
}

And(~'^the cart lineitem for item code (.+) has quantity of (.+)$') { String itemCode, String quantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	client.findCartElementBySkuCode(itemCode)

	assertThat(client.body.'quantity'.toString())
			.as("Cart line item quantity does not match.")
			.isEqualTo(quantity)
}

Then(~'^the list of cart lineitems is empty$') { ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	assertLinkDoesNotExist(client, ELEMENT_LINK)
}

Then(~'^I am not able to add the item to my cart$') { ->
	def itemUri = client.body.self.uri

	client.GET(itemUri)
			.addtocartform()
			.stopIfFailure()

	// Check that there is no addtodefaultcart link
	assertLinkDoesNotExist(client, "addtodefaultcartaction")
}

Then(~'^I am prevented from adding the item to the cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.addtocartform()
			.stopIfFailure()
	assertLinkDoesNotExist(client, "addtodefaultcartaction")
	def lineitemUri = client.body.self.uri
	client.POST(lineitemUri, [
			quantity: 1
	])
			.stopIfFailure()
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(403)
}

Then(~'^I am allowed to add to cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.addToCart(1)
			.follow()
			.stopIfFailure()
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(200)
}

When(~'^I add selected multisku item to the cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.item()
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()
}

When(~'I go to my cart$') { ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
}

Then(~'the number of cart lineitems is (.+)') { int numberOfLineitems ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	List lineItemElementList = new ArrayList();
	client.body.links.findAll {
		if (it.rel == "element") {
			lineItemElementList.add(it.uri)
		}
	}
	assertThat(numberOfLineitems)
			.as("Expected number of cart lineitems do not match.")
			.isEqualTo(lineItemElementList.size())
}

Then(~'^My default cart does not contain item name (.+)$') { String itemDisplayName ->
	def response = client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	verifyLineitemsNotContainElementWithDisplayName(itemDisplayName)
}

private addProductToCart(String triggerProduct, int quantity) {
	client.search(triggerProduct)
			.addToCart(quantity)
			.stopIfFailure()
}

private String findCartLineItemUriByDisplayName(String displayName) {
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	client.findCartElementByDisplayName(displayName)
			.stopIfFailure()
	return client.body.self.uri
}
