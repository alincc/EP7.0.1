package com.elasticpath.cortex.dce.orders

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.orders.OrderConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def orderBillingAddressUri;
def defaultBillingAddressUri;


And(~'^Shopper gets the default billing address$') { ->
	client.getDefaultBillingAddress()
			.stopIfFailure()

	defaultBillingAddressUri = client.body.self.uri
}

And(~'^the shoppers order does not have a billing address applied$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.stopIfFailure()

	assertLinkDoesNotExist(client, BILLING_ADDRESS_LINK)
}
When(~'^I create a default billing address on the profile$') { ->
	client.createRandomAddress()
			.GET("/")
			.defaultprofile()
			.addresses()
			.billingaddresses()
			.default()
			.stopIfFailure()

	defaultBillingAddressUri = client.body.self.uri
}

When(~'^I retrieve the shoppers billing address info on the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.billingaddress()
			.stopIfFailure()

	orderBillingAddressUri = client.body.self.uri
}

Then(~'^the default billing address is automatically applied to the order$') { ->
	assertThat(orderBillingAddressUri)
			.as("Billing address is not as expected")
			.isEqualTo(defaultBillingAddressUri)
}

When(~'^I retrieve the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.stopIfFailure()
}

Then(~'^there is a needinfo link to (.+)$') { def resourceName ->
	assertThat(needInfoExistsWithName(resourceName))
			.as("Needinfo link for $resourceName not found")
			.isTrue()
}

And(~'^billing address is selected$') { ->
	client.createRandomAddress()
			.stopIfFailure()
}

Then(~'^there is no needinfo link to (.+)$') { resourceName ->
	assertThat(needInfoExistsWithName(resourceName))
			.as("Needinfo link for $resourceName was found")
			.isFalse()
}

Then(~'I use the selectaction') { ->
	client.selectaction()
}

Then(~'I post the selectaction') { ->
	client.selectaction()
			.follow()
			.stopIfFailure()
}

def needInfoExistsWithName(def name) {
	def found = false
	def startingPointUri = client.body.self.uri
	def listoflinks = client.body.links.findAll {
		link ->
			link.rel == "needinfo"
	}
	listoflinks.findResult {
		link ->
			client.GET(link.href).response

			//checks if current representation contains the info name being queried for
			if (client["name"] == name) {
				client.GET(startingPointUri)
				found = true
			}
	}
	client.GET(startingPointUri)
	return found
}