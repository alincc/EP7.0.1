package com.elasticpath.cortex.dce.addresses

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.addresses.AddressConstants.*

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I fill in billing address needinfo$') { ->

	client.createRandomAddress()
			.stopIfFailure()
}

And(~'^I fill in payment methods needinfo$') { ->

	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.paymenttokenform()
			.createpaymenttokenfororderaction(
			['display-name': TEST_TOKEN_DISPLAY_NAME,
			 'token'       : TEST_TOKEN]
	)
			.stopIfFailure()
}

And(~'^I fill in email needinfo$') { ->

	client.GET("/")
			.defaultcart()
			.order()
			.emailinfo()
			.emailform()
			.createemailaction("email": TEST_EMAIL_VALUE)
			.stopIfFailure()
}

And(~'^I should see (.+) element on addresses$') { int numberOfAddresses ->

	goToAddress()

	def elements = client.body.links.findAll { link ->
		link.rel == ELEMENT_LINK
	}

	assertThat(elements)
			.size()
			.as("Number of elements is not as expected")
			.isEqualTo(numberOfAddresses)
}

And(~'^address element (.+) is identical to the public user\'s address$') { int index ->

	goToAddress()

	def elements = client.body.links.findAll { link ->
		link.rel == ELEMENT_LINK
	}

	def element = elements[index - 1]
	client.GET(element.uri)

	assertThat(client.body.address.'country-name')
			.as("Country name is not as expected")
			.isEqualTo(DEFAULT_COUNTRY_NAME)
	assertThat(client.body.address.'locality')
			.as("Locality is not as expected")
			.isEqualTo(DEFAULT_LOCALITY)
	assertThat(client.body.address.'postal-code')
			.as("Postal code is not as expected")
			.isEqualTo(DEFAULT_POSTAL_CODE)
	assertThat(client.body.address.'region')
			.as("Region is not as expected")
			.isEqualTo(DEFAULT_POSTAL_REGION)
	assertThat(client.body.address.'street-address')
			.as("Street address is not as expected")
			.contains(DEFAULT_STREE_ADDRESS_SNIPPET)
	assertThat(client.body.name.'family-name')
			.as("Family name is not as expected")
			.isEqualTo(DEFAULT_FAMILY_NAME)
	assertThat(client.body.name.'given-name')
			.as("Given name is not as expected")
			.isEqualTo(DEFAULT_GIVEN_NAME)
}
