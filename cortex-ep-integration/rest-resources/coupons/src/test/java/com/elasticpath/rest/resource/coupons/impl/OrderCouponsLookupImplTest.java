/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests for {@link OrderCouponsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderCouponsLookupImplTest {

	private static final String DECODED_COUPON_ID = "testcouponid";
	private static final String COUPON_ID = Base32Util.encode(DECODED_COUPON_ID);
	private static final String ORDER_URI = "/order/uri";
	private static final String DECODED_ORDER_ID = "ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderCouponsLookupStrategy mockCouponsLookupStrategy;

	@Mock
	private TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> mockCouponDetailsTransformer;

	@Mock
	private TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> mockCouponFormTransformer;

	@Mock
	private TransformRfoToResourceState<InfoEntity, Iterable<String>, OrderEntity> mockCouponInfoTransformer;

	private final CouponEntity couponEntity = CouponEntity.builder()
			.build();

	@Mock
	private ResourceState<CouponEntity> couponEntityResourceState;

	@Mock
	private OrderEntity orderEntity;

	@Mock
	private ResourceState<OrderEntity> orderRepresentation;

	@Mock
	private ResourceState<InfoEntity> infoRepresentation;

	private OrderCouponsLookupImpl lookup;

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		Self orderSelf = mock(Self.class);
		when(orderSelf.getUri()).thenReturn(ORDER_URI);
		when(orderSelf.getType()).thenReturn(OrdersMediaTypes.ORDER.id());
		when(orderRepresentation.getSelf()).thenReturn(orderSelf);
		when(orderRepresentation.getEntity()).thenReturn(orderEntity);
		when(couponEntityResourceState.getEntity()).thenReturn(couponEntity);
		// Cannot use injectMocks. Bug with Java 7 and Mockito injecting same objects
		lookup = new OrderCouponsLookupImpl(mockCouponsLookupStrategy,
					mockCouponDetailsTransformer, mockCouponFormTransformer, mockCouponInfoTransformer);
		when(orderEntity.getOrderId()).thenReturn(ORDER_ID);
	}

	@Test
	public void testDetailsLookupReturnsCouponRepresentationOnSuccess() {
		when(mockCouponsLookupStrategy.getCouponDetailsForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
			.thenReturn(ExecutionResultFactory.createReadOK(couponEntity));
		ArgumentCaptor<CouponEntity> actualEnhancedEntity = ArgumentCaptor.forClass(CouponEntity.class);
		when(mockCouponDetailsTransformer.transform(actualEnhancedEntity.capture(), eq(orderRepresentation)))
			.thenReturn(couponEntityResourceState);

		ExecutionResult<ResourceState<CouponEntity>> result = lookup.getCouponDetailsForOrder(orderRepresentation, COUPON_ID);

		assertExecutionResult(result)
			.isSuccessful()
			.data(couponEntityResourceState);

		assertEquals(DECODED_ORDER_ID, actualEnhancedEntity.getValue().getParentId());
		assertEquals(OrdersMediaTypes.ORDER.id(), actualEnhancedEntity.getValue().getParentType());
	}

	@Test
	public void testDetailsLookupReturnsFailureResultOnServerFailure() {
		when(mockCouponsLookupStrategy.getCouponDetailsForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
			.thenReturn(ExecutionResultFactory.<CouponEntity> createServerError("Test error"));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lookup.getCouponDetailsForOrder(orderRepresentation, COUPON_ID);
	}

	@Test
	public void testDetailsLookupReturnsFailureResultOnLookupFailure() {
		when(mockCouponsLookupStrategy.getCouponDetailsForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
			.thenReturn(ExecutionResultFactory.<CouponEntity> createNotFound("Test error"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getCouponDetailsForOrder(orderRepresentation, COUPON_ID);
	}


	@Test
	public void testCouponInfoLookupReturnsSuccessfullyWithReturnData() {
		Collection<String> returnData = Collections.singletonList(DECODED_COUPON_ID);
		when(mockCouponInfoTransformer.transform(anyCollectionOf(String.class), anyOrder()))
			.thenReturn(infoRepresentation);
		when(mockCouponsLookupStrategy.findCouponIdsForOrder(anyString(), anyString()))
			.thenReturn(ExecutionResultFactory.createReadOK(returnData));

		ExecutionResult<ResourceState<InfoEntity>> result = lookup.getCouponInfoForOrder(orderRepresentation);

		assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK)
			.data(infoRepresentation);
	}

	@Test
	public void testCouponInfoLookupReturnsFailureResultOnServerFailure() {
		when(mockCouponsLookupStrategy.findCouponIdsForOrder(anyString(), anyString()))
			.thenReturn(ExecutionResultFactory.<Collection<String>>createServerError("Test error"));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lookup.getCouponInfoForOrder(orderRepresentation);
	}

	@Test
	public void testFormsLookupReturnsCouponRepresentationOnSuccess() {
		when(mockCouponFormTransformer.transform(any(CouponEntity.class), eq(orderRepresentation)))
			.thenReturn(couponEntityResourceState);

		ExecutionResult<ResourceState<CouponEntity>> result = lookup.getCouponFormForOrder(orderRepresentation);

		assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK)
			.data(couponEntityResourceState);
	}

	private ResourceState<OrderEntity> anyOrder() {
		return any();
	}
}
