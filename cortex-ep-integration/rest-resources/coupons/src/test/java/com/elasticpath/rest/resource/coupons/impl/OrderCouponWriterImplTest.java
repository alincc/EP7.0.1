/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponWriterStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests for {@link OrderCouponWriterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderCouponWriterImplTest {

	private static final String COUPON_ID = "abcd=";

	private static final String DECODED_COUPON_ID = Base32Util.decode(COUPON_ID);

	private static final String SELF_URI = "/coupons/resourceUri/id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderCouponWriterStrategy mockCouponWriterStrategy;

	@Mock
	private TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> mockCouponDetailsTransformer;

	@InjectMocks
	private OrderCouponWriterImpl writer;

	@Mock
	private ResourceState<OrderEntity> orderResourceState;

	@Mock
	private OrderEntity mockOrderEntity;

	@Mock
	private CouponEntity mockCouponEntity;

	@Mock
	private ResourceState<CouponEntity> couponResourceState;

	private final Self couponSelf = SelfFactory.createSelf(SELF_URI, COUPON.id());

	@Before
	public void setUp() {
		when(mockCouponDetailsTransformer.transform(mockCouponEntity, orderResourceState)).thenReturn(couponResourceState);
		when(couponResourceState.getSelf()).thenReturn(couponSelf);
		when(couponResourceState.getEntity()).thenReturn(mockCouponEntity);
		when(orderResourceState.getEntity()).thenReturn(mockOrderEntity);
	}

	@Test
	public void testOnSuccessfulDeleteADeleteOKIsReturned() {
		when(mockCouponWriterStrategy.deleteCouponForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
			.thenReturn(ExecutionResultFactory.<Void>createDeleteOK());

		ExecutionResult<?> result = writer.deleteCouponFromOrder(orderResourceState, COUPON_ID);

		assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void testOnAnUnsuccessfulDeleteAServerErrorIsReturned() {
		when(mockCouponWriterStrategy.deleteCouponForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
			.thenReturn(ExecutionResultFactory.<Void>createServerError(""));

		ExecutionResult<?> result = writer.deleteCouponFromOrder(orderResourceState, COUPON_ID);

		assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testDeletingANonExistentCouponReturnsANotFound() {
		when(mockCouponWriterStrategy.deleteCouponForOrder(anyString(), anyString(), eq(DECODED_COUPON_ID)))
		.thenReturn(ExecutionResultFactory.<Void>createNotFound());

		ExecutionResult<?> result = writer.deleteCouponFromOrder(orderResourceState, COUPON_ID);

		assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testOnSuccessfulCreateThenCreateOKIsReturned() {

		when(mockCouponWriterStrategy.createCouponForOrder(anyString(), anyString(), eq(mockCouponEntity)))
			.thenReturn(ExecutionResultFactory.createCreateOKWithData(mockCouponEntity, false));

		ExecutionResult<?> result = writer.createCouponForOrder(orderResourceState, couponResourceState);

		assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void testOnSuccessfulExistingCreateThenReadOKIsReturned() {

		when(mockCouponWriterStrategy.createCouponForOrder(anyString(), anyString(), eq(mockCouponEntity)))
			.thenReturn(ExecutionResultFactory.createCreateOKWithData(mockCouponEntity, true));

		ExecutionResult<?> result = writer.createCouponForOrder(orderResourceState, couponResourceState);

		assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK);
	}
	@Test
	public void testCouponNotFoundInSystemWhenApplyingThenStateConflictIsReturned() {
		when(mockCouponWriterStrategy.createCouponForOrder(anyString(), anyString(), eq(mockCouponEntity)))
			.thenReturn(ExecutionResultFactory.<CouponEntity>createStateFailure(""));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		writer.createCouponForOrder(orderResourceState, couponResourceState);
	}

	@Test
	public void testOrderNotFoundInSystemWhenApplyingThenNotFoundIsReturned() {
		when(mockCouponWriterStrategy.createCouponForOrder(anyString(), anyString(), eq(mockCouponEntity)))
			.thenReturn(ExecutionResultFactory.<CouponEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		writer.createCouponForOrder(orderResourceState, couponResourceState);
	}

	@Test
	public void testOnAnUnsuccessfulCreateAServerErrorIsReturned() {
		when(mockCouponWriterStrategy.createCouponForOrder(anyString(), anyString(), eq(mockCouponEntity)))
		.thenReturn(ExecutionResultFactory.<CouponEntity>createServerError(""));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		writer.createCouponForOrder(orderResourceState, couponResourceState);
	}
}
