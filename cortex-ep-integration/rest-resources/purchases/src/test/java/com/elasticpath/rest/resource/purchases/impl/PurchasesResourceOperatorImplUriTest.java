/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.lineitems.impl.PurchaseLineItemsOperatorImpl;
import com.elasticpath.rest.resource.purchases.paymentmeans.impl.PaymentMeansResourceOperatorImpl;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link PurchasesResourceOperatorImplUriTest}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PurchasesResourceOperatorImpl.class,
		PaymentMeansResourceOperatorImpl.class,
		PurchaseLineItemsOperatorImpl.class })
public final class PurchasesResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String PURCHASE_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String LINE_ITEM_ID = "lsjbnwly6wduxulahkcozuc2aa=";
	private static final String SCOPE = "rockjam";
	private static final String PURCHASES = "purchases";

	@Spy
	private final PurchasesResourceOperatorImpl mockPurchaseResourceOperator = new PurchasesResourceOperatorImpl(null, null);
	@Spy
	private final PurchaseLineItemsOperatorImpl mockPurchaseLineItemOperator = new PurchaseLineItemsOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests {@link PurchasesResourceOperatorImpl#processReadPurchaseList(String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessReadPurchaseList() {
		String uri = URIUtil.format(PURCHASES, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(mockPurchaseResourceOperator)
				.processReadPurchaseList(SCOPE, operation);

		dispatch(operation);

		verify(mockPurchaseResourceOperator).processReadPurchaseList(SCOPE, operation);
	}

	/**
	 * Tests {@link PurchasesResourceOperatorImpl#processPurchaseRead(String, String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessPurchaseRead() {
		String uri = URIUtil.format(PURCHASES, SCOPE, PURCHASE_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(mockPurchaseResourceOperator)
				.processPurchaseRead(SCOPE, PURCHASE_ID, operation);

		dispatch(operation);

		verify(mockPurchaseResourceOperator).processPurchaseRead(SCOPE, PURCHASE_ID, operation);
	}

	/**
	 * Tests {@link PurchaseLineItemsOperatorImpl#processReadLineItemList(String, String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessLineItemList() {
		String uri = URIUtil.format(PURCHASES, SCOPE, PURCHASE_ID, LineItems.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(mockPurchaseLineItemOperator)
				.processReadLineItemList(SCOPE, PURCHASE_ID, operation);

		dispatch(operation);

		verify(mockPurchaseLineItemOperator).processReadLineItemList(SCOPE, PURCHASE_ID, operation);
	}

	/**
	 * Tests {@link PurchaseLineItemsOperatorImpl#processReadLineItem(String, String, String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessLineItemElement() {
		String uri = URIUtil.format(PURCHASES, SCOPE, PURCHASE_ID, LineItems.URI_PART, LINE_ITEM_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(mockPurchaseLineItemOperator)
				.processReadLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID, operation);

		dispatch(operation);

		verify(mockPurchaseLineItemOperator).processReadLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, mockPurchaseResourceOperator, mockPurchaseLineItemOperator);
	}
}
