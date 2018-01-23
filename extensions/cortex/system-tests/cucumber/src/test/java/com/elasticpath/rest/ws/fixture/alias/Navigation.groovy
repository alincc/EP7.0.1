package com.elasticpath.rest.ws.fixture.alias

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

/**
 * Common navigation commands that can be used as aliases.
 */
class Navigation {
	def client

	def Navigation(client) {
		this.client = client;
		client.alias(this.&findCategory)
	}

	def findCategory(categoryName) {
		client.findElement { category ->
			category["name"] == categoryName
		}
	}
}
