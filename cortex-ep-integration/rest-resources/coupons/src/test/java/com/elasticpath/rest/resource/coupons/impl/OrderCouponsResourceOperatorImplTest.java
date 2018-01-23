/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.coupons.OrderCouponWriter;
import com.elasticpath.rest.resource.coupons.OrderCouponsLookup;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;

/**
 * Testing for correct invocation of methods based on types.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderCouponsResourceOperatorImplTest {
	private static final String COUPON_ID = "COUPON_ID";
	private static final String TEST_URI = "/testuri";

	@Mock
	private OrderCouponWriter mockOrderCouponWriter;

	@Mock
	private OrderCouponsLookup mockOrderCouponsLookup;

	@Mock
	private ResourceState<CouponEntity> mockCoupon;

	@Mock
	private ResourceState<InfoEntity> mockInfo;

	@Mock
	private ResourceState<OrderEntity> mockResourceState;

	@Mock
	private ResourceState<OrderEntity> couponForm;

	@InjectMocks
	private OrderCouponsResourceOperatorImpl resourceOperator;


	@Test
	public void testProcessReadCouponDetailsReturnsReadOkay() {
		arrangeASuccessfulOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForOrder(mockResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result)
				.resourceState(mockCoupon)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadCouponDetailsReturnsNotFound() {
		arrangeANotFoundOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForOrder(mockResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadCouponDetailsReturnsServerError() {
		arrangeAServerErrorOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForOrder(mockResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessReadCouponInfoReturnsReadOkay() {
		arrangeASuccessfulOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponInfoForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result)
			.resourceState(mockInfo)
			.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadCouponInfoReturnsNotFound() {
		arrangeANotFoundOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponInfoForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadCouponInfoReturnsServerError() {
		arrangeAServerErrorOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponInfoForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessDeleteCouponWhenDeleteOkay() {
		arrangeASuccessfulOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processDeleteCouponFromOrder(mockResourceState, COUPON_ID, createDeleteOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void testProcessDeleteCouponWhenNotFound() {
		arrangeANotFoundOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processDeleteCouponFromOrder(mockResourceState, COUPON_ID, createDeleteOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessDeleteCouponWhenServerError() {
		arrangeAServerErrorOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processDeleteCouponFromOrder(mockResourceState, COUPON_ID, createDeleteOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessReadCouponFormWhenReadOkay() {
		arrangeASuccessfulOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponFormForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result)
				.resourceState(mockCoupon)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadCouponFormWhenNotFound() {
		arrangeANotFoundOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponFormForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadCouponFormWhenServerError() {
		arrangeAServerErrorOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processReadCouponFormForOrder(mockResourceState, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessCreateCouponWhenCreateOkay() {
		arrangeASuccessfulOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processCreateCouponForOrder(mockResourceState,
				createCreateOperation(couponForm));

		assertOperationResult(result).resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void testProcessCreateCouponWhenNotFound() {
		arrangeANotFoundOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processCreateCouponForOrder(mockResourceState,
				createCreateOperation(couponForm));

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessCreateCouponWhenServerError() {
		arrangeAServerErrorOrderResult(OrdersMediaTypes.ORDER.id());

		OperationResult result = resourceOperator.processCreateCouponForOrder(mockResourceState,
				createCreateOperation(couponForm));

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private ResourceOperation createCreateOperation(final ResourceState<OrderEntity> couponForm) {
		return TestResourceOperationFactory.createCreate(TEST_URI, couponForm);
	}

	private ResourceOperation createReadOperation() {
		return TestResourceOperationFactory.createRead(TEST_URI);
	}

	private ResourceOperation createDeleteOperation() {
		return TestResourceOperationFactory.createDelete(TEST_URI);
	}

	private void arrangeASuccessfulOrderResult(final String type) {
		arrangeLookupsAndWritersToReturnReadOkays();

		mockReadFromOrder(type);
	}

	private void arrangeANotFoundOrderResult(final String type) {
		arrangeLookupsAndWritersToReturnNotFound();

		mockReadFromOrder(type);
	}

	private void arrangeAServerErrorOrderResult(final String type) {
		arrangeLookupsAndWritersToReturnServerError();

		mockReadFromOrder(type);
	}

	private void arrangeLookupsAndWritersToReturnReadOkays() {

		when(mockOrderCouponsLookup.getCouponDetailsForOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCoupon));
		when(mockOrderCouponsLookup.getCouponFormForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCoupon));
		when(mockOrderCouponsLookup.getCouponInfoForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockInfo));
		when(mockOrderCouponWriter.createCouponForOrder(Matchers.<ResourceState<OrderEntity>>any(), Matchers.<ResourceState<CouponEntity>>any()))
				.thenReturn(ExecutionResultFactory.createCreateOK(TEST_URI, false));
		when(mockOrderCouponWriter.deleteCouponFromOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.<Void> createDeleteOK());
	}

	private void arrangeLookupsAndWritersToReturnNotFound() {
		when(mockOrderCouponsLookup.getCouponDetailsForOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createNotFound());
		when(mockOrderCouponsLookup.getCouponFormForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createNotFound());
		when(mockOrderCouponsLookup.getCouponInfoForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<InfoEntity>>createNotFound());
		when(mockOrderCouponWriter.createCouponForOrder(Matchers.<ResourceState<OrderEntity>>any(), Matchers.<ResourceState<CouponEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<ResourceEntity>>createNotFound());
		when(mockOrderCouponWriter.deleteCouponFromOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.<Void> createNotFound());
	}

	private void arrangeLookupsAndWritersToReturnServerError() {
		String testServerMsg = "Test server error";
		when(mockOrderCouponsLookup.getCouponDetailsForOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createServerError(testServerMsg));
		when(mockOrderCouponsLookup.getCouponFormForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createServerError(testServerMsg));
		when(mockOrderCouponsLookup.getCouponInfoForOrder(Matchers.<ResourceState<OrderEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<InfoEntity>>createServerError(testServerMsg));
		when(mockOrderCouponWriter.createCouponForOrder(Matchers.<ResourceState<OrderEntity>>any(), Matchers.<ResourceState<CouponEntity>>any()))
				.thenReturn(ExecutionResultFactory.<ResourceState<ResourceEntity>> createServerError(testServerMsg));
		when(mockOrderCouponWriter.deleteCouponFromOrder(Matchers.<ResourceState<OrderEntity>>any(), anyString()))
				.thenReturn(ExecutionResultFactory.<Void> createServerError(testServerMsg));
	}

	private void mockReadFromOrder(final String type) {
		Self self = mock(Self.class);
		when(self.getType()).thenReturn(type);

		when(mockResourceState.getSelf()).thenReturn(self);
	}

}
