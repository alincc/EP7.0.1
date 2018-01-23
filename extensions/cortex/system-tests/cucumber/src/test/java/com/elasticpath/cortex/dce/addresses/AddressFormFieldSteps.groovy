package com.elasticpath.cortex.dce.addresses

import cucumber.api.DataTable

import static com.elasticpath.cortex.dce.addresses.AddressConstants.goToAddress
import static com.elasticpath.cortex.dce.addresses.AddressConstants.goToAddressForm
import static com.elasticpath.cortex.dce.addresses.AddressConstants.ADDRESS_LINK_TYPE
import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'^Response\'s self link has type addresses$') { ->

	goToAddressForm()

	assertThat(client.body.self.type)
			.as("Self link is not as expected")
			.isEqualTo(ADDRESS_LINK_TYPE)
}

And(~'^link has no type$') { ->
	assertThat(client.body.links[0].type)
			.as("Link type is not as expected")
			.isEqualTo(null)
}

And(~'^link has rel (.+)') { String expectedRel ->
	assertThat(client.body.links[0].rel)
			.as("Link rel is not as expected")
			.isEqualTo(expectedRel)
}

And(~'^link does not have rev$') { ->
	assertThat(client.body.links[0].rev)
			.as("Link rev is not as expected")
			.isEqualTo(null)
}

And(~'^link has uri to addresses$') { ->
	def link_uri = client.body.links[0].uri
	def last_part = link_uri.split('/')[-1]
	assertThat(last_part)
			.as("Link is not as expected")
			.isEqualTo(DEFAULT_SCOPE)
}

When(~'^I create address with Country (.*), Extended-Address (.*), Locality (.*), Postal-Code (.*), Region (.*), Street-Address (.*), Family-Name (.*) and Given-Name (.*)$')
		{ String countryCode, String extendedAddress, String locale, String postalCode, String regionCode,
		  String streetAddress, String familyName, String givenName ->
			client.GET("/")
					.defaultprofile()
					.addresses()
					.addressform()
					.createaddressaction(address: ["country-name": countryCode, "extended-address": extendedAddress, "locality": locale, "postal-code": postalCode, "region": regionCode, "street-address": streetAddress], name: ["family-name": familyName, "given-name": givenName])
		}

When(~'^I modify the address with Country (.*), Extended-Address (.*), Locality (.*), Postal-Code (.*), Region (.*), Street-Address (.*), Family-Name (.*) and Given-Name (.*)$')
		{ String countryCode, String extendedAddress, String locale, String postalCode, String regionCode,
		  String streetAddress, String familyName, String givenName ->
			client.PUT(client.body.self.uri,
					[address: ["country-name": countryCode, "extended-address": extendedAddress, "locality": locale, "postal-code": postalCode, "region": regionCode, "street-address": streetAddress], name: ["family-name": familyName, "given-name": givenName]])
		}

When(~'^I create address with invalid address key with Country (.*), Locality (.*), Postal-Code (.*), Region (.*), Street-Address (.*), Family-Name (.*) and Given-Name (.*)$')
		{ String countryCode, String locale, String postalCode, String regionCode,
		  String streetAddress, String familyName, String givenName ->
			client.GET("/")
					.defaultprofile()
					.addresses()
					.addressform()
					.createaddressaction(invalidAddressKey: ["country-name": countryCode, "locality": locale, "postal-code": postalCode, "region": regionCode, "street-address": streetAddress], name: ["family-name": familyName, "given-name": givenName])
					.stopIfFailure()
		}

When(~'^I create address with invalid name key with Country (.*), Locality (.*), Postal-Code (.*), Region (.*), Street-Address (.*), Family-Name (.*) and Given-Name (.*)$')
		{ String countryCode, String locale, String postalCode, String regionCode,
		  String streetAddress, String familyName, String givenName ->
			client.GET("/")
					.defaultprofile()
					.addresses()
					.addressform()
					.createaddressaction(address: ["country-name": countryCode, "locality": locale, "postal-code": postalCode, "region": regionCode, "street-address": streetAddress], invalidNameKey: ["family-name": familyName, "given-name": givenName])
					.stopIfFailure()
		}


Then(~'I should see (.+) matches the following$') { def addressFormNode, DataTable dataTable ->
	goToAddress()
	client.element()
			.stopIfFailure()

	def mapList = dataTable.asMap(String, String)
	for (def map : mapList) {
		def key = map.getKey()
		def value = map.getValue()

		assertThat(client.body."$addressFormNode"."$key")
				.as("Expected $key does not match")
				.isEqualTo(value)
	}
}

Then(~'I should not see (.+) matches the following$') { def addressFormNode, DataTable dataTable ->
	goToAddress()
	client.element()
			.stopIfFailure()

	def mapList = dataTable.asMap(String, String)
	for (def map : mapList) {
		def key = map.getKey()
		def value = map.getValue()

		assertThat(client.body."$addressFormNode"."$key")
				.as("Expected $key does not match").isNotEqualTo(value)
	}
}

Then(~'the address matches the following$') { DataTable dataTable ->

	def mapList = dataTable.asMap(String, String)
	for (def map : mapList) {
		def key = map.getKey()
		def value = map.getValue()

		assertThat(client.body.address."$key")
				.as("Expected $key does not match")
				.isEqualTo(value)
	}
}
