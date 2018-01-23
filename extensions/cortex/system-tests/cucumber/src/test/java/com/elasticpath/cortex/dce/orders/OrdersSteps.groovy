package com.elasticpath.cortex.dce.orders

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.orders.OrderConstants.*

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkExists

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Given(~'^I have selected a credit card that would trigger a failed order$') { ->
	client.authRegisteredUserByName(CREDITCARD_SCOPE, CREDITCARD_SCOPE_SHOPPER)

	client.search(SEARCHABLE_PRODUCT)
			.addToCart(1)
			.stopIfFailure()

	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.findChoiceOrChosen {
		creditcard ->
			def description = creditcard.description()
			description["cardholder-name"] == "EXP_AUTH"
	}
	.stopIfFailure()
	selectIfNotAlreadySelected();

}

When(~'^I submit the order$') { ->
	client.submitPurchase()
			.stopIfFailure()
}

When(~'^I create an email for my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.emailinfo()
			.emailform()
			.createemailaction("email": TEST_EMAIL_VALUE)
			.stopIfFailure()
}

When(~'^I select only the billing address$') { ->
	configureBillingAddressToBeSelected()
	removeShippingAddressOnOrderIfItExists()
}

When(~'^I also select the shipping address') { ->
	client.selectAnyDestination()
			.stopIfFailure()
	selectShippingServiceLevel()
}

When(~'^I select only the shipping address$') { ->
	configureOnlyOneSelectedShippingAddressForUser()
}

Then(~'^my order fails with status (.+)$') { responseStatus ->
	assertThat(client.response.status.toString())
			.as("The http status is not as expected")
			.isEqualTo(responseStatus)
}

Then(~'^I am not be able to submit my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "submitorderaction")
}

And(~'^I am able to determine the reason is because of missing email information$') { ->
	def emailNeedInfoExists = client.body.links.findAll {
		link -> link.rel == "needinfo"
	}.any {
		link -> client.GET(link.uri)["name"] == "email-info"
	}
	assertThat(emailNeedInfoExists)
			.as("Email need info was not found")
			.isTrue()
}

When(~'^I add an address with country (.+) and region (.+)$') { country, subcountry ->
	client.addNewAddress(country, subcountry)
			.stopIfFailure()
}

And(~'^I retrieve the order taxes$') { ->
	client.GET("/")
			.defaultcart()
			.order()
	assertLinkExists(client, "tax")

	client.tax()
			.stopIfFailure()
}

Given(~'^I modify my payment method to another credit card$') { ->
	client.authRegisteredUserByName(CREDITCARD_SCOPE, CREDITCARD_SCOPE_SHOPPER)

	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.choice()
			.selectaction()
			.stopIfFailure()
}

Then(~'^my order succeeds$') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

Then(~'^the email is created and selected for my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.emailinfo()
			.email()
			.stopIfFailure()

	assertThat(client["email"])
			.as("Email on the order is not as expected")
			.isEqualTo(TEST_EMAIL_VALUE)
}

And(~'^the tax total on the order is (.+)$') { taxAmount ->
	assertThat(client["total"]["display"])
			.as("The tax total is not as expected")
			.isEqualTo(taxAmount)
}

Then(~'^the (.+) cost is (.+)$') { taxType, amount ->
	assertThat(client["cost"].find {
		it ->
			it.title == taxType
	}.display)
			.as("The cost is not as expected")
			.isEqualTo(amount)
}

Then(~'resolve the shipping-option-info needinfo') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.selector()
			.choice()
			.selectaction()
			.stopIfFailure()
}

Then(~'I select the (new|valid) shipping address') { def ignore ->
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.selector()
			.choice()
			.selectaction()
			.stopIfFailure()

}

Then(~'post to a created submitorderaction uri') { ->
	client.POST(client.body.self.uri.toString().replace("/form", ""), [:])
}

Then(~'post to a created addtodefaultcartaction uri') { ->
	def scope = client.body.self.uri.toString().split("/")[3]
	def itemID = client.body.self.uri.toString().split("/")[4]
	def postURI = "/carts/" + scope + "/default/lineitems/items/" + scope + "/" + itemID
	client.POST(postURI, [quantity: 1])
}

When(~'^I retrieve the purchase form$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.stopIfFailure()
}

private void selectIfNotAlreadySelected() {
	def selectAction = client.body.links.findAll {
		link ->
			link.rel == "selectaction"
	}

	if (selectAction.toList().size() > 0) {
		client.selectaction()
				.follow() // back to selector
	} else {
		client.selector()
	}
}

private void configureOnlyOneSelectedShippingAddressForUser() {
	client.createRandomAddress()
			.stopIfFailure()
	client.createRandomAddress()
			.stopIfFailure()
	client.selectAnyDestination()
			.stopIfFailure()
	selectShippingServiceLevel()

	// Delete the billing address on order since it was automatically set
	// when an address was created above.
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.billingaddress()
			.stopIfFailure()

	def billingAddressUri = client.body.self.uri
	client.DELETE(billingAddressUri)
}

private void selectShippingServiceLevel() {
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.shippingoptioninfo()
			.selector()
			.findChoice { shippingoption ->
		def description = shippingoption.description()
		description["name"] == "CanadaPostExpress"
	}
	.selectaction()
			.stopIfFailure()
}

private void configureBillingAddressToBeSelected() {
	client.createRandomAddress()
			.stopIfFailure();

	client.createRandomAddress()
			.stopIfFailure();

	client.selectAnyBillingInfo()
			.stopIfFailure();
}

private void removeShippingAddressOnOrderIfItExists() {
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.stopIfFailure()
	def link = client.body.links.find { link ->
		link.rel == "element"
	}
	if (link != null) {
		client.element()
				.destinationinfo()
				.destination()
				.stopIfFailure()

		def destinationUri = client.body.self.uri
		client.DELETE(destinationUri)
	}
}