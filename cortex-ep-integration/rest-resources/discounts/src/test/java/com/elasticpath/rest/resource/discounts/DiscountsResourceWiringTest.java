/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.discounts.integration.CartDiscountsLookupStrategy;
import com.elasticpath.rest.resource.discounts.integration.PurchaseDiscountsLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Tests promotions bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class DiscountsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "cartDiscountsLookupStrategy")
	private CartDiscountsLookupStrategy cartDiscountsLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseDiscountsLookupStrategy")
	private PurchaseDiscountsLookupStrategy purchaseDiscountsLookupStrategy;

	@ReplaceWithMock(beanName = "cartsUriBuilderFactory")
	private CartsUriBuilderFactory cartsUriBuilderFactory;

	@ReplaceWithMock(beanName = "purchaseUriBuilderFactory")
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;
}
