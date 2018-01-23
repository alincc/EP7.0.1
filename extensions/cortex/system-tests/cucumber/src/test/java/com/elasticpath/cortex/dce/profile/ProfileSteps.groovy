package com.elasticpath.cortex.dce.profile

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def newUserName

When(~'^I create a new shopper profile with family-name (.+), given-name (.+), password (.+), and unique user name$') {
	String familyName, String givenName, String password ->
		newUserName = UUID.randomUUID().toString() + "@elasticpath.com"
		client.registerShopper(DEFAULT_SCOPE, familyName, givenName, password, newUserName)
				.stopIfFailure()
}

When(~'^I authenticate with newly created shopper$') { ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, newUserName)
			.stopIfFailure()
}

When(~'^I create a new shopper profile with family-name (.*), given-name (.*), password (.*), and existing user name (.+)$') {
	String familyName, String givenName, String password, String existingUserName ->
		client.registerShopper(DEFAULT_SCOPE, familyName, givenName, password, existingUserName)
				.stopIfFailure()
}

Then(~'^I should see my profile name as family-name (.+) and given-name (.+)$') { String familyName, String givenName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()
	assertThat(client["family-name"])
			.as("Family name is not as expected")
			.isEqualTo(familyName)
	assertThat(client["given-name"])
			.as("Given name is not as expected")
			.isEqualTo(givenName)
}

/**
 * The OOTB Cortex behaviours is that in order to update email, user needs to use createemailaction
 * which will replace the old email value.
 */
When(~'^(?:I create my email id|I update my email id with new email) and I can see the new email id in my profile$') {
	->
	def userName = UUID.randomUUID().toString() + "@elasticpath.com"
	client.GET("/")
			.defaultprofile()
			.emails()
			.emailform()
			.createemailaction("email": userName)
			.follow()
			.stopIfFailure()
	assertThat(client["email"])
			.as("The email is not as expected")
			.isEqualTo(userName)
}

When(~'^I create invalid email (.*)$') { String email ->
	client.GET("/")
			.defaultprofile()
			.emails()
			.emailform()
			.createemailaction("email": email)
			.stopIfFailure()
}

When(~'^I update my profile family-name (.*) and given-name (.*)$') { String familyName, String firstName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri
	client.PUT(profileURI, [
			"family-name": familyName,
			"given-name" : firstName
	])
}

When(~'^I authenticate as another user and attempt to update the other users profile family-name (.+) and given-name (.+)') { String familyName, String firstName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri

	client.authAsRegisteredUser()
	client.PUT(profileURI, [
			"family-name": familyName,
			"given-name" : firstName
	])
}

When(~'^I POST to registration with body (.+)') { String jsonInput ->
	client.GET("/")
			.newaccountform()
			.stopIfFailure()
	client.POST("registrations/mobee/newaccount/form", jsonInput)
			.stopIfFailure()
}

When(~'^I PUT to profile with json body (.+)') { String jsonInput ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri
	client.PUT(profileURI, jsonInput)
			.stopIfFailure()
}