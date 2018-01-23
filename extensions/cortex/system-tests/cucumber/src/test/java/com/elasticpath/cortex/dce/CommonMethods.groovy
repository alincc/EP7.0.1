package com.elasticpath.cortex.dce

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static org.assertj.core.api.Assertions.assertThat
import static com.elasticpath.cortex.dce.SharedConstants.ELEMENT_LINK

/**
 * Shared methods.
 */
class CommonMethods {
	static String getLineItemUriForItemName(String itemDisplayName) {
		def lineitemUri

		client.findElement { element ->
			lineitemUri = client.body.self.uri
			client.item()
					.definition()
			assertThat(client["display-name"])
					.as("Cannot find item for the given item name.")
					.isEqualTo(itemDisplayName)
		}
		.stopIfFailure()
		return lineitemUri
	}

	static String getLineItemUriForItemCode(String itemCode) {
		def lineitemUri

		client.findElement { element ->
			lineitemUri = client.body.self.uri
			client.item()
					.code()
			assertThat(client["code"])
					.as("Cannot find item for the given item code.")
					.isEqualTo(itemCode)
		}
		.stopIfFailure()
		return lineitemUri
	}

	static void verifyLineitemsContainElementWithDisplayName(String itemDisplayName) {
		assertThat(getLineitemNamesList())
				.as("The lineitems do not contain the expected item.")
				.contains(itemDisplayName)
	}

	static void verifyLineitemsContainElementWithCode(String itemCode) {
		assertThat(getLineitemCodesList())
				.as("The lineitems do not contain the expected item.")
				.contains(itemCode)
	}

	static void verifyLineitemsNotContainElementWithDisplayName(String itemDisplayName) {
		assertThat(getLineitemNamesList())
				.as("The lineitems contain the unexpected item.")
				.doesNotContain(itemDisplayName)
	}

	static void verifyLineitemsNotContainElementWithCode(String itemCode) {
		assertThat(getLineitemCodesList())
				.as("The lineitems contain the unexpected item.")
				.doesNotContain(itemCode)
	}

	static List getLineitemNamesList() {
		def lineItemNames = []

		client.body.links.findAll {
			if (it.rel == "element") {
				client.GET(it.uri)
				client.item()
						.definition()
				lineItemNames.add(client["display-name"])
			}
		}
		return lineItemNames
	}

	static List getLineitemCodesList() {
		def lineItemCodes = []

		client.body.links.findAll {
			if (it.rel == "element") {
				client.GET(it.uri)
				client.item()
						.code()
				lineItemCodes.add(client["code"])
			}
		}
		return lineItemCodes
	}

	static void verifyNumberOfElements(int numElements) {
		def elements = client.body.links.findAll { link ->
			link.rel == ELEMENT_LINK
		}
		assertThat(elements)
				.as("Expected number of elements not match.")
				.hasSize(numElements)
	}
}
