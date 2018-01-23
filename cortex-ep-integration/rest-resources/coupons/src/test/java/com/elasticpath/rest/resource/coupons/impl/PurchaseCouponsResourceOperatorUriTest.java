/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.purchases.PurchasesMediaTypes.PURCHASE;
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
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PurchaseCouponsResourceOperatorImpl.class})
public final class PurchaseCouponsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String ENCODED_COUPON_ID = Base32Util.encode("couponId");
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private PurchaseCouponsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {

		mediaType(PURCHASE);
	}

	@Test
	public void testReadCouponsForPurchase() {

		String readCouponForOtherUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.build();
		ResourceOperation operation = createRead(readCouponForOtherUri);
		readOther(operation);
		when(resourceOperator.processReadCouponsForPurchase(anyPurchaseEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCouponsForPurchase(anyPurchaseEntity(), anyResourceOperation());
	}

	@Test
	public void testReadCouponFromPurchaseWithId() {

		String couponsUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setCouponId(ENCODED_COUPON_ID)
				.build();
		ResourceOperation operation = createRead(couponsUri);
		readOther(operation);
		when(resourceOperator.processReadCouponDetailsForPurchase(anyPurchaseEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCouponDetailsForPurchase(anyPurchaseEntity(), anyString(), anyResourceOperation());
	}

	private static ResourceState<PurchaseEntity> anyPurchaseEntity() {

		return Mockito.any();
	}
}
