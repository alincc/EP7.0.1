/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Tests for the {@link PaymentMeansResourceOperatorImpl} class.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PaymentMeansResourceOperatorImplTest {

	private static final String SCOPE = "scope";
	private static final String PURCHASE_ID = "purchase ID";
	private static final String PARENT_URI = "/parentUri";
	private static final String PAYMENT_ID = "payment id";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(PARENT_URI);

	@Mock
	private PaymentMeansLookup mockPaymentMeansLookup;

	@InjectMocks
	private PaymentMeansResourceOperatorImpl classUnderTest;


	/**
	 * Test happy path.
	 */
	@Test
	public void testReadPaymentMeans() {
		Self parentSelf = SelfFactory.createSelf(PARENT_URI, PurchasesMediaTypes.PURCHASE.id());
		ResourceState<PurchaseEntity> parentRepresentation = ResourceState.Builder
				.create(PurchaseEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.build())
				.withSelf(parentSelf)
				.withScope(SCOPE)
				.build();
		ResourceState<PaymentMeansEntity> expectedPaymentMeansRepresentation = ResourceState.Builder
				.create(PaymentMeansEntity.builder().build())
				.build();
		when(mockPaymentMeansLookup.findPaymentMeansById(SCOPE, PARENT_URI, PURCHASE_ID, PAYMENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(expectedPaymentMeansRepresentation));

		OperationResult result = classUnderTest.processPaymentMeansRead(parentRepresentation, PAYMENT_ID, READ);

		assertTrue(result.isSuccessful());
		assertEquals("wrong expectedPaymentMeansRepresentation returned", expectedPaymentMeansRepresentation, result.getResourceState());
	}

	/**
	 * Test lookup returned failure.
	 */
	@Test
	public void testReadPaymentMeansWithBadLookup() {

		Self parentSelf = SelfFactory.createSelf(PARENT_URI, PurchasesMediaTypes.PURCHASE.id());
		ResourceState<PurchaseEntity> parentRepresentation = ResourceState.Builder
				.create(PurchaseEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.build())
				.withSelf(parentSelf)
				.withScope(SCOPE)
				.build();
		when(mockPaymentMeansLookup.findPaymentMeansById(SCOPE, PARENT_URI, PURCHASE_ID, PAYMENT_ID))
				.thenReturn(ExecutionResultFactory.<ResourceState<PaymentMeansEntity>>createNotFound("Not Found"));

		OperationResult result = classUnderTest.processPaymentMeansRead(parentRepresentation, PAYMENT_ID, READ);

		assertFalse(result.isSuccessful());
	}

	@Test
	public void testReadPaymentMeansList() {
		Self parentSelf = SelfFactory.createSelf(PARENT_URI, PurchasesMediaTypes.PURCHASE.id());
		ResourceState<PurchaseEntity> parentRepresentation = ResourceState.Builder
				.create(PurchaseEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.build())
				.withSelf(parentSelf)
				.withScope(SCOPE)
				.build();
		ResourceState<LinksEntity> expectedPaymentMeansRepresentation = ResourceState.Builder
				.create(LinksEntity.builder().build())
				.build();
		when(mockPaymentMeansLookup.findPaymentMeansIdsByPurchaseId(SCOPE, PURCHASE_ID, PARENT_URI))
				.thenReturn(ExecutionResultFactory.createReadOK(expectedPaymentMeansRepresentation));

		OperationResult result = classUnderTest.processListOfPaymentMeansRead(parentRepresentation, READ);

		assertTrue(result.isSuccessful());
		assertEquals("wrong expectedPaymentMeansRepresentation returned", expectedPaymentMeansRepresentation, result.getResourceState());
	}

	/**
	 * Test lookup returned failure.
	 */
	@Test
	public void testReadPaymentMeansListWithBadLookup() {
		Self parentSelf = SelfFactory.createSelf(PARENT_URI, PurchasesMediaTypes.PURCHASE.id());
		ResourceState<PurchaseEntity> parentRepresentation = ResourceState.Builder
				.create(PurchaseEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.build())
				.withSelf(parentSelf)
				.withScope(SCOPE)
				.build();
		when(mockPaymentMeansLookup.findPaymentMeansIdsByPurchaseId(SCOPE, PURCHASE_ID, PARENT_URI))
				.thenReturn(ExecutionResultFactory.<ResourceState<LinksEntity>>createNotFound("Not Found"));

		OperationResult result = classUnderTest.processListOfPaymentMeansRead(parentRepresentation, READ);

		assertFalse(result.isSuccessful());
	}
}
