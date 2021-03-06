package com.elasticpath.cortex.dce.shippingDetails

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Given(~'^I select a shipping option (.+)$') { String optionName ->
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.shippingoptioninfo()
			.selector()
			.findChoice {
		shippingoption ->
			def description = shippingoption.description()
			description["name"] == optionName
	}
	.stopIfFailure()
}

Then(~'^the shipping cost has fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

	client.description()
	def listCostElement = client.body.cost[0]
	assertCost(listCostElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

