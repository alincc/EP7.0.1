package com.elasticpath.rest.ws.fixture.alias

/**
 * Shopper registration commands that can be used as aliases.
 */
class Register {
	
	def client

	def Register(client) {
		this.client = client
		client.alias(this.&registerShopper)
	}

	def registerShopper(registrationScope, familyName, givenName, password, username) {
		client.authAsAPublicUserOnScope(registrationScope)
		
		client.GET("registrations/$registrationScope/newaccount/form")
			.registeraction("family-name": familyName, "given-name": givenName, "password":password, "username": username)
	}
}