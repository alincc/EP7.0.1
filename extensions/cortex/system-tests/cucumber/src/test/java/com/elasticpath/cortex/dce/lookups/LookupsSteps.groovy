/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.lookups

import cucumber.api.DataTable

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.lookups.LookupConstants.*
import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I follow a link back to the item') { ->
	client.item()
			.stopIfFailure()
}

When(~/^I look up an item with code (.+?)$/) { String itemCode ->
	itemLookupByCode(itemCode)
}

When(~/^I (?:look up and add item|have item with) code (.+?) (?:to my|in my) cart$/) { String itemCode ->
	client.POST("/lookups/" + getCurrentScope() + "/items", [code: itemCode])
			.follow()
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()
}

When(~/^I (?:look up and add item|have item with) code (.+?) (?:to my|in my) cart with quantity (.+)$/) { String itemCode, int qty ->
	client.POST("/lookups/" + getCurrentScope() + "/items", [code: itemCode])
			.follow()
	client.addtocartform()
			.addtodefaultcartaction(quantity: qty)
			.follow()
			.stopIfFailure()
}

Then(~'I (?:have a|add an item with code|have an item with code) (.+) (?:in|to) the cart with quantity (.+) and configurable fields:$') { String itemCode, String itemQty, DataTable modifierFieldsTable ->
	Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	addConfigurableItemToCart(itemCode, itemQty, configurationFields)
}

Given(~'a registered customer (.+) with the following configured item in their cart') { String email, DataTable modifierFieldsTable ->
	Map<String, String> configurationFields = new HashMap<>(modifierFieldsTable.asMap(String, String))
	def itemcode = configurationFields.get("itemcode")
	configurationFields.remove("itemcode")
	def itemqty = configurationFields.get("itemqty")
	configurationFields.remove("itemqty")

	client.authRegisteredUserByName(DEFAULT_SCOPE, email)
			.stopIfFailure()
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	client.DELETE(client.body.self.uri)
	addConfigurableItemToCart(itemcode, itemqty, configurationFields)
}

When(~'^I change the multi sku selection by (.+) and select choice (.+)$') { String itemOption, String itemChoice ->
	client.definition()
			.options()
	client.findElement { option ->
		option[DISPLAY_NAME_FIELD] == itemOption
	}
	.selector()
			.findChoice { itemoption ->
		def description = itemoption.description()
		description[DISPLAY_NAME_FIELD] == itemChoice
	}
	.selectaction()
			.follow()
			.stopIfFailure()
}

Then(~'^the item code is (.+)$') { String itemSkuCode ->
	client.code()
	assertThat(client["code"])
			.as("Item code is not as expected")
			.isEqualTo(itemSkuCode)
}

Then(~'^I should see item name is (.+)$') { String itemName ->
	client.definition()
			.stopIfFailure()
	assertThat(client[DISPLAY_NAME_FIELD])
			.as("Item name is not as expected")
			.isEqualTo(itemName)
}

Then(~'^I should see item details shows: display name is (.+) and display value is (.+)$') { String itemDisplayName, String itemDisplayValue ->
	assertThat(client.body.details.'display-name')
			.as("Display name is not as expected")
			.isEqualTo([itemDisplayName])
	assertThat(client.body.details.'display-value')
			.as("Display value is not as expected")
			.isEqualTo([itemDisplayValue])
}

Given(~'^I retrieve the batch items lookup form$') { ->
	client.lookups(DEFAULT_SCOPE)
			.batchitemslookupform()
			.stopIfFailure()
}

When(~'^I submit a batch of codes$') { ->
	client.POST(LOOKUP_BATCH_ITEMS_ACTION_URI, [codes: ITEM_CODES])
			.follow()
			.stopIfFailure()
}

Then(~'^I find a batch of items$') { ->
	def items = client.body.links.findAll {
		link ->
			link.rel == "element"
	}
	assertThat(items).size()
			.as("Number of elements is not as expected")
			.isEqualTo(ITEM_CODES.size())
}

When(~'I submit the invalid item uri (.+)$') { String uri ->
	client.GET(uri)
			.stopIfFailure()
}


public static void itemLookupByCode(final String itemCode) {
	client.GET("/")
			.navigations()
	def store = client.body.self.uri.toString().split("/navigations/")[1]

	client.POST("/lookups/$store/items", [code: itemCode])
			.follow()
			.stopIfFailure()
}

public static String getCurrentScope() {
	client.GET("/")
			.defaultcart()
	return client.body.self.uri.toString().split("/")[2]
}

public static addConfigurableItemToCart(String itemCode, String itemQty, Map<String, String> configurationFields) {
	client.POST("/lookups/" + getCurrentScope() + "/items", [code: itemCode])
			.follow()
	client.addtocartform()
	client.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}