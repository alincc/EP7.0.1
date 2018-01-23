/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.purchases.addresses.integration.BillingAddressLookupStrategy;
import com.elasticpath.rest.resource.purchases.integration.PurchaseLookupStrategy;
import com.elasticpath.rest.resource.purchases.integration.PurchaseWriterStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemLookupStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.purchases.paymentmeans.integration.PaymentMeansLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests purchase bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class PurchasesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "purchaseLookupStrategy")
	private PurchaseLookupStrategy purchaseLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseLineItemLookupStrategy")
	private PurchaseLineItemLookupStrategy purchaseLineItemLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseWriterStrategy")
	private PurchaseWriterStrategy purchaseWriterStrategy;

	@ReplaceWithMock(beanName = "billingAddressLookupStrategy")
	private BillingAddressLookupStrategy billingAddressLookupStrategy;

	@ReplaceWithMock(beanName = "paymentMeansLookupStrategy")
	private PaymentMeansLookupStrategy paymentMeansLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseLineItemOptionsLookupStrategy")
	private PurchaseLineItemOptionsLookupStrategy purchaseLineItemOptionsLookupStrategy;
}
