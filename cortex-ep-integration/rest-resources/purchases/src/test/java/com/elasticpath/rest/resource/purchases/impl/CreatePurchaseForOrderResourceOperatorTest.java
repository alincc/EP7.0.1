/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.PurchaseWriter;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.resource.purchases.constants.PurchaseStatus;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test CREATE operation of Purchases.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CreatePurchaseForOrderResourceOperatorTest {

	private static final String PURCHASE_RESOURCE = "purchases";
	private static final String ORDER_RESOURCE = "orders";
	private static final String SCOPE = "scope";
	private static final PurchaseStatus EXPECTED_STATUS = PurchaseStatus.COMPLETED;
	private static final String ORDER_ID = "1375";
	private static final String ORDER_URI = URIUtil.format(ORDER_RESOURCE, SCOPE, ORDER_ID);
	private static final String CREATED_PURCHASE_ID = "98765";
	private static final ResourceOperation CREATE = TestResourceOperationFactory.createCreate(ORDER_URI, null);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseLookup mockPurchaseLookup;
	@Mock
	private PurchaseWriter mockPurchaseWriter;

	@InjectMocks
	private CreatePurchaseForOrderResourceOperator strategy;

	/**
	 * Tests that a purchase cannot be created from an order that is not purchasable.
	 */
	@Test
	public void testPurchaseCreateWithNonPurchasableOrder() {
		ExecutionResult<Boolean> orderPurchasableFailResult = ExecutionResultFactory.createReadOK(false);

		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, ORDER_ID))
				.thenReturn(orderPurchasableFailResult);
		CreatePurchaseForOrderResourceOperator createPurchaseForOrderResourceOperator =
				createCreatePurchaseForOrderStrategy(mockPurchaseLookup, null);

		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		createPurchaseForOrderResourceOperator.processCreatePurchase(createFreeOrder(), CREATE);
	}

	/**
	 * Tests that a purchase cannot be created if isOrderPurchasable is not successful.
	 */
	@Test
	public void testPurchaseCreateWithFailedIsOrderPurchasableLookup() {
		ExecutionResult<Boolean> orderPurchasableFailResult = ExecutionResultFactory.createNotFound("");

		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, ORDER_ID))
				.thenReturn(orderPurchasableFailResult);
		CreatePurchaseForOrderResourceOperator createPurchaseForOrderResourceOperator =
				createCreatePurchaseForOrderStrategy(mockPurchaseLookup, null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		OperationResult result = createPurchaseForOrderResourceOperator.processCreatePurchase(createFreeOrder(), CREATE);

		assertEquals("The failure result message should be the same as orderPurchasableFailResult.", orderPurchasableFailResult.getErrorMessage(),
				result.getMessage());
	}

	private CreatePurchaseForOrderResourceOperator createCreatePurchaseForOrderStrategy(final PurchaseLookup purchaseLookup,
			final PurchaseWriter purchaseWriter) {

		return new CreatePurchaseForOrderResourceOperator("purchases", purchaseLookup, purchaseWriter);
	}

	/**
	 * Test CREATE of purchase.
	 */
	@Test
	public void testPurchaseCreateNoPaymentRequired() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withStatus(EXPECTED_STATUS.name())
				.build();
		String purchaseUri = URIUtil.format(PURCHASE_RESOURCE, SCOPE, CREATED_PURCHASE_ID);
		ResourceState<PurchaseEntity> expectedPurchase = ResourceState.Builder
				.create(purchaseEntity)
				.withSelf(SelfFactory.createSelf(purchaseUri, PurchasesMediaTypes.PURCHASE.id()))
				.build();
		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));
		when(mockPurchaseLookup.findPurchaseById(SCOPE, CREATED_PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(expectedPurchase));
		when(mockPurchaseWriter.createPurchase(SCOPE, ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(CREATED_PURCHASE_ID));

		OperationResult result = strategy.processCreatePurchase(createFreeOrder(), CREATE);

		assertOperationResult(result).resourceStatus(ResourceStatus.CREATE_OK);
		assertNotNull(result.getResourceState());
	}

	/**
	 * Tests creating a purchase when a payment is required, and is provided.
	 */
	@Test
	public void testCreatePurchaseWithRequiredPayment() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withStatus(EXPECTED_STATUS.name())
				.build();
		String purchaseUri = URIUtil.format(PURCHASE_RESOURCE, SCOPE, CREATED_PURCHASE_ID);
		ResourceState<PurchaseEntity> expectedPurchase = ResourceState.Builder
				.create(purchaseEntity)
				.withSelf(SelfFactory.createSelf(purchaseUri, PurchasesMediaTypes.PURCHASE.id()))
				.build();
		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));
		when(mockPurchaseLookup.findPurchaseById(SCOPE, CREATED_PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(expectedPurchase));
		when(mockPurchaseWriter.createPurchase(SCOPE, ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(CREATED_PURCHASE_ID));

		OperationResult result = processPayment();

		assertOperationResult(result).resourceStatus(ResourceStatus.CREATE_OK);
		assertNotNull(result.getResourceState());
	}

	@SuppressWarnings("unchecked")
	private OperationResult processPayment() {
		ResourceState<OrderEntity> order = createOrderWithRequiredPayment();
		return strategy.processCreatePurchase(order, CREATE);
	}

	private ResourceState<OrderEntity> createFreeOrder() {
		Self self = SelfFactory.createSelf(ORDER_ID, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}

	private ResourceState<OrderEntity> createOrderWithRequiredPayment() {
		Self self = SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id());
		ResourceLink paymentInfoLink = ResourceLinkFactory.createUriRel(URIUtil.format("paymentmethods", self.getUri()),
				PurchaseResourceRels.PAYMENT_INFO_REL);
		return ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.addingLinks(paymentInfoLink)
				.build();
	}
}
