/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.rates.integration.CartLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.integration.ItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.integration.PurchaseLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests purchase bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class RatesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "cartLineItemRateLookupStrategy")
	private CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy;

	@ReplaceWithMock(beanName = "itemRateLookupStrategy")
	private ItemRateLookupStrategy itemRateLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseLineItemRateLookupStrategy")
	private PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookupStrategy;
}
