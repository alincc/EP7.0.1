/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
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
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ResourceLinkUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ReadPurchaseFormForOrderResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPurchaseFormForOrderResourceOperatorTest {

	private static final String TESTURI = "/testuri";
	private static final String URI_TO_PAYMENT_METHOD = "/uri_to_payment_method";
	private static final String SCOPE = "rockjam";
	private static final String PURCHASE_RESOURCE_PATH_NAME = "purchases";
	private static final String ORDER_RESOURCE_PATH_NAME = "orders";
	private static final String EXISTING_ORDER_ID = "34456";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(TESTURI);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private PurchaseLookup mockPurchaseLookup;

	private ReadPurchaseFormForOrderResourceOperator classUnderTest;

	@Before
	public void setUp() {
		classUnderTest =
				new ReadPurchaseFormForOrderResourceOperator(PURCHASE_RESOURCE_PATH_NAME, new PurchasesResourceLinkFactory(), mockPurchaseLookup);
	}
	/**
	 * Tests that a purchase form contains a submit link if it is for an order that is not purchasable.
	 */
	@Test
	public void testReadWithPurchasableOrder() {
		when(mockPurchaseLookup.isOrderPurchasable(anyString(), anyString())).thenReturn(ExecutionResultFactory.createReadOK(true));

		OperationResult result = classUnderTest.processPurchaseFormRead(createFreeOrder(), READ);

		assertTrue("The result should be a success.", result.isSuccessful());

		Collection<ResourceLink> formLinks = ResourceLinkUtil.findLinksByRel(result.getResourceState(), PurchaseResourceRels.SUBMIT_ORDER_ACTION_REL);

		assertThat("The form representation should contain a submitorderaction link.", formLinks, Matchers.hasSize(1));
	}

	/**
	 * Tests that a purchase form does not contain a submit link if it is for an order that is not purchasable.
	 */
	@Test
	public void testReadWithNonPurchasableOrder() {
		when(mockPurchaseLookup.isOrderPurchasable(anyString(), anyString())).thenReturn(ExecutionResultFactory.createReadOK(false));

		OperationResult result = classUnderTest.processPurchaseFormRead(createFreeOrder(), READ);

		assertTrue("The result should be a success.", result.isSuccessful());
		Collection<ResourceLink> formLinks = ResourceLinkUtil.findLinksByRel(result.getResourceState(), PurchaseResourceRels.SUBMIT_ORDER_ACTION_REL);

		assertThat("The form representation should not contain a submitorderaction link.", formLinks, Matchers.empty());
	}

	/**
	 * Tests that a purchase form fails if the lookup fails.
	 */
	@Test
	public void testReadWithFailedIsOrderPurchasableLookup() {
		ExecutionResult<Boolean> createStateFailure = ExecutionResultFactory.createStateFailure("");

		when(mockPurchaseLookup.isOrderPurchasable(anyString(), anyString())).thenReturn(createStateFailure);

		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		OperationResult result = classUnderTest.processPurchaseFormRead(createFreeOrder(), READ);

		assertEquals("The failure result message should be the same as createStateFailure.",
				createStateFailure.getErrorMessage(), result.getMessage());
	}

	/**
	 * Test read purchase form with order that has no links.
	 */
	@Test
	public void testReadWithNoLinks() {
		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, null)).thenReturn(ExecutionResultFactory.createReadOK(true));

		OperationResult result = classUnderTest.processPurchaseFormRead(createOrderRep(), READ);

		assertNotNull("Purchase representation should not be null.", result.getResourceState());
		String expectedSelfUri = URIUtil.format(PURCHASE_RESOURCE_PATH_NAME, ORDER_RESOURCE_PATH_NAME, SCOPE, EXISTING_ORDER_ID, Form.URI_PART);
		ResourceLink expectedOrderLink = ResourceLinkFactory.createUriRel(URIUtil.format(PURCHASE_RESOURCE_PATH_NAME,
				ORDER_RESOURCE_PATH_NAME, SCOPE, EXISTING_ORDER_ID), PurchaseResourceRels.SUBMIT_ORDER_ACTION_REL);
		assertResourceState(result.getResourceState())
				.self(SelfFactory.createSelf(expectedSelfUri))
				.containsLink(expectedOrderLink);
	}

	/**
	 * Test read purchase form with order that has a needinfo link.
	 */
	@Test
	public void testReadWithNeedInfoLink() {
		ResourceLink expectedNeedInfoLink = ResourceLinkFactory.createUriRel(TESTURI, NeedInfoRels.NEEDINFO);
		ResourceState<OrderEntity> order = ResourceState.builderFrom(createOrderRep())
				.addingLinks(expectedNeedInfoLink)
				.build();
		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, null)).thenReturn(ExecutionResultFactory.createReadOK(true));

		OperationResult result = classUnderTest.processPurchaseFormRead(order, READ);

		String expectedSelfUri = URIUtil.format(PURCHASE_RESOURCE_PATH_NAME, ORDER_RESOURCE_PATH_NAME, SCOPE, EXISTING_ORDER_ID, Form.URI_PART);
		assertNotNull("Purchase representation should not be null.", result.getResourceState());
		assertResourceState(result.getResourceState())
				.self(SelfFactory.createSelf(expectedSelfUri))
				.linkCount(1)
				.containsLink(expectedNeedInfoLink);
	}

	/**
	 * Test read purchase form with order that has a payment method link.
	 */
	@Test
	public void testReadWithPaymentMethodLink() {
		when(mockPurchaseLookup.isOrderPurchasable(SCOPE, null)).thenReturn(ExecutionResultFactory.createReadOK(true));

		OperationResult result = classUnderTest.processPurchaseFormRead(createOrderRepWithPaymentMethod(), READ);


		assertNotNull("Purchase representation should not be null.", result.getResourceState());
		String expectedSelfUri = URIUtil.format(PURCHASE_RESOURCE_PATH_NAME, ORDER_RESOURCE_PATH_NAME, SCOPE, EXISTING_ORDER_ID, Form.URI_PART);
		ResourceLink expectedNeedInfoLink = ResourceLinkFactory.createUriRel(TESTURI, NeedInfoRels.NEEDINFO);
		assertResourceState(result.getResourceState())
				.self(SelfFactory.createSelf(expectedSelfUri))
				.linkCount(1)
				.containsLink(expectedNeedInfoLink);
	}

	private ResourceState<OrderEntity> createOrderRep() {
		String selfUri = URIUtil.format(ORDER_RESOURCE_PATH_NAME, SCOPE, EXISTING_ORDER_ID);
		Self self = SelfFactory.createSelf(selfUri, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder()
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}

	private ResourceState<OrderEntity> createOrderRepWithPaymentMethod() {
		return ResourceState.builderFrom(createOrderRep())
				.addingLinks(
						ResourceLinkFactory.createUriRel(TESTURI, NeedInfoRels.NEEDINFO),
						ResourceLinkFactory.createUriRel(URI_TO_PAYMENT_METHOD, PurchaseResourceRels.PAYMENT_INFO_REL)
				).build();
	}

	private ResourceState<OrderEntity> createFreeOrder() {
		Self self = SelfFactory.createSelf(EXISTING_ORDER_ID, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(EXISTING_ORDER_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}
}
