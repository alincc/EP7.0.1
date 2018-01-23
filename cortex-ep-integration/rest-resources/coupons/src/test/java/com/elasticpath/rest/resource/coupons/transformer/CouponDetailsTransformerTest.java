/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.constant.CouponsConstants;
import com.elasticpath.rest.resource.coupons.impl.CouponsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Test class for {@link CouponDetailsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CouponDetailsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String COUPON_ID = "12345";
	private static final String ENCODED_COUPON_ID = Base32Util.encode(COUPON_ID);
	private static final String COUPON_CODE = "CODE";
	private static final String SOURCE_URI = "/otherResource/scope/id";
	private static final String SCOPE = "scope";
	private static final String PARENT_ID = "PARENT_ID";
	private static final String PARENT_TYPE = "PARENT_TYPE";

	@Mock
	private ResourceState<ResourceEntity> orderResourceState;

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@InjectMocks
	private CouponDetailsTransformer couponDetailsTransformer;

	private final CouponEntity entityToTransform = CouponEntity.builder()
			.withCode(COUPON_CODE)
			.withCouponId(COUPON_ID)
			.withParentId(PARENT_ID)
			.withParentType(PARENT_TYPE)
			.build();

	private final Self orderSelf = SelfFactory.createSelf(SOURCE_URI, OrdersMediaTypes.ORDER.id());


	@Before
	public void setUp() {
		when(couponsUriBuilderFactory.get()).thenAnswer(invocation -> new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME));

		when(orderResourceState.getSelf()).thenReturn(orderSelf);
		when(orderResourceState.getScope()).thenReturn(SCOPE);
	}

	@Test
	public void testRepresentationIsCorrectWhenTransformingCouponEntityToRepresentation() {
		Self expectedSelf = buildExpectedSelf();

		ResourceState<CouponEntity> couponResourceState =
				couponDetailsTransformer.transform(entityToTransform, orderResourceState);

		ResourceState<CouponEntity> expectedResourceState = ResourceState.Builder.create(entityToTransform)
				.withScope(SCOPE)
				.withSelf(expectedSelf)
				.withResourceInfo(ResourceInfo.builder()
					.withMaxAge(CouponsConstants.PURCHASE_COUPON_MAX_AGE)
					.build())
				.build();
		assertEquals("Representations should match.", expectedResourceState, couponResourceState);
		assertEquals(COUPON_CODE, couponResourceState.getEntity().getCode());
		assertEquals(COUPON_ID, couponResourceState.getEntity().getCouponId());
		assertEquals(PARENT_TYPE, couponResourceState.getEntity().getParentType());
		assertEquals(PARENT_ID, couponResourceState.getEntity().getParentId());
	}

	private Self buildExpectedSelf() {
		String expectedSelfUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
										.setSourceUri(SOURCE_URI)
										.setCouponId(ENCODED_COUPON_ID)
										.build();
		return SelfFactory.createSelf(expectedSelfUri);
	}
}
