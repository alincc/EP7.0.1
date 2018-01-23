/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createCreate;
import static com.elasticpath.rest.TestResourceOperationFactory.createDelete;
import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.uri.URIUtil.format;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderCouponsResourceOperatorImpl.class})
public final class OrderCouponsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String ENCODED_COUPON_ID = Base32Util.encode("couponId");
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private OrderCouponsResourceOperatorImpl resourceOperator;

	private ResourceState nullResourceState;

	@Before
	public void setUp() {

		nullResourceState = null;

		mediaType(ORDER);
	}

	@Test
	public void testReadCouponFromOrderWithId() {

		ResourceOperation operation = createRead(format(RESOURCE_SERVER_NAME, OTHER_URI, ENCODED_COUPON_ID));
		readOther(operation);
		when(resourceOperator.processReadCouponDetailsForOrder(anyOrderEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCouponDetailsForOrder(anyOrderEntity(), anyString(), anyResourceOperation());
	}

	@Test
	public void testDeleteCouponsFromOrderWithId() {

		ResourceOperation operation = createDelete(format(RESOURCE_SERVER_NAME, OTHER_URI, ENCODED_COUPON_ID));
		readOther(operation);
		when(resourceOperator.processDeleteCouponFromOrder(anyOrderEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processDeleteCouponFromOrder(anyOrderEntity(), anyString(), anyResourceOperation());
	}

	@Test
	public void testReadCouponInfoFromOrder() {

		String couponInfoUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setInfoUri()
				.build();
		ResourceOperation operation = createRead(couponInfoUri);
		readOther(operation);
		when(resourceOperator.processReadCouponInfoForOrder(anyOrderEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCouponInfoForOrder(anyOrderEntity(), anyResourceOperation());
	}

	@Test
	public void testReadCouponFormFromOrder() {

		String couponFormUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setFormUri()
				.build();
		ResourceOperation operation = createRead(couponFormUri);
		readOther(operation);
		when(resourceOperator.processReadCouponFormForOrder(anyOrderEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCouponFormForOrder(anyOrderEntity(), anyResourceOperation());
	}

	@Test
	public void testCreateCouponForOrder() {

		String createCouponUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.build();
		ResourceOperation operation = createCreate(createCouponUri, nullResourceState);
		readOther(operation);
		when(resourceOperator.processCreateCouponForOrder(anyOrderEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processCreateCouponForOrder(anyOrderEntity(), anyResourceOperation());
	}

	private static ResourceState<OrderEntity> anyOrderEntity() {

		return Mockito.any();
	}

}
