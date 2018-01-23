package com.elasticpath.cortex.dce

import com.elasticpath.CucumberDTO.StructuredError
import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertItemConfiguration
import static org.assertj.core.api.Assertions.assertThat;

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'I add the item to the cart with quantity (.+) and configurable fields:$') { String itemQty, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	client.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}

And(~'^Structured error message contains:') { DataTable responsesTable ->
	def List<String> responseMessages = responsesTable.asList(String)

	assertThat(client.body.messages.size())
			.as("The number of error messages is not as expected")
			.isEqualTo(responseMessages.size())
	client.body.messages.each { message ->
		assertThat(responseMessages.contains(message["debug-message"]))
				.as("The structured message " + message["debug-message"] + " was not expected")
				.isTrue()
	}
}

/**
 * Verifies structured error message by passing in a DataTable object mapping to the Cucumber DTO class
 * that stores the attributes.
 */
Then(~'I should see validation error message with error type, debug message, and field$') { DataTable error ->
	def structuredErrorList = error.asList(StructuredError)

	/**
	 * First loop is looping the data table coming from feature.
	 * Second loop is looping each message.
	 */
	for (StructuredError structureError : structuredErrorList) {
		boolean messageExists = false;
		client.body.messages.each { message ->
			if (message.data.'field-name' == structureError.getFieldName()) {
				assertThat(structureError.getErrorID())
						.as("Error Id is not as expected")
						.isEqualTo(message.'id')
				assertThat(structureError.getDebugMessage())
						.as("Debug Message is not as expected")
						.isEqualTo(message.'debug-message')
				messageExists = true
				return true
			}
		}
		assertThat(messageExists)
				.as("Unable to retrieve expected error - " + structureError.getFieldName())
				.isTrue()
	}
}

Then(~'I should see validation error message with error type and debug message') { DataTable error ->
	def structuredErrorList = error.asList(StructuredError)

	/**
	 * First loop is looping the data table coming from feature.
	 * Second loop is looping each message.
	 */
	for (StructuredError structureError : structuredErrorList) {
		boolean messageExists = false;
		client.body.messages.each { message ->
			if (structureError.getErrorID() == message.'id') {
				assertThat(structureError.getDebugMessage())
						.as("Debug Message is not as expected")
						.isEqualTo(message.'debug-message')
				messageExists = true
				return true
			}
		}
		assertThat(messageExists)
				.as("Unable to retrieve expected error - " + structureError.getErrorID())
				.isTrue()
	}
}

Then(~'^the cart lineitem with itemcode (.+) has quantity (.+) and configurable fields as:$') { String itemSkuCode, String qty, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	client.findCartElementBySkuCode(itemSkuCode)
			.stopIfFailure()

	assertThat(client.body.'quantity'.toString())
			.as("Line item quantity does not match for itemcode - " + itemSkuCode)
			.isEqualTo(qty)

	assertItemConfiguration(itemDetailsTable)
}

Then(~'^I should see wishlist line item configurable fields for itemcode (.+) as:$') { String itemSkuCode, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultwishlist()
			.lineitems()
			.stopIfFailure()

	client.findCartElementBySkuCode(itemSkuCode)
			.stopIfFailure()

	assertItemConfiguration(itemDetailsTable)
}

Then(~'I should see wishlist line item (.+) with configurable field values as:$') { String itemSkuCode, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultwishlist()
			.lineitems()
			.stopIfFailure()
	client.findCartElementBySkuCodeAndConfigurableFieldValues(itemSkuCode, itemDetailsTable)
			.stopIfFailure()
}

Then(~'^I should see the new wishlist line item configurable fields as:$') { DataTable itemDetailsTable ->
	assertItemConfiguration(itemDetailsTable)
}

When(~'^I update (.+) in cart with Quantity: (.+) and Configurable Fields:$') { String itemSkuCode, String itemQty, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	def lineitemUri = findLineitemUriBySkuCode(itemSkuCode)
	client.PUT(lineitemUri, [
			"quantity"   : itemQty,
			configuration: configurationFields
	])
}

private String findLineitemUriBySkuCode(String itemSkuCode) {
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	client.findCartElementBySkuCode(itemSkuCode)
			.stopIfFailure()
	return client.body.self.uri
}